package dk.bjop.wirecuddler;

import dk.bjop.wirecuddler.math.Utils;
import lejos.nxt.Sound;

/**
 * Created by bpeterse on 02-06-2015.
 */
public class AutoStopper extends Thread {

    private int millisBeforeStop;
    private CuddleController cc;

    public AutoStopper(int minutesBeforeStop, CuddleController cc) {
        this.millisBeforeStop = minutesBeforeStop * 60000;
        this.cc = cc;
    }


    public void run() {
        Utils.println("AutoStop thread running...  (will terminate system in " +(millisBeforeStop/60000) + " minutes)");

        long startTime = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() > startTime + millisBeforeStop - 10000) {
                Sound.beepSequenceUp();
            }

            if (System.currentTimeMillis() > startTime + millisBeforeStop) {
                Utils.println("AutoStop thread stopping cuddler...");
                cc.stopCuddle();
                Utils.println("AutoStop thread terminating system...");
                WireCuddler.terminateProgram("Stop requested by AutoStop thread.");
            }
        }
    }
}
