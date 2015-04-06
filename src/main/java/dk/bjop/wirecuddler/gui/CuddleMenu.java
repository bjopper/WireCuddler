package dk.bjop.wirecuddler.gui;

import dk.bjop.wirecuddler.CuddleController;
import dk.bjop.wirecuddler.motor.MotorGroup;
import dk.bjop.wirecuddler.motor.NXTCuddleMotor;
import dk.bjop.wirecuddler.math.Utils;
import lejos.nxt.*;

/**
 * Created by bpeterse on 03-11-2014.
 */
public class CuddleMenu {

    boolean[] homeVisited = new boolean[]{false, false, false};
    final int menuWaitAfterButtonPress = 500;

    CuddleController cc = new CuddleController();


    public CuddleMenu() {
        // M2-M1: 13114
        // M1-M3: 13428
        // M3-M2: 10537
    }

    public void mainMenu() throws InterruptedException {
        boolean redraw = true;
        int mainSelect = 0;
        while (true) {

            if (redraw) {
                LCD.clear();
                LCD.drawString("Cuddle", 0, 0, mainSelect==0);
                LCD.drawString("Calibration", 0, 1, mainSelect==1);
                redraw = false;
            }

            if (Button.ENTER.isDown()) {
                if (mainSelect == 0 ) cuddle();
                if (mainSelect == 1 ) calibMenu();

                redraw = true;
                Thread.sleep(menuWaitAfterButtonPress);
            }
            if (Button.LEFT.isDown()) {
                mainSelect--;
                redraw = true;
            }
            if (Button.RIGHT.isDown()) {
                mainSelect++;
                redraw = true;
            }

            if (mainSelect < 0) mainSelect = 0;
            if (mainSelect > 1) mainSelect = 1;
        }
    }

