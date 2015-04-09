package dk.bjop.wirecuddler.motor;

import dk.bjop.wirecuddler.math.Triangle;
import dk.bjop.wirecuddler.math.Utils;
import dk.bjop.wirecuddler.sensors.EmergencyBreak;
import dk.bjop.wirecuddler.sensors.EmergencyBreakListener;
import lejos.nxt.MotorPort;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;

/**
 * Created by bpeterse on 05-11-2014.
 */
public class MotorGroup implements EmergencyBreakListener{
    public static final int M1 = 0;
    public static final int M2 = 1;
    public static final int M3 = 2;

    NXTCuddleMotor[] motors;

    EmergencyBreak eb;
    boolean emergencyBreakEnabled = true;

    boolean positionKnown = false;

    private static MotorGroup instance;

    private int[] initialPosition = null;

    public static MotorGroup getInstance() {
        if (instance == null) {
            instance = new MotorGroup(Triangle.getInstance());
        }
        return instance;
    }

    private MotorGroup(Triangle tri) {
        motors = new NXTCuddleMotor[]{new NXTCuddleMotor(MotorPort.A), new NXTCuddleMotor(MotorPort.B), new NXTCuddleMotor(MotorPort.C)};

        // TODO fix these hardcoded settings
        this.setTachoCountOffsets(180, tri.getCalibValues().getP1P2tachoDist(), tri.getCalibValues().getP1P3tachoDist()); // Will set position flag to known!
        this.initialPosition = this.getTachoCounts();

        eb = new EmergencyBreak(1, SensorPort.S1);
        eb.addListener(this);
        eb.start();
    }

    public int[] getInitialPosition() {
        return initialPosition;
    }

    public NXTCuddleMotor getM1() {
        return motors[M1];
    }

    public NXTCuddleMotor getM2() {
        return motors[M2];
    }

    public NXTCuddleMotor getM3() {
        return motors[M3];
    }

    public NXTCuddleMotor getMotorByIndex(int indx) {
        if (indx >= 0 && indx < motors.length) return motors[indx];
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
        motors[M1].setTachoOffset(m1Offset);
        motors[M2].setTachoOffset(m2Offset);
        motors[M3].setTachoOffset(m3Offset);
        positionKnown = true;
    }

    public void resetAllTachoCounters() {
        for (int i = 0; i< motors.length;i++) {
            motors[i].resetTachoCount();
        }
    }

    public int[] getTachoCountOffsets() {
        return new int[]{motors[0].getTachoOffset(), motors[1].getTachoOffset(), motors[2].getTachoOffset()};
    }

    private void setEnabledAll(boolean enabled) {
        motors[M1].setEnabled(enabled);
        motors[M2].setEnabled(enabled);
        motors[M3].setEnabled(enabled);
    }

    public boolean positionKnown() {
        return positionKnown;
    }

    public void setTachoMax(int m1Max, int m2Max, int m3Max) {
        motors[M1].setTachoOffset(m1Max);
        motors[M2].setTachoOffset(m2Max);
        motors[M3].setTachoOffset(m3Max);
    }

    @Override
    public void switchPressed(int switchID) {
        if (!emergencyBreakEnabled) return;
        setEnabledAll(false);
        stopAll();
        Utils.println("Emergency break triggered. Disabling and stopping all motors.");
        Sound.beepSequence();
    }

    @Override
    public void switchReleased(int switchID) {
        if (!emergencyBreakEnabled) return;
        Utils.println("Emergency break released. We do nothing...");
    }

    public void setEmergencyBreakingEnabled(boolean enabled) {
        this.emergencyBreakEnabled = enabled;
    }

    public String tachosToString() {
        int[] t = this.getTachoCounts();
        String s = "(";
        for (int i=0;i<motors.length;i++) {
            s+=motors[i].getTachoCount()+", ";
        }
        s+=")";
        return s;
    }
}
