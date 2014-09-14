package dk.bjop.wirecuddler;

import dk.bjop.wirecuddler.util.Utils;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.util.LogColumn;
import lejos.util.NXTDataLogger;

import java.io.IOException;

/**
 * Created by bpeterse on 13-09-2014.
 */
public class PathRunner extends Thread{

    static NXTDataLogger logger = new NXTDataLogger();
    static LogColumn m1Tacho = new LogColumn("M1 tacho count", LogColumn.DT_INTEGER);
    static LogColumn function = new LogColumn("Perfect math", LogColumn.DT_INTEGER);
    static LogColumn error = new LogColumn("xxx", LogColumn.DT_INTEGER);
    static LogColumn[] columnDefs = new LogColumn[] { m1Tacho, function, error };

    NXTRegulatedMotor m;

    float speed= 5f;
    int adjustIntervalMillis = 250;
    int lookAheadMillis = 1000; // TODO Increase this to ~1100+ when we have to perform very quick accelerations/decellerations - we need an algorithm to do this automatically!

    MovementPath path = null;

    public PathRunner(NXTRegulatedMotor m) {
        this.m=m;
    }

    public void setMovementPath(MovementPath mp) {
        this.path=mp;
    }

    public void run() {
        LCD.drawString("Waiting for ", 0, 2);
        LCD.drawString("bluetooth con to", 0, 3);
        LCD.drawString("PC to log data.", 0, 4);
        LCD.drawString("Launch NXT Chart", 0, 5);
        LCD.drawString("Logger & click", 0, 6);
        LCD.drawString("the Connect btn.", 0, 7);
        NXTConnection connection = Bluetooth.waitForConnection();
        try {
            logger.startRealtimeLog(connection);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.setColumns(columnDefs);  // must be after startRealtimeLog()

        Sound.beep();

        LCD.clear();
        LCD.drawString("Press and hold", 0, 5);
        LCD.drawString("dark gray ESCAPE", 0, 6);
        LCD.drawString("button to stop.", 0, 7);

        while (!Button.ESCAPE.isDown()) {
            long loopTimeStart = System.currentTimeMillis();

            // Before each new path we reset motor state
            m.setAcceleration(0);
            m.setSpeed(m.getMaxSpeed());
            m.forward();

            if (path != null) {
                MovementPath p = path;
                path = null;
                followPath(p);
            }
            else break;
        }

        logger.stopLogging();
        Utils.println("PathRunner ending...");
    }

    private void followPath(MovementPath path) {
        boolean speedIsSet = false;

        // We track an SSE-like error of each move
        long errorSum = 0;
        int obsCount = 0;

        long pathMoveStarttime = System.currentTimeMillis();

        while (!Button.ESCAPE.isDown() && !path.isMovementFinished(System.currentTimeMillis() - pathMoveStarttime)) {
            long loopTimeStart = System.currentTimeMillis();

            if (!speedIsSet) {
                m.setAcceleration(0);
                m.setSpeed(m.getMaxSpeed());
                m.forward();
                speedIsSet=true;
            }

            int curAcc = m.getAcceleration();

            int nextPerfectPos = path.getExpectedTachoPosAtTimeT((System.currentTimeMillis() - pathMoveStarttime) + lookAheadMillis, speed);
            int currPos = m.getTachoCount();
            int perfectCurPos = path.getExpectedTachoPosAtTimeT(System.currentTimeMillis() - pathMoveStarttime, speed);
            int error = perfectCurPos - currPos;

            errorSum += Math.pow(Math.max(perfectCurPos, currPos) - Math.min(perfectCurPos, currPos),2);
            obsCount++;

            // NOTE!!! When setting the acceleration I do not time into account, but relies on the fact that acc is deg/s/s


            float errAdjustWeight = 2.5f;
            float errCorrection = 0;//Math.round(error*errAdjustWeight);
            //Utils.println(""+errCorrection);
            float multiplier = 1.8f;

            if (nextPerfectPos < perfectCurPos) {
                m.backward();

                int diff = nextPerfectPos-currPos;
                if (m.getRotationSpeed() < diff) {
                    //Utils.println("BACKWARD: Setting speed to ZERO! (RS: " +m1.getRotationSpeed()+" DIFF: " + diff + " ACC: " + (diff-m1.getRotationSpeed()) + ")");
                    m.setAcceleration(Math.round(((m.getRotationSpeed()-diff )*multiplier)+errCorrection));
                    m.setSpeed(1);
                }
                else {
                    //Utils.println("BACKWARD: Setting speed to MAX! (RS: " +m1.getRotationSpeed()+" DIFF: " + diff + " ACC: " + (diff-m1.getRotationSpeed()) + ")");
                    m.setAcceleration(Math.round(((diff-m.getRotationSpeed())*multiplier)+errCorrection));
                    m.setSpeed(m.getMaxSpeed());
                }
            }
            else {
                m.forward();

                int diff = nextPerfectPos-currPos;
                if (m.getRotationSpeed() > diff) {
                    //Utils.println("FORWARD: Setting speed to ZERO! (RS: " +m1.getRotationSpeed()+" DIFF: " + diff + " ACC: " + (diff-m1.getRotationSpeed()) + ")");
                    m.setAcceleration(Math.round(((m.getRotationSpeed()-diff)*multiplier)+errCorrection));
                    m.setSpeed(1);
                }
                else {
                    //Utils.println("FORWARD: Setting speed to MAX! (RS: " +m1.getRotationSpeed()+" DIFF: " + diff + " ACC: " + (diff-m1.getRotationSpeed()) + ")");
                    m.setAcceleration(Math.round(((diff-m.getRotationSpeed())*multiplier)+errCorrection));
                    m.setSpeed(m.getMaxSpeed());
                }
            }

            logger.writeLog(m.getTachoCount());
            logger.writeLog(perfectCurPos);
            logger.writeLog(error);
            logger.finishLine();

            long loopTime = System.currentTimeMillis()-loopTimeStart;
            if (loopTime > adjustIntervalMillis) Utils.println("LOOP FLAW! Looptime exceeded the interval");
            sleepx(adjustIntervalMillis - loopTime);
        }

        m.stop();
        m.flt();
        m.resetTachoCount();

        if (path.isMovementFinished(System.currentTimeMillis() - pathMoveStarttime)) {
            Utils.println("Error of the " + obsCount + " observations: " + (errorSum/obsCount));
        }
    }

    private void sleepx(long sleeptime)  {
        try {
            Thread.sleep(sleeptime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
