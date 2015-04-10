package dk.bjop.wirecuddler;

import dk.bjop.wirecuddler.calibration.RestPoint;
import dk.bjop.wirecuddler.config.CalibValues;
import dk.bjop.wirecuddler.math.Triangle;
import dk.bjop.wirecuddler.motor.MotorGroup;
import dk.bjop.wirecuddler.movement.CuddleMoveController;
import dk.bjop.wirecuddler.movement.CuddleMoveProducerFactory;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;

/**
 * Created by bpeterse on 26-11-2014.
 */
public class CuddleController {

    private TouchSensor ts1 = new TouchSensor(SensorPort.S1);
    private RestPoint rp = new RestPoint(ts1);
    private CuddleMoveController cmc;


    public CuddleController() {}

    public void moveToRestpoint() {
        rp.moveToRestPoint(MotorGroup.getInstance());
    }

    public void stopCuddle() {
        cmc.skipCurrentMoveAndReturnToInitialPosition();
    }

    public void doCuddle()throws InterruptedException {

        Triangle.createInstance(CalibValues.getInstance());
        MotorGroup mg = MotorGroup.getInstance();

        //TODO Fix lego brake - it has too much slack!


        cmc = new CuddleMoveController(mg);
        //cmc.setMoveProducer(CuddleMoveProducerFactory.getListBasedCMP(mg));
        cmc.setMoveProducer(CuddleMoveProducerFactory.getAutoRandomBasedCMP());
        cmc.start();
    }




    public TouchSensor getTouchSensor() {
        return ts1;
    }
}
