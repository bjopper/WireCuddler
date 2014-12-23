package dk.bjop.wirecuddler;

import dk.bjop.wirecuddler.motor.MotorGroup;
import dk.bjop.wirecuddler.motor.RestPoint;
import dk.bjop.wirecuddler.util.Utils;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;

/**
 * Created by bpeterse on 26-11-2014.
 */
public class CuddleController {
    private MotorGroup mg = new MotorGroup();
    private TouchSensor ts1 = new TouchSensor(SensorPort.S1);
    private RestPoint rp = new RestPoint(ts1);

    public CuddleController() {
        // verify calib-values validity
    }

    public void moveToRestpoint() {
        rp.moveToRestPoint(mg);
    }


    public void doCuddle() {
        Utils.println("doCuddle - NOT IMPLEMENTED!!!");
    }

    private void xx() {

    }


    public MotorGroup getMotorgroup() {
        return mg;
    }

    public TouchSensor getTouchSensor() {
        return ts1;
    }
}
