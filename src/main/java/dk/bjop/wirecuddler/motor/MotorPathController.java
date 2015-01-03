package dk.bjop.wirecuddler.motor;

import dk.bjop.wirecuddler.movement.CuddleMoveController;
import dk.bjop.wirecuddler.math.Utils;
import dk.bjop.wirecuddler.movement.moves.MotorPathMove;
import lejos.nxt.Button;

/**
 * Created by bpeterse on 13-09-2014.
 */
public class MotorPathController extends Thread {

    //ArrayList<MotorPath> pathList = new ArrayList<MotorPath>();
    static MotorPathMove path = null;
    static long pathMoveStarttime;

    int id; // 1,2 or 3 - one for each motor

    CuddleMoveController cmc;


    NXTCuddleMotor m;

    float speed= 5f;

    // The 3 params below are central to tuning the ability to follow a line precisely. Espceially the latter two !
    int adjustIntervalMillis = 250;
    int lookAheadMillis = 1000; // TODO Increase this to ~1100+ when we have to perform very quick accelerations/decellerations - we need an algorithm to do this automatically!
    float accMultiplier = 1.8f;



    public MotorPathController(NXTCuddleMotor m, int id, CuddleMoveController cmc) {
        this.id = id;
        this.m=m;
        this.cmc = cmc;
    }

    /*public void addMovementPath(MotorPath path) {
       pathList.add(path);
    }*/

    public void run() {


        while (!Button.ESCAPE.isDown() /*&& !pathList.isEmpty()*/) {

            cmc.waitForMove(id);
            long startTime = System.currentTimeMillis();
            Utils.println("Controller: '" + id + "' notified! Startime will be: " + startTime);
            followPath();

            // Before each new path we reset motor state
            m.setAcceleration(0);
            m.setSpeed(m.getMaxSpeed());
            m.forward();
            //m.resetTachoCount();
           // followPath(pathList.remove(0));
        }


        println("PathRunner ending...");
    }

    private void followPath() {

        // We track an SSE-like error of each move
        long errorSum = 0;
        int obsCount = 0;

       // long pathMoveStarttime = System.currentTimeMillis();

        m.setAcceleration(0);
        m.setSpeed(0);

        while (!Button.ESCAPE.isDown() && !path.isMovementFinished(System.currentTimeMillis() - pathMoveStarttime)) {
            long loopTimeStart = System.currentTimeMillis();

            int nextPerfectPos = path.getExpectedTachoPosAtTimeT((System.currentTimeMillis() - pathMoveStarttime) + lookAheadMillis)[id];
            int currPos = m.getTachoCount();
            int perfectCurPos = path.getExpectedTachoPosAtTimeT(System.currentTimeMillis() - pathMoveStarttime)[id];
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

            long loopTime = System.currentTimeMillis()-loopTimeStart;
            if (loopTime > adjustIntervalMillis) println("LOOP FLAW! Looptime exceeded the interval");
            sleepx(adjustIntervalMillis - loopTime);
        }

        m.setAcceleration(100);
        m.setSpeed(0);
        m.stop();
        m.flt();
        //m.resetTachoCount();

        if (path.isMovementFinished(System.currentTimeMillis() - pathMoveStarttime)) {
            println("Error of the " + obsCount + " observations: " + (errorSum/obsCount));
        }
    }

    public int getControllerID() {
        return id;
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


    public static void setMove(MotorPathMove mpth) {
        path = mpth;
        pathMoveStarttime = System.currentTimeMillis();
    }
}
