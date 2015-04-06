package dk.bjop.wirecuddler.movement;

import dk.bjop.wirecuddler.PosNotAvailableException;
import dk.bjop.wirecuddler.math.Utils;
import dk.bjop.wirecuddler.math.WT3Coord;
import dk.bjop.wirecuddler.motor.LookAheadCuddleMotorController;
import dk.bjop.wirecuddler.motor.MotorGroup;
import dk.bjop.wirecuddler.movement.moves.MotorPathMove;

import java.util.ArrayList;

/**
 * Created by bpeterse on 20-09-2014.
 */
public class CuddleMoveController extends Thread implements TachoPositionController {

    MotorGroup mg;
    LookAheadCuddleMotorController[] mpcs;

    Object listLock = new Object();
    Object monitor = new Object();

    int threadsWaiting = 0;
    CuddleMoveProducer cmp;
    ArrayList<MotorPathMove> activeMovesList = new ArrayList<MotorPathMove>();

    public CuddleMoveController(MotorGroup mg) {
        this.mg = mg;
        this.mpcs = new LookAheadCuddleMotorController[] {new LookAheadCuddleMotorController(mg.getM1(), this), new LookAheadCuddleMotorController(mg.getM2(), this), new LookAheadCuddleMotorController(mg.getM3(), this)};

        for (int i = 0; i<mpcs.length;i++) {
            mpcs[i].start();
        }
    }

    public void setMoveProducer(CuddleMoveProducer cmp) {
        this.cmp = cmp;
    }


    public void run() {
        Utils.println("Cuddle move controller running...");

        while (true) {

            if (allThreadsWaiting() && cmp.hasMoreMoves()) {
                activeMovesList.add(0,cmp.getNewMove());
                // TODO handle move-producer no more moves
                activeMovesList.get(0).initialize(new WT3Coord(mg.getTachoCounts()).toCartesian(), System.currentTimeMillis());
                notifyAllMoveControllers();
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean allThreadsWaiting() {
        return threadsWaiting == 3;
    }

    private void notifyAllMoveControllers() {
        synchronized (monitor) {
            Utils.println("Notifying all threads of start!");
            monitor.notifyAll();
            threadsWaiting = 0;
        }
    }

    /**
     *
     * @param t the time at which we want to know the tacho-position. If no position can be obtained for the given time -1 is returned.
     * @return the tacho-position the motor should be at the specified time
     */
    @Override
    public int getTachoPositionAtTimeT(long t, LookAheadCuddleMotorController mpc) throws PosNotAvailableException {

        MotorPathMove m = null;
        synchronized (activeMovesList) {
            m = activeMovesList.get(0);
            if (m.isAfterMove(t)) {


                if (!cmp.hasMoreMoves()) throw new PosNotAvailableException("Producer fresh out of moves!");

                MotorPathMove newMove = cmp.getNewMove();

                if (m == null) {
                    newMove.initialize(new WT3Coord(mg.getTachoCounts()).toCartesian(), t);
                    m.setEndtime(t);
                }
                else {
                    newMove.initialize(m.getMoveTargetPos(), t);
                    m.setEndtime(t);
                }


                activeMovesList.add(0, newMove);
                m = activeMovesList.get(0);
                if (activeMovesList.size() > 5 ) {
                    activeMovesList.remove(activeMovesList.size() - 1);
                }
            } else if (m.isBeforeMove(t)) {
                if (activeMovesList.size() > 1) {
                    m = activeMovesList.get(1);
                } else {
                    Utils.println("This should not happen!?");
                    throw new PosNotAvailableException("");
                }
            }
        }

        return m.getExpectedTachoPosAtTimeT(t)[mpc.getControllerID().getIDNumber()-1];
    }

    @Override
    public void waitForMove(LookAheadCuddleMotorController m) {
        synchronized (monitor) {
            threadsWaiting++;
            Utils.println("MotorPathController: '" + m.getControllerID().getIDNumber() + "' entering wait for move...");
            try {
                monitor.wait();
            } catch (InterruptedException e) {
                System.out.println("InterruptedException caught");
            }
        }
    }

}
