package dk.bjop.wirecuddler.gui;

import dk.bjop.wirecuddler.CuddleController;
import lejos.nxt.Button;
import lejos.nxt.LCD;

/**
 * Created by bpeterse on 28-12-2014.
 */
public class SettingsMenu {

    private CuddleController cc;

    public SettingsMenu(CuddleController cc) {
        this.cc = cc;
    }


    public void startMenu() throws InterruptedException {

        boolean redraw = true;
        int mainSelect = 0;

        String[] options = new String[]{"Profile", "Store current point"};

        while (true) {

            if (redraw) {
                LCD.clear();
                for (int i = 0; i < options.length; i++) {
                    LCD.drawString(options[i], 0, i, mainSelect == i);
                }
                redraw = false;
            }

            if (Button.ENTER.isDown()) {
                if (mainSelect == 0) throw new RuntimeException("Not implemented!");
                if (mainSelect == 1) throw new RuntimeException("Not implemented!");
                while (Button.ENTER.isDown()) Thread.sleep(10);
                redraw = true;
            }
            if (Button.LEFT.isDown()) {
                mainSelect--;
                while (Button.LEFT.isDown()) Thread.sleep(10);
                redraw = true;
            }
            if (Button.RIGHT.isDown()) {
                mainSelect++;
                while (Button.RIGHT.isDown()) Thread.sleep(10);
                redraw = true;
            }
            if (Button.ESCAPE.isDown()) {
                while (Button.ESCAPE.isDown()) Thread.sleep(10);
                break;
            }

            if (mainSelect < 0) mainSelect = options.length - 1;
            if (mainSelect == options.length) mainSelect = 0;
        }

    }

}
