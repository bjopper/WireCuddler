package dk.bjop.wirecuddler;

import dk.bjop.wirecuddler.util.Utils;
import lejos.nxt.*;
import lejos.nxt.Button;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RConsole;
import lejos.util.Delay;
import lejos.util.LogColumn;
import lejos.util.NXTDataLogger;

import java.awt.*;
import java.io.IOException;

/**
 * Created by bpeterse on 21-06-2014.
 */
public class WireCuddler {
    static NXTDataLogger logger = new NXTDataLogger();
    static LogColumn m1Tacho = new LogColumn("M1 tacho count", LogColumn.DT_INTEGER);
    static LogColumn function = new LogColumn("Perfect math", LogColumn.DT_INTEGER);
    static LogColumn error = new LogColumn("xxx", LogColumn.DT_INTEGER);
    static LogColumn[] columnDefs = new LogColumn[] { m1Tacho, function, error };

    static int motorDirection=0;

    public static void main(String[] args) throws InterruptedException, IOException {
        LCD.drawString("Waiting for ", 0, 2);
        LCD.drawString("bluetooth con to", 0, 3);
        LCD.drawString("PC to log data.", 0, 4);
        LCD.drawString("Launch NXT Chart", 0, 5);
        LCD.drawString("Logger & click", 0, 6);
        LCD.drawString("the Connect btn.", 0, 7);

        // From a command line:
        // [leJOS Install Location]\bin\nxjchartinglogger.bat
        // Enter the NXT's name or address.  Be sure you have write permission
        // to the folder chosen by the utility.  If not, change the folder.
        // Click the Connect button

        // 20 motor revs = 34cm snor
        // 3:1 gearing
        // Omkreds af rulle = 34cm/(20 revs/3) = 51mm
        //  51mm snor-længde ændring = +- 1080 grader på motoraksel
        // Hastighed på 5 cm giver altså ca. 1080 grader pr sek.

        NXTConnection connection = Bluetooth.waitForConnection();
        logger.startRealtimeLog(connection);
        logger.setColumns(columnDefs);  // must be after startRealtimeLog()

        Sound.beep();
        //Button.ENTER.waitForPressAndRelease();
        LCD.clear();

        LCD.drawString("Press and hold", 0, 5);
        LCD.drawString("dark gray ESCAPE", 0, 6);
        LCD.drawString("button to stop.", 0, 7);

        //MovementPath path1 = new MovementPathImpl();
        MovementPath path1 = new MovementPathSinusImpl(24);
        //MovementPath path1 = new MovementPathSinus2Impl();

        NXTRegulatedMotor m1 = new NXTRegulatedMotor(MotorPort.A);



        doPathTest(m1, path1);


        logger.stopLogging();
    }

    private static void doPathTest(NXTRegulatedMotor m, MovementPath path) {
        boolean speedIsSet = false;
        long startTime = System.currentTimeMillis();
        float speed= 5f;

        int adjustIntervalMillis = 500;
        int lookAheadMillis = 1000;

        long errorSum = 0;
        int obsCount = 0;

        while (!Button.ESCAPE.isDown() && !path.isMovementFinished(System.currentTimeMillis() - startTime)) {
            long loopTimeStart = System.currentTimeMillis();

            if (!speedIsSet) {
                m.setAcceleration(0);
                m.setSpeed(m.getMaxSpeed());
                motorForward(m);
                speedIsSet=true;
            }

            int curAcc = m.getAcceleration();
            int nextPerfectPos = path.getExpectedTachoPosAtTimeT((System.currentTimeMillis() - startTime) + lookAheadMillis, speed);
            int currPos = m.getTachoCount();
            int perfectCurPos = path.getExpectedTachoPosAtTimeT(System.currentTimeMillis() - startTime, speed);
            int error = perfectCurPos - currPos;

            errorSum += Math.pow(Math.max(perfectCurPos, currPos) - Math.min(perfectCurPos, currPos),2);
            obsCount++;

            // NOTE!!! When setting the acceleration I do not time into account, but relies on the fact that acc is deg/s/s


            float errAdjustWeight = 2.5f;
            float errCorrection = 0;//Math.round(error*errAdjustWeight);
            //Utils.println(""+errCorrection);
            float multiplier = 1.5f;

            if (nextPerfectPos < perfectCurPos) {
                motorBackward(m);

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
                motorForward(m);

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
            Delay.msDelay(adjustIntervalMillis - (System.currentTimeMillis()-loopTimeStart));
        }

        if (path.isMovementFinished(System.currentTimeMillis() - startTime)) {
            Utils.println("Error of the " + obsCount + " observations: " + (errorSum/obsCount));
        }

    }


    //tacho-distance to target

    // Er vi over eller under target lige nu?
            /*if (currPos <= perfectCurPos) {
                // We're below or on target

                // Going up or down?
                if (nextPerfectPos > currPos) {
                    // We need to go up

                    // are we currently going up?
                    if (isForward()) {
                        // Yep, we're currently going up

                        if (m1.getSpeed())
                    }
                }

            }
            else {
                // We're above
            }*/



            /*if (nextPerfectPos > currPos) {
                int diff = nextPerfectPos-currPos;
                if (m1.getRotationSpeed() > diff) {
                    m1.setAcceleration(m1.getRotationSpeed()-diff);
                    m1.setSpeed(0);

                }
                else {
                    m1.setAcceleration(diff-m1.getRotationSpeed());
                    m1.setSpeed(m1.getMaxSpeed());
                }
            }
            else {
                int diff = currPos - nextPerfectPos;
                if (m1.getRotationSpeed() > diff) {
                    m1.setAcceleration(m1.getRotationSpeed()-diff);
                    m1.setSpeed(0);

                }
                else {
                    m1.setAcceleration(diff-m1.getRotationSpeed());
                    m1.setSpeed(m1.getMaxSpeed());
                }

            }*/

    private static void motorForward(NXTRegulatedMotor m) {
        m.forward();
        motorDirection = 1;
    }

    private static void motorBackward(NXTRegulatedMotor m) {
        m.backward();
        motorDirection = -1;
    }

    private static void motorFlt(NXTRegulatedMotor m) {
        m.flt();
        motorDirection = 0;
    }

    private static void motorStop(NXTRegulatedMotor m) {
        m.stop();
        motorDirection = 0;
    }

    private static boolean isForward() {
        return motorDirection == 1;
    }

    private static boolean isBackward() {
        return motorDirection == -1;
    }


}
