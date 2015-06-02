package dk.bjop.wirecuddler.gui.utils;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;

/**
 * Created by bpeterse on 25-05-2015.
 */
public class Messages {

    public static boolean showOkCancelMessage(String heading, String[] msg, boolean dobeep) throws InterruptedException {
        showMessage(heading, msg, dobeep);
        Button.waitForAnyPress();
        if (Button.readButtons() == Button.ID_ENTER) {
            while (Button.ENTER.isDown()) Thread.sleep(10);
            return true;
        }
        else return false;
    }

    public static void showTimedMessage(String heading, String[] msg, boolean dobeep, long millis) {
        showMessage(heading, msg, dobeep);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void showMessage(String heading, String[] msg, boolean dobeep) {
        LCD.clear();
        LCD.drawString(heading, 0, 0, false);
        if (msg != null) {
            for (int i = 0; i < msg.length; i++) {
                if (msg[i] != null && !msg[i].trim().equals("")) LCD.drawString(msg[i], 0, 2 + i, false);
            }
        }
        if (dobeep) Sound.beep();
    }
}
