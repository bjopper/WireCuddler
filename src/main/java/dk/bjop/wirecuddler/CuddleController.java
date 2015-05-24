package dk.bjop.wirecuddler;

import dk.bjop.wirecuddler.config.CalibValues;
import dk.bjop.wirecuddler.config.CuddleProfile;
import dk.bjop.wirecuddler.math.Utils;
import dk.bjop.wirecuddler.math.coordinates.XYZCoord;
import dk.bjop.wirecuddler.math.geometry.BaseGeometry;
import dk.bjop.wirecuddler.math.coordinates.WT3Coord;
import dk.bjop.wirecuddler.motor.MotorGroup;
import dk.bjop.wirecuddler.movement.movecontrol.CuddleMoveController;
import dk.bjop.wirecuddler.movement.moveproducers.CuddleMoveProducer;
import dk.bjop.wirecuddler.movement.moveproducers.CuddleMoveProducerFactory;
import dk.bjop.wirecuddler.movement.moves.MotorPathMove;
import dk.bjop.wirecuddler.movement.moves.PointMove;

/**
 * Created by bpeterse on 26-11-2014.
 */
public class CuddleController {

    private CuddleMoveController cmc;


    public CuddleController() {}

    public void initialize() {
        ensureInitialized();
    }

    public void moveToRestpoint() {
        ensureInitialized();
        MotorPathMove m = new PointMove(new WT3Coord(MotorGroup.getInstance().getInitialPosition()).toCartesian());
        m.initialize(new WT3Coord(MotorGroup.getInstance().getTachoCounts()).toCartesian(), System.currentTimeMillis());
        cmc.runMove(m, false);
    }

    public void stopCuddle() {
        cmc.stopController();
    }

    public void doCuddle(CuddleProfile cp ) {
        ensureInitialized();

        //cmc.attachMoveProducer(CuddleMoveProducerFactory.getListBasedCMP(mg));
        //cmc.runMoves(CuddleMoveProducerFactory.getAutoRandomBasedCMP(), false);

        CuddleMoveProducer cmp = CuddleMoveProducerFactory.getAutoRandomByProfile(cp);

        XYZCoord pos = new WT3Coord(MotorGroup.getInstance().getTachoCounts()).toCartesian();
        for (int i = 0;i<20;i++) {
            Utils.println("----------------------------------------------------------\n");
            MotorPathMove m = cmp.getNewMove();
            Utils.println("CMP move was:");
            Utils.println(m.getTarget().toString());
        }

        WireCuddler.terminateProgram("Testing CMP");

        cmc.runMoves(CuddleMoveProducerFactory.getAutoRandomByProfile(cp), false);
    }

    public void manualMove(MotorPathMove move, boolean awaitMoveCompletion) {
        ensureInitialized();
        cmc.runMove(move, false);
        if (awaitMoveCompletion) cmc.waitForAllMovesCompleted();
    }

    private void ensureInitialized() {
        if (cmc == null) {
            BaseGeometry.createInstance(CalibValues.getInstance());
            MotorGroup mg = MotorGroup.getInstance();

            //TODO Fix lego brake - it has too much slack!

            cmc = new CuddleMoveController(mg);
            cmc.start();
        }
    }

}
