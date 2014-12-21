package dk.bjop.wirecuddler.motor;

import lejos.nxt.MotorPort;

/**
 * Created by bpeterse on 05-11-2014.
 */
public class MotorGroup {

    NXTCuddleMotor[] motors;

    public MotorGroup() {
        motors = new NXTCuddleMotor[]{new NXTCuddleMotor(MotorPort.A), new NXTCuddleMotor(MotorPort.B), new NXTCuddleMotor(MotorPort.C)};
    }

    public NXTCuddleMotor getM1() {
        return motors[0];
    }

    public NXTCuddleMotor getM2() {
        return motors[1];
    }

    public NXTCuddleMotor getM3() {
        return motors[2];
    }

    public NXTCuddleMotor getMotorByIndex(int indx) {
        if (indx > 0 && indx < motors.length) return motors[indx];
        else return null;
    }

    public void setSpeedForAll(float speed) {
        for (int i = 0; i< motors.length;i++) {
            motors[i].setSpeed(speed);
        }
    }

    public void setAccelerationForAll(int acc) {
        for (int i = 0; i< motors.length;i++) {
            motors[i].setAcceleration(acc);
        }
    }

    public void fltAll() {
        for (int i = 0; i< motors.length;i++) {
            motors[i].flt();
        }
    }

    public void stopAll() {
        for (int i = 0; i< motors.length;i++) {
            motors[i].stop();
        }
    }

    public void setDirectionForwardForAll(boolean forward) {
        for (int i = 0; i< motors.length;i++) {
            motors[i].setDirectionForward(forward);
        }
    }

    public int[] getTachoCounts() {
        int[] t = new int[motors.length];
        for (int i=0;i<motors.length;i++) {
            t[i] = motors[i].getTachoCount();
        }
        return t;
    }

    public void setTachoCountOffsets(int m1Offset, int m2Offset, int m3Offset) {
        motors[0].setTachoOffset(m1Offset);
        motors[1].setTachoOffset(m2Offset);
        motors[2].setTachoOffset(m3Offset);
    }

    public void setTachoMax(int m1Max, int m2Max, int m3Max) {
        motors[0].setTachoOffset(m1Max);
        motors[1].setTachoOffset(m2Max);
        motors[2].setTachoOffset(m3Max);
    }
}
