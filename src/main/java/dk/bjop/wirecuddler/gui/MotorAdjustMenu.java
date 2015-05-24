package dk.bjop.wirecuddler.gui;

import dk.bjop.wirecuddler.CuddleController;
import dk.bjop.wirecuddler.motor.NXTCuddleMotor;
import lejos.nxt.Button;
import lejos.nxt.LCD;

/**
 * Created by bpeterse on 23-05-2015.
 */
public class MotorAdjustMenu {


    final int menuWaitAfterButtonPress = 500;
    CuddleController cc;

    public MotorAdjustMenu(CuddleController cc) {
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
        LCD.clear();
        int motorSelect = 1;
        Thread.sleep(menuWaitAfterButtonPress);
        redrawSingleMotorMenu(motorSelect);
        while (true) {

            if (Button.ENTER.isDown()) {
                switch (motorSelect) {
                    case 1:
                    case 2:
                    case 3: motorMove(motorSelect);break;
                }
                LCD.clear();
                redrawSingleMotorMenu(motorSelect);
            }
            if (Button.ESCAPE.isDown()) {
                while (Button.ESCAPE.isDown()) Thread.sleep(10);
                break;
            }
            if (Button.LEFT.isDown()) {
                motorSelect = getPrevIndex(1, 4, motorSelect);
                redrawSingleMotorMenu(motorSelect);
                while (Button.LEFT.isDown()) Thread.sleep(10);
            }
            if (Button.RIGHT.isDown()) {
                motorSelect = getNextIndex(1, 4, motorSelect);
                redrawSingleMotorMenu(motorSelect);
                while (Button.RIGHT.isDown()) Thread.sleep(10);
            }
        }
    }

    private void redrawSingleMotorMenu(int motorSelect) {
        LCD.drawString("   SELECT MOTOR", 0, 0);
        LCD.drawString("Motor A", 4, 2, motorSelect ==1);
        LCD.drawString("Motor B", 4, 3, motorSelect ==2);
        LCD.drawString("Motor C", 4, 4, motorSelect ==3);
    }

    private NXTCuddleMotor getMotor(int index) {
        switch (index) {
            case 1: return new NXTCuddleMotor(NXTCuddleMotor.MotorID.M1);
            case 2: return new NXTCuddleMotor(NXTCuddleMotor.MotorID.M2);
            case 3: return new NXTCuddleMotor(NXTCuddleMotor.MotorID.M3);
        }
        return null;
    }

    private void motorMove(int motor) throws InterruptedException {
        LCD.clear();
        LCD.drawString("-M"+(motor)+" CONTROL-", 2, 0, true);
        Thread.sleep(menuWaitAfterButtonPress);

        NXTCuddleMotor m = getMotor(motor);


        boolean leftWasDownLastTime=false;
        boolean rightWasDownLastTime=false;
        int acc = 100;
        int decc = 400;
        int maxSpeed = 900;

        while (true) {
            LCD.drawString("Tacho: "+m.getTachoCount(),3,4);

            if (Button.ESCAPE.isDown()) {
                m.setSpeed(1);
                m.setAcceleration(decc);
                m.stop();
                m.flt();
                while (Button.ESCAPE.isDown()) Thread.sleep(10);
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

}
