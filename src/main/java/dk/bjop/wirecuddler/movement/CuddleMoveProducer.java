package dk.bjop.wirecuddler.movement;

import dk.bjop.wirecuddler.movement.moves.MotorPathMove;

/**
 * Created by bpeterse on 05-04-2015.
 */
public interface CuddleMoveProducer {
    MotorPathMove getNewMove();
    boolean hasMoreMoves();
}
