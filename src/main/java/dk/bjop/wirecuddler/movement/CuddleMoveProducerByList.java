package dk.bjop.wirecuddler.movement;

import dk.bjop.wirecuddler.movement.moves.MotorPathMove;

import java.util.ArrayList;

/**
 * Created by bpeterse on 05-04-2015.
 */
public class CuddleMoveProducerByList implements CuddleMoveProducer {

    ArrayList<MotorPathMove> movesList;

    public CuddleMoveProducerByList(ArrayList<MotorPathMove> predefinedMoves) {
        this.movesList = predefinedMoves;
    }

    @Override
    public MotorPathMove getNewMove() {
        MotorPathMove m = null;
        if (movesList.size() > 0) {
            m = movesList.remove(0);
        }
        return m;
    }
}
