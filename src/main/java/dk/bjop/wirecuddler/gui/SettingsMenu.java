package dk.bjop.wirecuddler.gui;

import dk.bjop.wirecuddler.CuddleController;
import lejos.nxt.Button;
import lejos.nxt.LCD;

/**
 * Created by bpeterse on 28-12-2014.
 */
public class SettingsMenu {

    private CuddleController cc;

    ProfileMenu pm;
    CalibrationMenu cm;

    public SettingsMenu(CuddleController cc) {
        this.cc = cc;
        pm = new ProfileMenu(cc);
        cm = new CalibrationMenu(cc);
    }

    public int getNextIndex(int min, int max, int cur) {
        if (cur == max-1) return min;
        else return ++cur;
    }

    public int getPrevIndex(int min, int max, int cur) {
        if (cur == min) return max-1;
        else return --cur;
    }

    public void startMenu() throws InterruptedException {
        Thread.sleep(500);
        int mainSelect = 0;

        String heading="  SETTINGS MENU   ";
        int offset = 2;
        String[] options = new String[]{"Profiles", "Calibration"};

        redraw(heading, offset, options, mainSelect);
        while (true) {

            if (Button.ENTER.isDown()) {
                while (Button.ENTER.isDown()) Thread.sleep(10);
                if (mainSelect == 0) pm.startMenu();
                else if (mainSelect == 1) cm.startMenu();
                else throw new RuntimeException("Invalid choice! [" + mainSelect + "]");
                redraw(heading, offset, options, mainSelect);
            }
            if (Button.LEFT.isDown()) {
                mainSelect = getPrevIndex(0, options.length, mainSelect);
                redraw(heading, offset, options, mainSelect);
                while (Button.LEFT.isDown()) Thread.sleep(10);
            }
            if (Button.RIGHT.isDown()) {
                mainSelect = getNextIndex(0, options.length, mainSelect);
                redraw(heading, offset, options, mainSelect);
                while (Button.RIGHT.isDown()) Thread.sleep(10);
            }
            if (Button.ESCAPE.isDown()) {
                while (Button.ESCAPE.isDown()) Thread.sleep(10);
                break;
            }
        }
    }

    private void redraw(String heading, int lineOffset, String[] options, int select) {
        LCD.clear();
        LCD.drawString(heading, 0, 0, false);
        for (int i = 0; i < options.length; i++) {
            if (options[i] !=null && !options[i].trim().equals("")) LCD.drawString("- " + options[i], 0, lineOffset + i, select == i);
        }
    }


}
