package dk.bjop.wirecuddler.movement.moveproducers;

import dk.bjop.wirecuddler.math.WT3Coord;
import dk.bjop.wirecuddler.math.XYZCoord;
import dk.bjop.wirecuddler.motor.MotorGroup;
import dk.bjop.wirecuddler.movement.moves.MotorPathMove;
import dk.bjop.wirecuddler.movement.moves.StraightLineMove;

import java.util.ArrayList;

/**
 * Created by bpeterse on 09-04-2015.
 */
public class CuddleMoveProducerFactory {

    public static CuddleMoveProducer getListBasedCMP(MotorGroup mg) {
        int height = 110;

        ArrayList<MotorPathMove> moves = new ArrayList<MotorPathMove>();
        moves.add(0, new StraightLineMove(new XYZCoord(120,height,70)));
        moves.add(1, new StraightLineMove(new XYZCoord(160,height,70)));
        moves.add(2, new StraightLineMove(new XYZCoord(40,height,30)));
        moves.add(3, new StraightLineMove(new WT3Coord(mg.getTachoCounts()).toCartesian()));

        return new CuddleMoveProducerByList(moves);
    }

    public static CuddleMoveProducer getAutoRandomBasedCMP() {
        return new CuddleMoveProducerGenerateRandomInRectangle();
    }
}
