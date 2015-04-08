package dk.bjop.wirecuddler.calibration;

import dk.bjop.wirecuddler.math.Triangle;
import dk.bjop.wirecuddler.math.Utils;
import dk.bjop.wirecuddler.motor.MotorGroup;
import dk.bjop.wirecuddler.motor.NXTCuddleMotor;
import lejos.nxt.Button;
import lejos.nxt.TouchSensor;

/**
 * Created by bpeterse on 14-11-2014.
 */
public class RestPoint {
    TouchSensor ts1;

    // TODO Check if it causes problems that both the EmergencyBrake object and this one uses the sensor. I'll bet i does...
    public RestPoint(TouchSensor ts1) {
        this.ts1 = ts1;
    }

    public void getRestPointCoordinates() {

    }

    public void moveToRestPoint(MotorGroup mgp) {

        int acc = 50;
        float speed = 100;

        mgp.setAccelerationForAll(acc);
        mgp.setSpeedForAll(speed);

        boolean targetReached = false;
        boolean wireToggler = true;
        boolean sensorFirstActivation = true;

        mgp.setEmergencyBreakingEnabled(false);

        while (!Button.ESCAPE.isDown() && !targetReached) {

            // Start roll-in on some motor
            //NXTCuddleMotor tuneMotor = m1;//getMotorToTune();
            mgp.getM1().backward();

            // Wait for tension to become strong enough to trigger sensor
            if (ts1.isPressed()) {
                Utils.println("SENSOR ACTIVATED!!!");

                // Sensor triggered
                mgp.getM1().flt(); // Stop the motor immediately

                // We do nor now anything about the state of the wires on startup. They may be relaxed which is why the first move of any actuator must always be a tighten. If we relax
                // an already relaxed wire the wires get entangled in the machine and the system fucks up.
                if (sensorFirstActivation) {
                    Utils.println("First activation protocol...");
                    loosenWire(mgp.getM1(), 480);

                    tightenWireUntilSensorActivated(mgp.getM2());
                    loosenWireUntilSensorReleased(mgp.getM2());

                    tightenWireUntilSensorActivated(mgp.getM3());
                    loosenWireUntilSensorReleased(mgp.getM3());
                    sensorFirstActivation = false;
                    continue;
                }

                // Try rollout of other engine (swith between them on each iteration)

                if (!loosenWireWithRollback(wireToggler ? mgp.getM2() : mgp.getM3(), 720)) {
                    // It didnt. Try the other wire...
                    if (!loosenWireWithRollback(wireToggler ? mgp.getM3() : mgp.getM2(), 720)) {

                        // Try loosen both wires
                        Utils.println("Loosening both wires...");
                        loosenWire(mgp.getM2(), 540);
                        loosenWire(mgp.getM3(), 540);

                        if (ts1.isPressed()) {

                            // That didnt help either. From this we infer that the cuddle-object has reached the hookpoint of the motor were calibrating.
                            Utils.println("Sleep-point reached. Resetting tacho!");
                            // Wire's too tight, so we losen it a bit

                            Utils.println("Loosen wire until sensor released...");
                            loosenWireUntilSensorReleased(mgp.getM1());

                            Utils.println("Loosen wire 360 degrees further");
                            loosenWire(mgp.getM1(), 720);

                            targetReached = true;

                            Triangle tri = Triangle.getInstance();

                            mgp.resetAllTachoCounters();
                            mgp.setTachoCountOffsets(360, tri.getCalibValues().getP1P2tachoDist(), tri.getCalibValues().getP1P3tachoDist());

                            continue;
                        }
                    }
                }
                wireToggler = !wireToggler;
            }

            xsleep(25);
        }

        mgp.setEmergencyBreakingEnabled(true);
    }

    private void xsleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void tightenWireUntilSensorActivated(NXTCuddleMotor m) {
        if (ts1.isPressed()) return;

        m.backward();
        while (!ts1.isPressed()) {
            xsleep(25);
        }
        m.flt();
    }

    private void loosenWireUntilSensorReleased(NXTCuddleMotor m) {

        //TODO Is has been observed that the sensor is never released, though the tightening of exavctly this wire is what caused the sensor to be pressed. This must be handled...
        if (!ts1.isPressed()) return;

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
        if (!ts1.isPressed()) {
            // It did help
            return true;
        }
        else {
            // It didnt help. Roll back
            m.rotate(-degrees);
        }
        return false;
    }

}