    private void calibMenu() throws InterruptedException {
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

            if (mainSelect < 0) mainSelect = 0;
            if (mainSelect > 1) mainSelect = 1;
        }
    }

    private void cuddle() {

        /*Utils.println("Cuddling not implemented! Exiting...");
        LCD.clear();
        LCD.drawString("THE END", 3, 3);*/


        try {
            cc.doCuddle();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // System.exit(0);
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
                    case 4: motorMoveDual(0,1);break;
                    case 5: motorMoveDual(0,2);break;
                    case 6: motorMoveDual(1,2);break;
                }
                manualCalibMenu(motorSelect);
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

            if (motorSelect > 6) motorSelect = 6;
            if (motorSelect < 1) motorSelect = 1;
        }
    }

    private void redrawSingleMotorMenu(int motorSelect) {
        LCD.drawString("SELECT MOTOR", 0, 0);
        LCD.drawString("M1", 3, 1, motorSelect ==1);
        LCD.drawString("M2", 3, 2, motorSelect ==2);
        LCD.drawString("M3", 3, 3, motorSelect ==3);
        LCD.drawString("M1-M2", 3, 4, motorSelect ==4);
        LCD.drawString("M1-M3", 3, 5, motorSelect ==5);
        LCD.drawString("M2-M3", 3, 6, motorSelect ==6);
    }

    private void manualCalibMenu(int motor) throws InterruptedException {
        LCD.clear();
        boolean redraw = true;
        int menuIndex = 1;

        String motorStr = "M"+motor;

        Thread.sleep(menuWaitAfterButtonPress);
        while (true) {

            if (redraw) {
                LCD.drawString("XXXXXXX", 0, 0);
                LCD.drawString("Adjust pos of "+motorStr, 0, 2, menuIndex ==1);
                LCD.drawString("Register "+motorStr+" home pos", 0, 3, menuIndex ==2);
                LCD.drawString("Set pos as restpoint", 0, 4, menuIndex ==3);
                redraw = false;
            }
            if (Button.ENTER.isDown()) {
                switch (menuIndex) {
                    case 1: motorMove(motor-1);
                        break;
                    case 2: setHomePos(motor-1);
                        break;
                    case 3: setRestpoint(motor-1);
                        break;
                }

                LCD.clear();
                redraw = true;
                Thread.sleep(menuWaitAfterButtonPress);
            }
            if (Button.ESCAPE.isDown()) {
                break;
            }
            if (Button.LEFT.isDown()) {
                menuIndex--;
                redraw = true;
                Thread.sleep(menuWaitAfterButtonPress);
            }
            if (Button.RIGHT.isDown()) {
                menuIndex++;
                redraw = true;
                Thread.sleep(menuWaitAfterButtonPress);
            }

            if (menuIndex > 3) menuIndex = 3;
            if (menuIndex < 1) menuIndex = 1;
        }
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

        TouchSensor ts1 = cc.getTouchSensor();

        while (true) {

            LCD.drawString("Tacho: "+m.getTachoCount(),2,3);

            if (ts1.isPressed()) {
                Sound.beepSequence();
                Utils.println("Sensor activated!!!!!");
            }


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

    private void motorMoveDual(int motor1, int motor2) throws InterruptedException {
        LCD.clear();
        LCD.drawString("-M"+(motor1)+"-M"+(motor2)+" CONTROL-", 2, 0, true);
        Thread.sleep(menuWaitAfterButtonPress);

        MotorGroup mg = MotorGroup.getInstance();
        boolean leftWasDownLastTime=false;
        boolean rightWasDownLastTime=false;
        NXTCuddleMotor m1 = mg.getMotorByIndex(motor1);
        NXTCuddleMotor m2 = mg.getMotorByIndex(motor2);
        int acc = 100;
        int decc = 500;
        int maxSpeed = 900;

        TouchSensor ts1 = new TouchSensor(SensorPort.S1);

        while (true) {

            LCD.drawString("Tacho (M"+(motor1)+"): "+m1.getTachoCount(),2,3);
            LCD.drawString("Tacho (M"+(motor2)+"): "+m1.getTachoCount(),2,4);

            if (ts1.isPressed()) {
                Sound.beepSequence();
                Utils.println("Sensor activated!!!!!");
            }


            if (Button.ESCAPE.isDown()) {
                m1.setSpeed(1);
                m1.setAcceleration(decc);
                m1.stop();
                m1.flt();
                m2.setSpeed(1);
                m2.setAcceleration(decc);
                m2.stop();
                m2.flt();
                break;
            }
            if (Button.LEFT.isDown()) {
                if (!leftWasDownLastTime) {
                    m1.setAcceleration(acc);
                    m1.setSpeed(maxSpeed);
                    m1.forward();

                    m2.setAcceleration(acc);
                    m2.setSpeed(maxSpeed);
                    m2.backward();
                    leftWasDownLastTime = true;
                }
            }
            else {
                if (leftWasDownLastTime) {
                    // Just released...
                    m1.setSpeed(1);
                    m1.setAcceleration(decc);
                    m1.stop();
                    m2.setSpeed(1);
                    m2.setAcceleration(decc);
                    m2.stop();
                    //m.flt();
                }
                leftWasDownLastTime = false;
            }
            if (Button.RIGHT.isDown()) {
                if (!rightWasDownLastTime) {
                    m1.setAcceleration(acc);
                    m1.setSpeed(maxSpeed);
                    m1.backward();

                    m2.setAcceleration(acc);
                    m2.setSpeed(maxSpeed);
                    m2.forward();
                    rightWasDownLastTime = true;
                }
            }
            else {
                if (rightWasDownLastTime) {
                    // Just released...
                    m1.setSpeed(1);
                    m1.setAcceleration(decc);
                    m1.stop();
                    m2.setSpeed(1);
                    m2.setAcceleration(decc);
                    m2.stop();
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

    private void setHomePos(int motor) {
        MotorGroup mg = MotorGroup.getInstance();
        Utils.println("Home tacho pos for M"+(motor+1)+": "+mg.getMotorByIndex(motor).getTachoCount()+" resetting now...");

        mg.getMotorByIndex(motor).resetTachoCount();

        for (int i = 0;i<homeVisited.length;i++) {
            if (homeVisited[i]) {
                Utils.println("Tacho-distance from M"+(i+1)+" to M"+(motor+1)+": "+mg.getMotorByIndex(i).getTachoCount());
            }
        }

        homeVisited[motor] = true;
    }

    private void setRestpoint(int motor) {
        Utils.println("Registering M"+motor+" (motor #"+(motor-1)+") as system restpoint");
    }
}
