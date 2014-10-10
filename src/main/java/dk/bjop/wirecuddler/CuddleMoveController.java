package dk.bjop.wirecuddler;

import lejos.nxt.Button;

/**
 * Created by bpeterse on 20-09-2014.
 */
public class CuddleMoveController extends Thread {
    CuddleMove currentMove = null;
    long sleepTime = 500;

    CuddleMoveProducer moveProducer;

    boolean stopMoving = false;

    public CuddleMoveController(CuddleMoveProducer moveProducer) {
        this.moveProducer = moveProducer;
    }

    public void run() {
        while (!Button.ESCAPE.isDown() && !stopMoving) {

            if (currentMove == null) {
                currentMove = moveProducer.getNewMove();
            }

            //currentMove.getPosition();

            sleepx(sleepTime);
        }

        //Finis and cleanup...
    }

    private void sleepx(long sleeptime)  {
        try {
            Thread.sleep(sleeptime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}
