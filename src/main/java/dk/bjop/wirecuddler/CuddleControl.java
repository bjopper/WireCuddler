package dk.bjop.wirecuddler;

import lejos.nxt.Button;

/**
 * Created by bpeterse on 13-09-2014.
 */
public class CuddleControl extends Thread {


    public void run() {
        while (!Button.ESCAPE.isDown()) {



            sleepx(500);
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
