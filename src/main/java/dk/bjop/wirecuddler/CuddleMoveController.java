package dk.bjop.wirecuddler;

import dk.bjop.wirecuddler.math.Utils;
import dk.bjop.wirecuddler.math.WT3Coord;
import dk.bjop.wirecuddler.math.XYZCoord;
import dk.bjop.wirecuddler.motor.MotorGroup;
import dk.bjop.wirecuddler.motor.MotorPathController;
import dk.bjop.wirecuddler.motor.MotorPathMove;

import java.util.ArrayList;

/**
 * Created by bpeterse on 20-09-2014.
 */
public class CuddleMoveController extends Thread{

    MotorGroup mg;
    MotorPathController[] mpcs;

    ArrayList<MotorPathMove> movesList = new ArrayList<MotorPathMove>();
    Object listLock = new Object();

    Object monitor = new Object();
    MotorPathMove m = null;
    int threadsWaiting = 0;

    XYZCoord currentPos;

    public CuddleMoveController(MotorGroup mg) {
        this.mg = mg;
        this.mpcs = new MotorPathController[] {new MotorPathController(mg.getM1(), MotorGroup.M1, this), new MotorPathController(mg.getM2(), MotorGroup.M2, this), new MotorPathController(mg.getM3(), MotorGroup.M3, this)};

        for (int i = 0; i<mpcs.length;i++) {
            mpcs[i].start();
        }
       this.start();
    }

    public void run() {
        Utils.println("Cuddle move controller running...");

        while (true) {
            if (readyForNextMove() && !movesList.isEmpty()) {

                MotorPathMove move = null;
                synchronized (listLock) {
                    move = movesList.remove(0);
                }

                if (move != null) {
                    // Obtain current position
                    int[] tachos = mg.getTachoCounts();

                    for (int i = 0;i<tachos.length;i++) {
                        Utils.println("[" + i + "] = " +tachos[i]);
                    }


                    currentPos = new WT3Coord(tachos).toCartesian();
                    move.setStartPos(currentPos);
                    Utils.println("Setting startpos to: " + currentPos.toString() + " and targetPos to: " + move.getTargetPos());
                    Utils.println("Wires are now: " + currentPos.toWiresTachoCoord().toString());
                    Utils.println("Distance is: " + currentPos.distanceTo(move.getTargetPos()));

                    MotorPathController.setMove(move);
                    notifyAllMoveControllers();
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean readyForNextMove() {
        return threadsWaiting == 3;
    }

     public void waitForMove(int mcid) {
         synchronized (monitor) {
             threadsWaiting++;
             Utils.println("MotorPathController: '" + mcid + "' entering wait for move...");
             try {
                 monitor.wait();
             } catch (InterruptedException e) {
                 System.out.println("InterruptedException caught");
             }
         }
     }

    private void notifyAllMoveControllers() {
        synchronized (monitor) {
            Utils.println("Notifying all threads of start!");
            monitor.notifyAll();
            threadsWaiting = 0;
        }
    }

    public void queueMove(MotorPathMove move) {
        synchronized (listLock) {
            movesList.add(move);
            Utils.println("move added to movesList...");
        }
    }
}
