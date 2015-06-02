package dk.bjop.wirecuddler.gui.utils;

import lejos.nxt.Button;
import lejos.nxt.LCD;

/**
 * Created by bpeterse on 20-05-2015.
 */
public class ValueSelect {

    public ValueSelect() {

    }

    public Integer selectValueMenu(String heading, int min, int max, int defValue) throws InterruptedException{
        int count = defValue;
        draw(heading,count);
        while (true) {
            if (Button.ENTER.isDown()) {
                while (Button.ENTER.isDown()) Thread.sleep(10);
                return count;
            }
            if (Button.LEFT.isDown()) {
                count = getNewValue(count, -1, min, max);
                draw(heading,count);
                Thread.sleep(500);
                long startTime = System.currentTimeMillis();

                while (Button.LEFT.isDown()) {
                    count = getNewValue(count, -determineIncrease(startTime), min, max);
                    draw(heading,count);
                    Thread.sleep(200);
                }
                draw(heading,count);
            }
            if (Button.RIGHT.isDown()) {

                count = getNewValue(count, 1, min, max);
                draw(heading,count);
                Thread.sleep(500);
                long startTime = System.currentTimeMillis();

                while (Button.RIGHT.isDown()) {
                    count = getNewValue(count, determineIncrease(startTime), min, max);
                    draw(heading,count);
                    Thread.sleep(200);
                }
                draw(heading,count);
            }
            if (Button.ESCAPE.isDown()) {
                while (Button.ESCAPE.isDown()) Thread.sleep(10);
                return null;
            }
        }
    }

    private int getNewValue(int currValue, int increase, int min, int max) {
        currValue += increase;
        if (currValue > max) return max;
        if (currValue < min) return min;
        return currValue;
    }

    private int determineIncrease(long startTime) {
        long t = System.currentTimeMillis() - startTime;
        if (t < 1000) {
            return 1;
        }
        else if (t < 2000) {
            return 5;
        }
        else if (t < 3000) {
            return 10;
        }
        if (t < 4000) {
            return 50;
        }
        else {
            return 100;
        }
    }

    private void draw(String heading, int count) {
        LCD.clear();
        LCD.drawString(heading, 0, 0, false);
        LCD.drawString(""+count+" mm", 6, 4, false);
    }


}
