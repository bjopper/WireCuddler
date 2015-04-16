package dk.bjop.wirecuddler;

import dk.bjop.wirecuddler.calibration.RestPoint;
import dk.bjop.wirecuddler.config.CalibValues;
import dk.bjop.wirecuddler.math.Triangle;
import dk.bjop.wirecuddler.motor.MotorGroup;
import dk.bjop.wirecuddler.movement.CuddleMoveController;
import dk.bjop.wirecuddler.movement.moveproducers.CuddleMoveProducerFactory;
import dk.bjop.wirecuddler.movement.moves.OperatedMove;
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
        ensureInitialized();
        cmc.skipCurrentMoveAndReturnToInitialPosition();
    }

    public void stopCuddle() {
        cmc.skipCurrentMoveAndReturnToInitialPosition();
    }

    public void doCuddle() {
        ensureInitialized();
        //cmc.setMoveProducer(CuddleMoveProducerFactory.getListBasedCMP(mg));
        cmc.setMoveProducer(CuddleMoveProducerFactory.getAutoRandomBasedCMP());
        cmc.start();
    }

    public void doOperatedMove(OperatedMove move) {
        ensureInitialized();
        cmc.setMoveProducer(CuddleMoveProducerFactory.getOperatedCMP(move));
        cmc.start();
    }

    private void ensureInitialized() {
        if (cmc == null) {
            Triangle.createInstance(CalibValues.getInstance());
            MotorGroup mg = MotorGroup.getInstance();

            //TODO Fix lego brake - it has too much slack!

            cmc = new CuddleMoveController(mg);
        }
    }

}
