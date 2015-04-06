package dk.bjop.wirecuddler.movement;

import dk.bjop.wirecuddler.math.Utils;
import dk.bjop.wirecuddler.movement.moves.MotorPathMove;

import java.util.ArrayList;

/**
 * Created by bpeterse on 05-04-2015.
 */
public class CuddleMoveProducerByList implements CuddleMoveProducer {

    private ArrayList<MotorPathMove> movesList;

    private boolean stopRequested = false;

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
        return !stopRequested && movesList.size() > 0;
    }

    @Override
    public void stopProduction() {
        stopRequested = true;
    }
}
