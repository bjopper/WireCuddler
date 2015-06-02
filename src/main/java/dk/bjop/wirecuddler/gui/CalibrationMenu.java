package dk.bjop.wirecuddler.gui;

import dk.bjop.wirecuddler.CuddleController;
import dk.bjop.wirecuddler.config.CalibValues;
import dk.bjop.wirecuddler.gui.utils.Messages;
import dk.bjop.wirecuddler.gui.utils.ValueSelect;
import dk.bjop.wirecuddler.math.Utils;
import lejos.nxt.Button;
import lejos.nxt.LCD;

/**
 * Created by bpeterse on 28-12-2014.
 */
public class CalibrationMenu {

    final int menuWaitAfterButtonPress = 500;
    CuddleController cc;

    public CalibrationMenu(CuddleController cc) {
        this.cc = cc;
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
        Thread.sleep(menuWaitAfterButtonPress);
        boolean redraw = true;
        int mainSelect = 0;
        redrawStartMenu(mainSelect);
        while (true) {
            if (Button.ENTER.isDown()) {
                if (mainSelect == 0 ) runCalibWizard();
                if (mainSelect == 1 ) toRestpoint();
                redrawStartMenu(mainSelect);
            }
            if (Button.ESCAPE.isDown()) {
                while (Button.ESCAPE.isDown()) Thread.sleep(10);
                break;
            }
            if (Button.LEFT.isDown()) {
                mainSelect = getPrevIndex(0, 2, mainSelect);
                redrawStartMenu(mainSelect);
                while (Button.LEFT.isDown()) Thread.sleep(10);
                redraw = true;
            }
            if (Button.RIGHT.isDown()) {
                mainSelect = getNextIndex(0, 2, mainSelect);
                redrawStartMenu(mainSelect);
                while (Button.RIGHT.isDown()) Thread.sleep(10);
                redraw = true;
            }
        }
    }

    private void redrawStartMenu(int select) {
        LCD.clear();
        LCD.drawString("   CALIBRATION", 0, 0);
        LCD.drawString(" - Calibrate", 0, 3, select==0);
        LCD.drawString(" - Find restpoint", 0, 4, select==1);
    }

    private void toRestpoint() {
       /* TriangleMeasurer t = new TriangleMeasurer();
        t.moveToRestPoint(mgrp);*/
        cc.moveToRestpoint();
    }

    private void runCalibWizard() throws InterruptedException{
        Messages.showOkCancelMessage("",new String[]{"Set all values.","Units are mm"}, false);

        int maxValue = 10000;

        Integer p1p2Dist = new ValueSelect().selectValueMenu("   A - B dist", 0, maxValue);
        if (p1p2Dist == null) throw new RuntimeException("err");

        Integer p1p3Dist = new ValueSelect().selectValueMenu("   A - C dist", 0, maxValue);
        if (p1p2Dist == null) throw new RuntimeException("err");

        Integer p2p3Dist = new ValueSelect().selectValueMenu("   B - C dist", 0, maxValue);
        if (p1p2Dist == null) throw new RuntimeException("err");

        Integer p1p2HeightDiff = new ValueSelect().selectValueMenu("A-B height-diff", 0, maxValue);
        if (p1p2Dist == null) throw new RuntimeException("err");

        Integer p1p3HeightDiff = new ValueSelect().selectValueMenu("A-C height-diff", 0, maxValue);
        if (p1p2Dist == null) throw new RuntimeException("err");

        CalibValues cv = CalibValues.createCalibInstance(p1p2HeightDiff, p1p3HeightDiff, p1p2Dist, p1p3Dist, p2p3Dist, 1);
        cv.saveCalib();

        Utils.println("Calibration wizard result:");
        Utils.println(cv.toString());

        Messages.showOkCancelMessage("",new String[]{"Calibration","complete.", "Data has ","been saved."}, false);
    }





}
