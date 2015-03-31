package dk.bjop.wirecuddler.motor;

import dk.bjop.wirecuddler.PosNotAvailableException;
import dk.bjop.wirecuddler.math.Utils;
import dk.bjop.wirecuddler.movement.TachoPositionController;
import lejos.nxt.Button;

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

    private boolean stopRequested = false;


    public LookAheadCuddleMotorController(NXTCuddleMotor m, TachoPositionController posCtrl) {
        this.m=m;
        this.posCtrl = posCtrl;
    }


    public void run() {
        while (!Button.ESCAPE.isDown() /*&& !pathList.isEmpty()*/) {
            stopRequested=false;
            posCtrl.waitForMove(this); // blocks here

            //cmc.waitForMove(m.getID().getIDNumber());
            long startTime = System.currentTimeMillis();
            Utils.println("Controller: '" + m.getID().getIDString() + "' notified! Startime will be: " + startTime);
            try {
                followTachoPath();
            } catch (InterruptedException e) {
                // Stop motors
                m.setAcceleration(1);
                m.flt();

               Utils.println("MotorController '" + m.getID().getIDString() + "' interrupted. Cause: "+e.getMessage());
            }
        }

        println("PathRunner ending...");
    }

    private void followTachoPath() throws InterruptedException {

        // We track an SSE-like error of each move
        long errorSum = 0;
        int obsCount = 0;

       // long pathMoveStarttime = System.currentTimeMillis();

        m.setAcceleration(0);
        m.setSpeed(0);

        boolean targetPosAvailable = true;

        while (!stopRequested) {



            // Check for is finished



            long now = System.currentTimeMillis();

            //int nextPerfectPos = path.getExpectedTachoPosAtTimeT((System.currentTimeMillis() - pathMoveStarttime) + lookAheadMillis)[id];
            int nextPerfectPos = 0;
            int perfectCurPos = 0;
            try {
                nextPerfectPos = posCtrl.getTachoPositionAtTimeT(now + lookAheadMillis, this);
                //perfectCurPos = posCtrl.getTachoPositionAtTimeT(now, this);
            } catch (PosNotAvailableException e) {
                Utils.println(e.getMessage());
                break;
            }


            int currPos = m.getTachoCount();

            int error = perfectCurPos - currPos;

            errorSum += Math.pow(Math.max(perfectCurPos, currPos) - Math.min(perfectCurPos, currPos),2);
            obsCount++;

            float errAdjustWeight = 2.5f;
            float errCorrection = 0;//Math.round(error*errAdjustWeight);
            //println(""+errCorrection);

            int diff = 0;
            int acc = 0;
            if (nextPerfectPos < perfectCurPos) {
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

            //println("DIFF: " + diff + "  ACC: " + acc + "  ERR: "+error+"  SPEED: " + m.getRotationSpeed());

            lookAheadMillis = 1000 + Math.abs(acc)/4;


            /*logger.writeLog(m.getTachoCount());
            logger.writeLog(perfectCurPos);
            logger.writeLog(error);
            logger.finishLine();*/
//            MotorSyncController.log(getControllerID(), m.getTachoCount(), perfectCurPos, error);

            long loopTime = System.currentTimeMillis()-now;
            if (loopTime > adjustIntervalMillis) println("LOOP FLAW! Looptime exceeded the interval");
            sleepx(adjustIntervalMillis - loopTime);


        }

        m.setAcceleration(100);
        m.setSpeed(0);
        m.stop();
        m.flt();
        //m.resetTachoCount();


        println("Error of the " + obsCount + " observations: " + (errorSum/obsCount));


    }

    public void requestStop() {
        stopRequested = true;
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
    
    public void println(String s) {
        Utils.println("["+getControllerID()+"] "+s);
    }


}
