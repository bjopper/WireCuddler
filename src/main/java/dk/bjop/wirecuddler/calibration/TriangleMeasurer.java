package dk.bjop.wirecuddler.calibration;

import dk.bjop.wirecuddler.motor.MotorGroup;
import dk.bjop.wirecuddler.motor.NXTCuddleMotor;
import dk.bjop.wirecuddler.sensors.SwitchListener;
import dk.bjop.wirecuddler.util.Utils;
import lejos.nxt.*;

/**
 * Created by bpeterse on 09-10-2014.
 */
public class TriangleMeasurer extends Thread implements SwitchListener{

    NXTCuddleMotor m1;
    NXTCuddleMotor m2;
    NXTCuddleMotor m3;
    //NXTCuddleMotor[] motors;
    int motorIndex = 0;
    int motorToTuneIndex = 0;

    static final int SWITCH_1 = 1;
    static final int SWITCH_2 = 2;
    TouchSensor ts1 = new TouchSensor(SensorPort.S1);
   // TouchSensor ts2 = new TouchSensor(SensorPort.S2);

    int acc = 50;
    float speed = 100;

    boolean forward = true;

    public TriangleMeasurer() {}

    public void moveToRestPoint(MotorGroup mgrp) {



       /* m1.setSpeed(200);
        m1.rotate(5000);
        m2.rotate(-700);
        m3.rotate(-700);*/

        mgrp.setAccelerationForAll(acc);
        mgrp.setSpeedForAll(speed);



        //Switch s1 = new Switch(SWITCH_1, SensorPort.S1);
        //Switch s2 = new Switch(SWITCH_2, SensorPort.S2);
        //s1.addListener(this);
        //s2.addListener(this);
        //s1.start();
        //s2.start();

        boolean targetReached = false;
        boolean wireToggler = true;
        boolean sensorFirstActivation = true;

        while (!Button.ESCAPE.isDown() && !targetReached) {

            // Start roll-in on some motor
            //NXTCuddleMotor tuneMotor = m1;//getMotorToTune();
            m1.backward();

            // Wait for tension to become strong enough to trigger sensor
            if (anyTouchSensorPressed()) {
                Utils.println("SENSOR ACTIVATED!!!");

                // Sensor triggered
                m1.flt(); // Stop the motor immediately

                // We do nor now anything about the state of the wires on startup. They may be relaxed which is why the first move of any actuator must always be a tighten. If we relax
                // an already relaxed wire the wires get entangled in the machine and the system fucks up.
                if (sensorFirstActivation) {
                    Utils.println("First activation protocol...");
                    loosenWire(m1, 480);

                    tightenWireUntilSensorActivated(m2);
                    loosenWireUntilSensorReleased(m2);

                    tightenWireUntilSensorActivated(m3);
                    loosenWireUntilSensorReleased(m3);
                    sensorFirstActivation = false;
                    continue;
                }

                // Try rollout of other engine (swith between them on each iteration)

                if (!loosenWireWithRollback(wireToggler ? m2 : m3, 720)) {
                    // It didnt. Try the other wire...
                    if (!loosenWireWithRollback(wireToggler ? m3 : m2, 720)) {

                        // Try loosen both wires
                        Utils.println("Loosening both wires...");
                        loosenWire(m2, 540);
                        loosenWire(m3, 540);

                        if (anyTouchSensorPressed()) {

                            // That didnt help either. From this we infer that the cuddle-object has reached the hookpoint of the motor were calibrating.
                            Utils.println("Sleep-point reached. Resetting tacho!");
                            // Wire's too tight, so we losen it a bit

                            Utils.println("Loosen wire until sensor released...");
                            loosenWireUntilSensorReleased(m1);

                            // The other wires may be loose so now we tighten these.
                            /*Utils.println("Tightening m2...");
                            tightenWireUntilSensorActivated(m2);
                            // And loosen it a bit
                            Utils.println("Loosening m2 again");
                            loosenWireUntilSensorReleased(m2);

                            // And we do the same for m3
                            Utils.println("Tightening m3...");
                            tightenWireUntilSensorActivated(m3);
                            // And loosen it a bit
                            Utils.println("Loosening m3 again");
                            loosenWireUntilSensorReleased(m3);*/

                            Utils.println("Loosen wire 360 degrees further");
                            loosenWire(m1, 720);

                            targetReached = true;
                            m1.resetTachoCount();
                            continue;
                        }
                    }
                }
                wireToggler = !wireToggler;
            }

            xsleep(25);
        }


    }

    private void xsleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void tightenWireUntilSensorActivated(NXTCuddleMotor m) {
        if (anyTouchSensorPressed()) return;

        m.backward();
        while (!ts1.isPressed()) {
            xsleep(25);
        }
        m.flt();
    }

    private void loosenWireUntilSensorReleased(NXTCuddleMotor m) {
        if (!anyTouchSensorPressed()) return;

        m.forward();
        while (ts1.isPressed()) {
            xsleep(25);
        }
        m.flt();
    }

    private void loosenWire(NXTCuddleMotor m, int degrees) {
        m.rotate(degrees);
    }

    private void tightenWire(NXTCuddleMotor m, int degrees) {
        m.rotate(-degrees);
    }

    private boolean loosenWireWithRollback(NXTCuddleMotor m, int degrees) {
        // Relax a wire to see if this was what triggered the sensor
        m.rotate(degrees);

        // Did it help?
        if (!anyTouchSensorPressed()) {
            // It did help
            return true;
        }
        else {
            // It didnt help. Roll back
            m.rotate(-degrees);
        }
        return false;
    }


    private boolean anyTouchSensorPressed() {
        return ts1.isPressed();
    }

    /*private int getNextMotorIndex() {
        motorIndex++;
        if (motorIndex == motors.length) return 0;
        else return motorIndex;
    }

    private int getPrevMotorIndex() {
        motorIndex--;
        if (motorIndex ==-1) return motors.length-1;
        else return motorIndex;
    }*/

    private int getMotorToTuneIndex() {
        return motorToTuneIndex;
    }

    /*private NXTCuddleMotor getNextMotor() {
        return motors[getNextMotorIndex()];
    }

    private NXTCuddleMotor getPrevMotor() {
        return motors[getPrevMotorIndex()];
    }

    private NXTCuddleMotor getCurrentMotor() {
        return motors[motorIndex];
    }

    private NXTCuddleMotor getMotorToTune() {
        return motors[motorToTuneIndex];
    }*/


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
        /*this.notify();
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

        }*/
    }
}
