package dk.bjop.wirecuddler.motor;

import dk.bjop.wirecuddler.math.Utils;
import dk.bjop.wirecuddler.math.WT3Coord;
import dk.bjop.wirecuddler.movement.moveproducers.CuddleMoveProducer;
import dk.bjop.wirecuddler.movement.moves.MotorPathMove;

import java.util.ArrayList;

/**
 * Created by bpeterse on 13-09-2014.
 */
public class MotorSyncController extends Thread {

    private ArrayList<MotorPathMove> activeMovesList = new ArrayList<MotorPathMove>();
    private MotorPathMove currentMove = null;

    private final int lookaheadMillis = 1000;
    private final float accMultiplier = 1.8f;


    private MotorGroup mg = MotorGroup.getInstance();
    private CuddleMoveProducer cmp;


    public MotorSyncController() {

    }

    public void setMoveProducer(CuddleMoveProducer cmp) {
        this.cmp = cmp;
    }


    public void run() {

        currentMove = cmp.getNewMove();
        currentMove.initialize(new WT3Coord(mg.getTachoCounts()).toCartesian(), System.currentTimeMillis());

        while (true) {

            long now = System.currentTimeMillis();
            if (currentMove.isAfterMove(now)) {
                // This move is over - start a new one
                if (cmp.hasMoreMoves()) {
                    currentMove = cmp.getNewMove();
                    currentMove.initialize(new WT3Coord(mg.getTachoCounts()).toCartesian(), now);
                }
                else {
                    Utils.println("No more moves available. Terminating loop...");
                    break;
                }
            }


            int[] calculatedTachosNow = currentMove.getExpectedTachoPosAtTimeT(now);
            int[] calculatedFutureTachos = currentMove.getExpectedTachoPosAtTimeT(now + lookaheadMillis);

            Utils.println("Loop started at: "+now);
            for (int i=0;i<3;i++) {
                Utils.println("Setting motor M" + (i+1) + " acc and speed at time: "+System.currentTimeMillis());
                setMotorAccAndSpeed(mg.getMotorByIndex(i), calculatedTachosNow[i], calculatedFutureTachos[i]);
            }



            sleepx(250);
        }


        Utils.println("Main loop terminated. Stopping all engines...");

        for (int i=0;i<3;i++) {
            mg.getMotorByIndex(i).setSpeed(1);
            mg.getMotorByIndex(i).setAcceleration(200);
            mg.getMotorByIndex(i).stop();
        }

        Utils.println("All engines set to stop.");
    }


    private void setMotorAccAndSpeed(NXTCuddleMotor m, int perfectCurPos, int nextPerfectPos) {
        int currPos = m.getTachoCount();

        int diff = 0;
        int acc = 0;
        if (nextPerfectPos < perfectCurPos) {
            m.backward();

            diff = nextPerfectPos-currPos;
            if (m.getRotationSpeed() < diff) {
                Utils.println("BACKWARD: Setting speed to ZERO! (RS: " +m.getRotationSpeed()+" DIFF: " + diff + " ACC: " + (diff-m.getRotationSpeed()) + ")");
                acc = Math.round(((m.getRotationSpeed()-diff )*accMultiplier));
                m.setAcceleration(acc);
                m.setSpeed(1);
            }
            else {
                Utils.println("BACKWARD: Setting speed to MAX! (RS: " +m.getRotationSpeed()+" DIFF: " + diff + " ACC: " + (diff-m.getRotationSpeed()) + ")");
                acc = Math.round(((diff-m.getRotationSpeed())*accMultiplier));
                m.setAcceleration(acc);
                m.setSpeed(m.getMaxSpeed());
            }
        }
        else {
            m.forward();

            diff = nextPerfectPos-currPos;
            if (m.getRotationSpeed() > diff) {
                Utils.println("FORWARD: Setting speed to ZERO! (RS: " +m.getRotationSpeed()+" DIFF: " + diff + " ACC: " + (diff-m.getRotationSpeed()) + ")");
                acc = Math.round(((m.getRotationSpeed()-diff)*accMultiplier));
                m.setAcceleration(acc);
                m.setSpeed(1);
            }
            else {
                Utils.println("FORWARD: Setting speed to MAX! (RS: " +m.getRotationSpeed()+" DIFF: " + diff + " ACC: " + (diff-m.getRotationSpeed()) + ")");
                acc = Math.round(((diff-m.getRotationSpeed())*accMultiplier));
                m.setAcceleration(acc);
                m.setSpeed(m.getMaxSpeed());
            }
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
