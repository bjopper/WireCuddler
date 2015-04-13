package dk.bjop.wirecuddler.movement;

import dk.bjop.wirecuddler.motor.LookAheadCuddleMotorController;

/**
 * Created by bpeterse on 10-01-2015.
 */
public interface TachoPositionController {
    public int getTachoPositionAtTimeT(long lookaheadMillis, LookAheadCuddleMotorController mpc) throws PosNotAvailableException;
    public void waitForMove(LookAheadCuddleMotorController m);
}
