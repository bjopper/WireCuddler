package dk.bjop.wirecuddler.motor;

import dk.bjop.wirecuddler.movement.PosNotAvailableException;
import dk.bjop.wirecuddler.math.Utils;
import dk.bjop.wirecuddler.movement.TachoPositionController;

/**
 * Created by bpeterse on 13-09-2014.
 */
public class LookAheadCuddleMotorController extends Thread {

    TachoPositionController posCtrl;
    NXTCuddleMotor m;

    float speed= 5f;

    // The 3 params below are central to tuning the ability to follow a line precisely. Espceially the latter two !
    int adjustIntervalMillis = 250;
    int lookAheadMillis = 1000; // TODO Increase this to ~1100+ when we have to perform very quick accelerations/decellerations - we need an algorithm to do this automatically!
    float accMultiplier = 1.8f;

    private boolean debugMode = false;

    private boolean stopRequested = false;


    public LookAheadCuddleMotorController(NXTCuddleMotor m, TachoPositionController posCtrl) {
        this.m=m;
        this.posCtrl = posCtrl;
    }

    public void setDebugMode(boolean debug) {
        this.debugMode = debug;
    }

    public void setStopRequested() {
        stopRequested = true;
    }

    public void run() {

        stopRequested = false;

        while (true) {

            posCtrl.waitForMove(this); // blocks here

            if (stopRequested) break;

            long startTime = System.currentTimeMillis();
            Utils.println("Controller: '" + m.getID().getIDString() + "' notified! Startime will be: " + startTime);
            try {
                followTachoPath();
            } catch (InterruptedException e) {
                // Stop motors
                m.setAcceleration(1000);
                m.setSpeed(1);
                m.flt();

                Utils.println("MotorController '" + m.getID().getIDString() + "' interrupted. Cause: "+e.getMessage());
                break;
            }
        }
        Utils.println("MotorController '" + m.getID().getIDString() + "' terminated.");
    }

    private void followTachoPath() throws InterruptedException {

        // We track an SSE-like error of each move
        long errorSum = 0;
        int obsCount = 0;

        while (true) {
            long now = System.currentTimeMillis();

            int nextPerfectPos = 0;
            int perfectCurPos = 0;
            try {
                nextPerfectPos = posCtrl.getTachoPositionAtTimeT(now + lookAheadMillis, this);
                perfectCurPos = posCtrl.getTachoPositionAtTimeT(now, this);
            } catch (PosNotAvailableException e) {
                Utils.println(e.getMessage());
                break;
            }

            int currPos = m.getTachoCount();

            if (debugMode) {
                // testing M3 only
                Utils.println("["+getControllerID()+"] CurrPos: "+currPos + "   PerfectCurPos: "+perfectCurPos + "   nextPerfectpos: " + nextPerfectPos);
            }

            int error = perfectCurPos - currPos;

            errorSum += Math.pow(Math.max(perfectCurPos, currPos) - Math.min(perfectCurPos, currPos),2);
            obsCount++;

            float errAdjustWeight = 2.5f;
            float errCorrection = 0;//Math.round(error*errAdjustWeight);
            //println(""+errCorrection);

            int diff = 0;
            int acc = 0;
            if (nextPerfectPos < currPos) {
                m.backward();

                diff = nextPerfectPos-currPos;
                if (m.getRotationSpeed() < diff) {
                    //println("BACKWARD: Setting speed to ZERO! (RS: " +m1.getRotationSpeed()+" DIFF: " + diff + " ACC: " + (diff-m1.getRotationSpeed()) + ")");
                    acc = Math.round(((m.getRotationSpeed()-diff )*accMultiplier)+errCorrection);
                    m.setAcceleration(acc);
                    m.setSpeed(1);
                }
                else {
                    //println("BACKWARD: Setting speed to MAX! (RS: " +m1.getRotationSpeed()+" DIFF: " + diff + " ACC: " + (diff-m1.getRotationSpeed()) + ")");
                    acc = Math.round(((diff-m.getRotationSpeed())*accMultiplier)+errCorrection);
                    m.setAcceleration(acc);
                    m.setSpeed(m.getMaxSpeed());
                }
            }
            else {
                m.forward();

                diff = nextPerfectPos-currPos;
                if (m.getRotationSpeed() > diff) {
                    //println("FORWARD: Setting speed to ZERO! (RS: " +m1.getRotationSpeed()+" DIFF: " + diff + " ACC: " + (diff-m1.getRotationSpeed()) + ")");
                    acc = Math.round(((m.getRotationSpeed()-diff)*accMultiplier)+errCorrection);
                    m.setAcceleration(acc);
                    m.setSpeed(1);
                }
                else {
                    //println("FORWARD: Setting speed to MAX! (RS: " +m1.getRotationSpeed()+" DIFF: " + diff + " ACC: " + (diff-m1.getRotationSpeed()) + ")");
                    acc = Math.round(((diff-m.getRotationSpeed())*accMultiplier)+errCorrection);
                    m.setAcceleration(acc);
                    m.setSpeed(m.getMaxSpeed());
                }
            }

            lookAheadMillis = 1000 + Math.abs(acc)/4;

            long loopTime = System.currentTimeMillis()-now;
            if (loopTime > adjustIntervalMillis) Utils.println("LOOP FLAW! Looptime exceeded the interval");
            sleepx(adjustIntervalMillis - loopTime);
        }

        m.setAcceleration(1000);
        m.setSpeed(1);
        m.stop();
        m.flt();

        if (obsCount > 0) Utils.println("Error of the " + obsCount + " observations: " + (errorSum / obsCount));
    }


    public NXTCuddleMotor.MotorID getControllerID() {
        return m.getID();
    }

    private void sleepx(long sleeptime)  {
        try {
            Thread.sleep(sleeptime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
