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
    //CuddlePointMenu cpm;
    ProfileMenu pm;
    SettingsMenu sm;


    public CuddleMenu() {
        // M2-M1: 13114
        // M1-M3: 13428
        // M3-M2: 10537
        cc = new CuddleController();
        cm = new CalibrationMenu(cc);
        //cpm = new CuddlePointMenu(cc);
        pm = new ProfileMenu(cc);
        sm = new SettingsMenu(cc);
    }

    public void mainMenu() throws InterruptedException {
        while (true) {
            if (CalibValues.getInstance() == null) {
                if (CalibValues.calibrationFileExist()) {
                    CalibValues.loadCalib();
                    startupMenu();
                } else {
                    cm.startMenu();
                }
            }
            else {
                startupMenu();
            }
        }
    }

    public int getNextIndex(int min, int max, int cur) {
        if (cur == max-1) return min;
        else return ++cur;
    }

    public int getPrevIndex(int min, int max, int cur) {
        if (cur == min) return max-1;
        else return --cur;
    }

    private void redraw(int select) {
        LCD.clear();
        LCD.drawString("    MAIN MENU", 0, 0, false);
        LCD.drawString(" - Cuddle", 0, 2, select==0);
        LCD.drawString(" - Settings", 0, 3, select==1);
        LCD.drawString(" - Calibration", 0, 4, select==2);
        LCD.drawString(" - Exit", 0, 5, select==3);
    }

    private void startupMenu() throws InterruptedException{
        boolean redraw = true;
        int mainSelect = 0;
        redraw(mainSelect);

        while (true) {

            if (Button.ENTER.isDown()) {
                while (Button.ENTER.isDown()) Thread.sleep(10);
                if (mainSelect == 0 ) cuddleStartStopMenu();
                if (mainSelect == 1 ) pm.startMenu();
                if (mainSelect == 2 ) cm.startMenu();
                if (mainSelect == 3 ) throw new RuntimeException("Terminating system."); // This is the only known way to make sure the NXT shuts down the running program!? (and then lejos turns off by auto after a while)

                redraw(mainSelect);
            }
            if (Button.LEFT.isDown()) {
                mainSelect = getPrevIndex(0, 4, mainSelect);
                redraw(mainSelect);
                while (Button.LEFT.isDown()) Thread.sleep(10);
            }
            if (Button.RIGHT.isDown()) {
                mainSelect = getNextIndex(0, 4, mainSelect);
                redraw(mainSelect);
                while (Button.RIGHT.isDown()) Thread.sleep(10);
            }
        }
    }

    private void cuddleStartStopMenu() throws InterruptedException {
        boolean redraw = true;

        cc.doCuddle();

        while (true) {

            if (redraw) {
                LCD.clear();
                LCD.drawString("  CUDDLING...", 0, 0);
                LCD.drawString("Stop cuddling", 0, 4, true);

                redraw = false;
            }

            if (Button.ENTER.isDown()) {
                cc.stopCuddle();
                while (Button.ENTER.isDown()) Thread.sleep(10);
                break;
            }

        }

    }

}
