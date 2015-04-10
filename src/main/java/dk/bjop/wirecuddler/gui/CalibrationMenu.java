package dk.bjop.wirecuddler.gui;

import dk.bjop.wirecuddler.CuddleController;
import dk.bjop.wirecuddler.motor.MotorGroup;
import dk.bjop.wirecuddler.motor.NXTCuddleMotor;
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


    public void calibMenu() throws InterruptedException {
        Thread.sleep(menuWaitAfterButtonPress);
        boolean redraw = true;
        int mainSelect = 0;
        while (true) {

            if (redraw) {
                LCD.clear();
                LCD.drawString("  CALIBRATION", 0, 0);
                LCD.drawString("Manual", 0, 1, mainSelect==0);
                LCD.drawString("Automatic", 0, 2, mainSelect==1);
                redraw = false;
            }

            if (Button.ENTER.isDown()) {
                if (mainSelect == 0 ) selectMotorMenu();
                if (mainSelect == 1 ) autoCalibrate();

                redraw = true;
                Thread.sleep(menuWaitAfterButtonPress);
            }
            if (Button.ESCAPE.isDown()) {
                break;
            }
            if (Button.LEFT.isDown()) {
                mainSelect--;
                redraw = true;
            }
            if (Button.RIGHT.isDown()) {
                mainSelect++;
                redraw = true;
            }

            if (mainSelect < 0) mainSelect = 1;
            if (mainSelect > 1) mainSelect = 0;
        }
    }

    private void selectMotorMenu() throws InterruptedException {
        LCD.clear();
        int motorSelect = 1;
        Thread.sleep(menuWaitAfterButtonPress);
        redrawSingleMotorMenu(motorSelect);
        while (true) {

            if (Button.ENTER.isDown()) {
                switch (motorSelect) {
                    case 1:
                    case 2:
                    case 3: motorMove(motorSelect-1);break;
                }
                LCD.clear();
                redrawSingleMotorMenu(motorSelect);
                Thread.sleep(menuWaitAfterButtonPress);
            }
            if (Button.ESCAPE.isDown()) {
                break;
            }
            if (Button.LEFT.isDown()) {
                motorSelect--;
                redrawSingleMotorMenu(motorSelect);
                Thread.sleep(menuWaitAfterButtonPress);
            }
            if (Button.RIGHT.isDown()) {
                motorSelect++;
                redrawSingleMotorMenu(motorSelect);
                Thread.sleep(menuWaitAfterButtonPress);
            }

            if (motorSelect > 3) motorSelect = 1;
            if (motorSelect < 1) motorSelect = 3;
        }
    }

    private void redrawSingleMotorMenu(int motorSelect) {
        LCD.drawString("SELECT MOTOR", 0, 0);
        LCD.drawString("M1", 3, 1, motorSelect ==1);
        LCD.drawString("M2", 3, 2, motorSelect ==2);
        LCD.drawString("M3", 3, 3, motorSelect ==3);
    }

    private void motorMove(int motor) throws InterruptedException {
        LCD.clear();
        LCD.drawString("-M"+(motor+1)+" CONTROL-", 2, 0, true);
        Thread.sleep(menuWaitAfterButtonPress);

        MotorGroup mg = MotorGroup.getInstance();
        boolean leftWasDownLastTime=false;
        boolean rightWasDownLastTime=false;
        NXTCuddleMotor m = mg.getMotorByIndex(motor);
        int acc = 100;
        int decc = 400;
        int maxSpeed = 900;

        while (true) {
            LCD.drawString("Tacho: "+m.getTachoCount(),2,3);

            if (Button.ESCAPE.isDown()) {
                m.setSpeed(1);
                m.setAcceleration(decc);
                m.stop();
                m.flt();
                break;
            }
            if (Button.LEFT.isDown()) {
                if (!leftWasDownLastTime) {
                    m.setAcceleration(acc);
                    m.setSpeed(maxSpeed);
                    m.forward();
                    leftWasDownLastTime = true;
                }
            }
            else {
                if (leftWasDownLastTime) {
                    // Just released...
                    m.setSpeed(1);
                    m.setAcceleration(decc);
                    m.stop();
                    //m.flt();
                }
                leftWasDownLastTime = false;
            }
            if (Button.RIGHT.isDown()) {
                if (!rightWasDownLastTime) {
                    m.setAcceleration(acc);
                    m.setSpeed(maxSpeed);
                    m.backward();
                    rightWasDownLastTime = true;
                }
            }
            else {
                if (rightWasDownLastTime) {
                    // Just released...
                    m.setSpeed(1);
                    m.setAcceleration(decc);
                    m.stop();
                    //m.flt();
                }
                rightWasDownLastTime = false;
            }

        }
    }

    private void autoCalibrate() {
       /* TriangleMeasurer t = new TriangleMeasurer();
        t.moveToRestPoint(mgrp);*/
        cc.moveToRestpoint();
    }

}
