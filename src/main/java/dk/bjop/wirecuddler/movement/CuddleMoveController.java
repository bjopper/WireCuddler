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

    //ArrayList<MotorPathMove> movesList = new ArrayList<MotorPathMove>();
    Object listLock = new Object();

    Object monitor = new Object();

    int threadsWaiting = 0;


    //long currentMoveStarttime;


    CuddleMoveProducer cmp;


    ArrayList<MotorPathMove> activeMovesList = new ArrayList<MotorPathMove>();

    public CuddleMoveController(MotorGroup mg) {
        this.mg = mg;
        this.mpcs = new LookAheadCuddleMotorController[] {new LookAheadCuddleMotorController(mg.getM1(), this), new LookAheadCuddleMotorController(mg.getM2(), this), new LookAheadCuddleMotorController(mg.getM3(), this)};

        for (int i = 0; i<mpcs.length;i++) {
            mpcs[i].start();
        }

        this.cmp = new CuddleMoveProducer();
    }

    public void run() {
        Utils.println("Cuddle move controller running...");

        while (true) {

            if (allThreadsReady()) {
                activeMovesList.add(0,cmp.getNewMove(new WT3Coord(mg.getTachoCounts()).toCartesian()));
                activeMovesList.get(0).setMoveStarttime(System.currentTimeMillis());
                notifyAllMoveControllers();
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean allThreadsReady() {
        return threadsWaiting == 3;
    }

  /*  private MotorPathMove getNextMove() {
        MotorPathMove move = null;
        synchronized (listLock) {
            move = movesList.remove(0);
        }
        return move;
    }*/

    /* public void waitForMove(int mcid) {
         synchronized (monitor) {
             threadsWaiting++;
             Utils.println("MotorPathController: '" + mcid + "' entering wait for move...");
             try {
                 monitor.wait();
             } catch (InterruptedException e) {
                 System.out.println("InterruptedException caught");
             }
         }
     }*/

    private void notifyAllMoveControllers() {
        synchronized (monitor) {
            Utils.println("Notifying all threads of start!");
            monitor.notifyAll();
            threadsWaiting = 0;
        }
    }

    /*public void queueMove(MotorPathMove move) {

        // Either validate target pos here, or do it when the move is coinstructed.
        synchronized (listLock) {
            movesList.add(move);
            Utils.println("move added to movesList...");
        }
    }*/

    /*public void queueMoves(Collection<? extends MotorPathMove> moves) {

        // Either validate target pos here, or do it when the move is constructed.
        synchronized (listLock) {
            movesList.addAll(moves);
        }
    }*/

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
                MotorPathMove newMove = cmp.getNewMove(m.getMoveTargetPos());
                newMove.setMoveStarttime(t);
                m.setEndtime(t);
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

        return m.getExpectedTachoPosAtTimeT(t)[mpc.getControllerID().getIDNumber()];
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

    /*private void getMove() {
        MotorPathMove move = null;

        if (activeMovesList.isEmpty()) {
            move = getNextMove();
        }
    }*/

}
