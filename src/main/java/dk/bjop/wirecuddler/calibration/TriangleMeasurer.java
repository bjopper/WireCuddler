package dk.bjop.wirecuddler.calibration;

import dk.bjop.wirecuddler.sensors.SwitchListener;
import dk.bjop.wirecuddler.util.Utils;
import lejos.nxt.*;

/**
 * Created by bpeterse on 09-10-2014.
 */
public class TriangleMeasurer extends Thread implements SwitchListener{

    NXTRegulatedMotor m1;
    NXTRegulatedMotor m2;
    NXTRegulatedMotor m3;
    NXTRegulatedMotor[] motors;
    int motorIndex = 0;
    int motorToTuneIndex = 0;

    static final int SWITCH_1 = 1;
    static final int SWITCH_2 = 2;
    TouchSensor ts1 = new TouchSensor(SensorPort.S1);
    TouchSensor ts2 = new TouchSensor(SensorPort.S2);

    int acc = 50;
    float speed = 100;

    boolean forward = true;

    public TriangleMeasurer() {}

    public void moveToRestPoint() {

        m1 = new NXTRegulatedMotor(MotorPort.A);
        m2 = new NXTRegulatedMotor(MotorPort.B);
        m3 = new NXTRegulatedMotor(MotorPort.C);
        motors = new NXTRegulatedMotor[]{m1, m2, m3};

        m1.setSpeed(500);
        m1.rotate(-10000);
        m2.rotate(2000);
        m3.rotate(2000);

        initializeMotors();



        //Switch s1 = new Switch(SWITCH_1, SensorPort.S1);
        //Switch s2 = new Switch(SWITCH_2, SensorPort.S2);
        //s1.addListener(this);
        //s2.addListener(this);
        //s1.start();
        //s2.start();

        boolean targetReached = false;
        boolean wireToggler = true;

        while (!Button.ESCAPE.isDown() && !targetReached) {

            // Start roll-in on some motor
            NXTRegulatedMotor tuneMotor = m1;//getMotorToTune();
            tuneMotor.forward();

            // Wait for tension to become strong enough to trigger sensor
            if (ts1.isPressed() || ts2.isPressed()) {
                Utils.println("SENSOR ACTIVATED!!!");

                // Sensor triggered
                tuneMotor.flt(); // Stop the motor immediately

                // Try rollout of other engine (swith between them on each iteration)

                if (!loosenWireWithRollback(wireToggler ? m2 : m3, 1080)) {
                    // It didnt. Try the other wire...
                    if (!loosenWireWithRollback(wireToggler ? m3 : m2, 1080)) {
                        // That didnt help either. From this we infer that the cuddle-object has reached the hookpoint of the motor were calibrating.
                        tuneMotor.resetTachoCount();
                        Utils.println("Sleep-point reached. Resetting tacho!");
                        m1.rotate(-1440);
                        targetReached = true;
                        continue;
                    }
                }
                wireToggler = !wireToggler;
            }

            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean loosenWireWithRollback(NXTRegulatedMotor m, int degrees) {
        // Relax a wire to see if this was what triggered the sensor
        m.rotate(-degrees);

        // Did it help?
        if (!anyTouchSensorPressed()) {
            // It did help
            return true;
        }
        else {
            // It didnt help. Roll back
            m.rotate(degrees);
        }
        return false;
    }

    private void initializeMotors() {
        m1.setAcceleration(acc);
        m2.setAcceleration(acc);
        m3.setAcceleration(acc);

        m1.setSpeed(speed);
        m2.setSpeed(speed);
        m3.setSpeed(speed);
    }

    private boolean anyTouchSensorPressed() {
        return ts1.isPressed() || ts2.isPressed();
    }

    private int getNextMotorIndex() {
        motorIndex++;
        if (motorIndex == motors.length) return 0;
        else return motorIndex;
    }

    private int getPrevMotorIndex() {
        motorIndex--;
        if (motorIndex ==-1) return motors.length-1;
        else return motorIndex;
    }

    private int getMotorToTuneIndex() {
        return motorToTuneIndex;
    }

    private NXTRegulatedMotor getNextMotor() {
        return motors[getNextMotorIndex()];
    }

    private NXTRegulatedMotor getPrevMotor() {
        return motors[getPrevMotorIndex()];
    }

    private NXTRegulatedMotor getCurrentMotor() {
        return motors[motorIndex];
    }

    private NXTRegulatedMotor getMotorToTune() {
        return motors[motorToTuneIndex];
    }


    @Override
    public synchronized void switchPressed(int switchID) {
        this.notify();
        if (switchID == SWITCH_1) {
            m1.flt();
            m2.flt();
            m3.flt();
            forward = !forward;
        }


        // Have we reached the target point, or are the other wires holding us back?
    }

    @Override
    public synchronized void switchReleased(int switchID) {
        this.notify();
        // reset tachocount on active motor
        if (switchID == SWITCH_2) {
            m1.stop();
            m2.stop();
            m3.stop();

            motorIndex++;
            if (motorIndex == motors.length) motorIndex = 0;

            motors[motorIndex].setAcceleration(acc);
            motors[motorIndex].setSpeed(speed);
            if (forward) motors[motorIndex].forward();
            else motors[motorIndex].backward();

        }
    }
}
