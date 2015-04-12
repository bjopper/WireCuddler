package dk.bjop.wirecuddler.gui;

import dk.bjop.wirecuddler.CuddleController;
import dk.bjop.wirecuddler.config.CalibValues;
import lejos.nxt.Button;
import lejos.nxt.LCD;

/**
 * Created by bpeterse on 03-11-2014.
 */
public class CuddleMenu {

    final int menuWaitAfterButtonPress = 500;

    CuddleController cc;
    CalibrationMenu cm;


    public CuddleMenu() {
        // M2-M1: 13114
        // M1-M3: 13428
        // M3-M2: 10537
        cc = new CuddleController();
        cm = new CalibrationMenu(cc);
    }

    public void mainMenu() throws InterruptedException {
        while (true) {
            if (CalibValues.getInstance() == null) {
                if (CalibValues.calibrationFileExist()) {
                    CalibValues.loadCalib();
                    startupMenu();
                } else {
                    cm.calibMenu();
                }
            }
            else {
                startupMenu();
            }
        }
    }

    private void startupMenu() throws InterruptedException{
        boolean redraw = true;
        int mainSelect = 0;

        while (true) {

            if (redraw) {
                LCD.clear();
                LCD.drawString("Cuddle", 0, 0, mainSelect==0);
                LCD.drawString("Settings", 0, 1, false); // TODO implement
                LCD.drawString("Calibration", 0, 2, mainSelect==1);
                LCD.drawString("Exit", 0, 3, mainSelect==2);
                redraw = false;
            }

            if (Button.ENTER.isDown()) {
                if (mainSelect == 0 ) cuddleStartStopMenu();
                if (mainSelect == 1 ) cm.calibMenu();
                if (mainSelect == 2 ) throw new RuntimeException("Terminating system."); // This is the only known way to make sure the NXT shuts down the running program!? (and then lejos turns off by auto after a while)

                redraw = true;
                Thread.sleep(menuWaitAfterButtonPress);
            }
            if (Button.LEFT.isDown()) {
                mainSelect--;
                redraw = true;
                Thread.sleep(menuWaitAfterButtonPress);
            }
            if (Button.RIGHT.isDown()) {
                mainSelect++;
                redraw = true;
                Thread.sleep(menuWaitAfterButtonPress);
            }

            if (mainSelect < 0) mainSelect = 2;
            if (mainSelect > 2) mainSelect = 0;
        }
    }

    private void cuddleStartStopMenu() throws InterruptedException {
        Thread.sleep(menuWaitAfterButtonPress);
        boolean redraw = true;

        try {
            cc.doCuddle();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (true) {

            if (redraw) {
                LCD.clear();
                LCD.drawString("  CUDDLING...", 0, 0);
                LCD.drawString("Stop cuddling", 0, 1, true);

                redraw = false;
            }

            if (Button.ENTER.isDown()) {
                cc.stopCuddle();
                Thread.sleep(menuWaitAfterButtonPress);
                break;
            }

        }

    }

}
