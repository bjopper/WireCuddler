package dk.bjop.wirecuddler.movement;

import dk.bjop.wirecuddler.math.Utils;
import dk.bjop.wirecuddler.math.coordinates.WT3Coord;
import dk.bjop.wirecuddler.motor.LookAheadCuddleMotorController;
import dk.bjop.wirecuddler.motor.MotorGroup;
import dk.bjop.wirecuddler.movement.moveproducers.CuddleMoveProducer;
import dk.bjop.wirecuddler.movement.moveproducers.CuddleMoveProducerByList;
import dk.bjop.wirecuddler.movement.moves.MotorPathMove;

import java.util.ArrayList;

/**
 * Created by bpeterse on 20-09-2014.
 */
public class CuddleMoveController extends Thread implements TachoPositionController {

    MotorGroup mg;
    LookAheadCuddleMotorController[] mpcs;
    CuddleMoveProducer cmp;

    Object cmpLock = new Object();
    Object controllersLock = new Object();
    Object moveCompleteLock = new Object();

    boolean objectsAwaitingMovesCompleted = false;

    int threadsWaiting = 0;
    MotorPathMove currentMove = null;
    boolean terminate = false;


    public CuddleMoveController(MotorGroup mg) {
        this.mg = mg;
        this.mpcs = new LookAheadCuddleMotorController[] {new LookAheadCuddleMotorController(mg.getM1(), this), new LookAheadCuddleMotorController(mg.getM2(), this), new LookAheadCuddleMotorController(mg.getM3(), this)};

        //mpcs[0].setDebugMode(true);
        //mpcs[1].setDebugMode(true);
        //mpcs[2].setDebugMode(true);
    }


    public void runMoves(CuddleMoveProducer cmp, boolean waitForCurrentMoveCompletion) {
       if (!this.isAlive() || terminate) throw new RuntimeException("CuddleMoveController has not been started!");
        if (!waitForCurrentMoveCompletion && currentMove != null) currentMove.setMoveTerminate();
        synchronized (cmpLock) {
            this.cmp = cmp;
        }
    }

    public void runMoves(ArrayList<MotorPathMove> moves, boolean waitForCurrentMoveCompletion) {
        runMoves(new CuddleMoveProducerByList(moves), waitForCurrentMoveCompletion);
    }

    public void runMove(MotorPathMove m, boolean waitForCurrentMoveCompletion) {
        ArrayList<MotorPathMove> moves = new ArrayList<MotorPathMove>();
        moves.add(0,m);
        runMoves(moves, waitForCurrentMoveCompletion);
    }

    public void terminateRunningMoves() {
        if (!this.isAlive()) throw new RuntimeException("CuddleMoveController has not been started!");
        synchronized (cmpLock) {
            this.cmp = null;
        }
    }

    public void stopController() {
        this.terminate=true;
    }


    public void run() {
        terminate = false;

        Utils.println("Starting LookAheadCuddleMotorControllers...");
        for (int i = 0; i<mpcs.length;i++) {
            mpcs[i].start();
        }
        Utils.println("Cuddle move controller running...");


        while (!terminate) {

            if (allThreadsWaiting()) {
                if (moveAvailable()) {
                    MotorPathMove newMove = getNextMove();
                    if (newMove != null) {
                        newMove.initialize(new WT3Coord(mg.getTachoCounts()).toCartesian(), System.currentTimeMillis());
                        currentMove = newMove;
                    }
                    notifyAllMoveControllers();
                } else {
                    if (objectsAwaitingMovesCompleted) notifyAllMovesCompleted();
                }
            }
            else {
                if (currentMove != null) {
                    long t = System.currentTimeMillis();
                    if (currentMove.isMoveDone(t)) {
                        if (moveAvailable()) {
                            MotorPathMove newMove = getNextMove();
                            if (newMove != null) {
                                newMove.initialize(new WT3Coord(mg.getTachoCounts()).toCartesian(), t);
                            }
                            currentMove = newMove;
                        } else {
                            currentMove = null;
                        }
                    }
                }
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Utils.println("Stopping LookaheadCuddleMotorController threads...");
        for (int i = 0; i < mpcs.length; i++) {
            mpcs[i].setStopRequested();
            mpcs[i].interrupt();
        }
        Utils.println("Initial tachos were: "+new WT3Coord(mg.getInitialPosition()).toString());
        Utils.println("Current tachos are: "+new WT3Coord(mg.getTachoCounts()).toString());
        Utils.println("CuddleMoveController-thread terminating...");
        terminate=false;
    }

    public void waitForAllMovesCompleted() {

        synchronized (moveCompleteLock) {
            objectsAwaitingMovesCompleted = true;
            try {
                moveCompleteLock.wait();
            } catch (InterruptedException e) {
                Utils.println("InterruptedException caught");
            }
        }
    }

    private void notifyAllMovesCompleted() {
        synchronized (moveCompleteLock) {
            moveCompleteLock.notifyAll();
            objectsAwaitingMovesCompleted=false;
        }
    }

    /**
     *
     * @param t the time at which we want to know the tacho-position. If no position can be obtained for the given time -1 is returned.
     * @return the tacho-position the motor should be at the specified time
     */
    @Override
    public Integer getTachoPositionAtTimeT(long t, LookAheadCuddleMotorController mpc)  {
        if (currentMove != null) return new Integer(currentMove.getExpectedTachoPosAtTimeT(t)[mpc.getControllerID().getIDNumber()-1]); // TODO make threadsafe
        else return null;
    }

    @Override
    public void waitForMove(LookAheadCuddleMotorController m) {
        synchronized (controllersLock) {
            threadsWaiting++;
            Utils.println("MotorPathController: '" + m.getControllerID().getIDNumber() + "' entering wait for move...");
            try {
                controllersLock.wait();
            } catch (InterruptedException e) {
                Utils.println("InterruptedException caught");
            }
        }
    }

    private MotorPathMove getNextMove() {
        synchronized (cmpLock) {
            if (!moveAvailable()) return null;
            else return cmp.getNewMove();
        }
    }

    private boolean moveAvailable() {
        synchronized (cmpLock) {
            return cmp != null && cmp.hasMoreMoves();
        }
    }

    private boolean allThreadsWaiting() {
        return threadsWaiting == 3;
    }

    private void notifyAllMoveControllers() {
        synchronized (controllersLock) {
            Utils.println("Notifying all threads of start!");
            controllersLock.notifyAll();
            threadsWaiting = 0;
        }
    }

}
