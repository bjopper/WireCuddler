package dk.bjop.wirecuddler.movement;

import dk.bjop.wirecuddler.PosNotAvailableException;
import dk.bjop.wirecuddler.math.Utils;
import dk.bjop.wirecuddler.math.WT3Coord;
import dk.bjop.wirecuddler.motor.LookAheadCuddleMotorController;
import dk.bjop.wirecuddler.motor.MotorGroup;
import dk.bjop.wirecuddler.movement.moves.MotorPathMove;
import dk.bjop.wirecuddler.movement.moves.StraightToPointMove;

import java.util.ArrayList;

/**
 * Created by bpeterse on 20-09-2014.
 */
public class CuddleMoveController extends Thread implements TachoPositionController {

    MotorGroup mg;
    LookAheadCuddleMotorController[] mpcs;

    Object moveProducerLock = new Object();
    Object monitor = new Object();

    int threadsWaiting = 0;
    CuddleMoveProducer cmp;
    ArrayList<MotorPathMove> activeMovesList = new ArrayList<MotorPathMove>();
    private boolean skipCurrentMoveRequested =false;


    public CuddleMoveController(MotorGroup mg) {
        this.mg = mg;
        this.mpcs = new LookAheadCuddleMotorController[] {new LookAheadCuddleMotorController(mg.getM1(), this), new LookAheadCuddleMotorController(mg.getM2(), this), new LookAheadCuddleMotorController(mg.getM3(), this)};

        for (int i = 0; i<mpcs.length;i++) {
            mpcs[i].start();
        }

       // mpcs[2].setDebugMode(true);
    }

    public void setMoveProducer(CuddleMoveProducer cmp) {
        synchronized (moveProducerLock) {
            this.cmp = cmp;
        }
    }


    public void run() {
        Utils.println("Cuddle move controller running...");

        while (true) {

            if (allThreadsWaiting() && moveAvailable()) {
                try {
                    activeMovesList.add(0,getNextMove());
                    activeMovesList.get(0).initialize(new WT3Coord(mg.getTachoCounts()).toCartesian(), System.currentTimeMillis());
                    notifyAllMoveControllers();
                } catch (PosNotAvailableException e) {
                    Utils.println(e.getMessage());
                }
            }

            try {
                Thread.sleep(200);
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
            if (m.isAfterMove(t) || skipCurrentMoveRequested) {
                skipCurrentMoveRequested = false;

                MotorPathMove newMove = getNextMove();
                newMove.initialize(new WT3Coord(mg.getTachoCounts()).toCartesian(), t);
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

        return m.getExpectedTachoPosAtTimeT(t)[mpc.getControllerID().getIDNumber()-1];
    }

    public void skipCurrentMove() {
        skipCurrentMoveRequested = true;
    }

    public void skipCurrentMoveAndReturnToInitialPosition() {
        ArrayList<MotorPathMove> l = new ArrayList<MotorPathMove>();
        l.add(new StraightToPointMove(new WT3Coord(mg.getInitialPosition()).toCartesian()));
        setMoveProducer(new CuddleMoveProducerByList(l));

        skipCurrentMove();
    }

    private MotorPathMove getNextMove() throws PosNotAvailableException {
        synchronized (moveProducerLock) {
            if (!moveAvailable()) throw new PosNotAvailableException("Producer fresh out of moves!");
            return cmp.getNewMove();
        }
    }

    private boolean moveAvailable() {
        synchronized (moveProducerLock) {
            return cmp.hasMoreMoves();
        }
    }

    @Override
    public void waitForMove(LookAheadCuddleMotorController m) {
        synchronized (monitor) {
            threadsWaiting++;
            Utils.println("MotorPathController: '" + m.getControllerID().getIDNumber() + "' entering wait for move...");
            try {
                monitor.wait();
            } catch (InterruptedException e) {
                Utils.println("InterruptedException caught");
            }
        }
    }

}
