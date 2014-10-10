package dk.bjop.wirecuddler.calibration;

import dk.bjop.wirecuddler.Switch;
import dk.bjop.wirecuddler.SwitchListener;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;

/**
 * Created by bpeterse on 09-10-2014.
 */
public class TriangleMeasurer implements SwitchListener{

    NXTRegulatedMotor m1;
    NXTRegulatedMotor m2;
    NXTRegulatedMotor m3;
    NXTRegulatedMotor[] motors;
    NXTRegulatedMotor activeMotor;
    int motorIndex = 0;

    static final int SWITCH_1 = 1;
    static final int SWITCH_2 = 2;

    int acc = 50;
    float speed = 150;

    public TriangleMeasurer() {

    }

    public void measure() {


        m1 = new NXTRegulatedMotor(MotorPort.A);
        m2 = new NXTRegulatedMotor(MotorPort.B);
        m3 = new NXTRegulatedMotor(MotorPort.C);
        motors = new NXTRegulatedMotor[]{m1, m2, m3};

        m1.setAcceleration(acc);
        m2.setAcceleration(acc);
        m3.setAcceleration(acc);

        Switch s1 = new Switch(SWITCH_1, SensorPort.S1);
        Switch s2 = new Switch(SWITCH_2, SensorPort.S2);
        s1.addListener(this);
        s2.addListener(this);
        s1.start();
        s2.start();

        // Start roll-in on motor 1
       // activeMotor = m1;
        //m1.setSpeed(speed);
        //m1.forward();

    }

    @Override
    public void switchPressed(int switchID) {
        if (switchID == SWITCH_1) {
            m1.stop();
            m2.stop();
            m3.stop();
        }


        // Have we reached the target point, or are the other wires holding us back?
    }

    @Override
    public void switchReleased(int switchID) {
        // reset tachocount on active motor
        if (switchID == SWITCH_2) {
            m1.stop();
            m2.stop();
            m3.stop();

            motorIndex++;
            if (motorIndex == motors.length) motorIndex = 0;

            motors[motorIndex].setAcceleration(acc);
            motors[motorIndex].setSpeed(speed);
            motors[motorIndex].forward();

        }
    }
}
