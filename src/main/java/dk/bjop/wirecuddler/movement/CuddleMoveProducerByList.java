package dk.bjop.wirecuddler.movement;

import dk.bjop.wirecuddler.math.Utils;
import dk.bjop.wirecuddler.movement.moves.MotorPathMove;

import java.util.ArrayList;

/**
 * Created by bpeterse on 05-04-2015.
 */
public class CuddleMoveProducerByList implements CuddleMoveProducer {

    private ArrayList<MotorPathMove> movesList;

    public CuddleMoveProducerByList(ArrayList<MotorPathMove> predefinedMoves) {
        this.movesList = predefinedMoves;
    }

    @Override
    public MotorPathMove getNewMove() {

        // TODO make thread safe

        MotorPathMove m = null;
        if (movesList.size() > 0) {
            m = movesList.remove(0);
        }
        return m;
    }

    @Override
    public boolean hasMoreMoves() {
        Utils.println("Moves-list size: "+movesList.size());
        return movesList.size() > 0;
    }

}
