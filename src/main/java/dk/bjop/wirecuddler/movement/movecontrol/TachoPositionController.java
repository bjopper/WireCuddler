package dk.bjop.wirecuddler.movement.movecontrol;

/**
 * Created by bpeterse on 10-01-2015.
 */
public interface TachoPositionController {
    public Integer getTachoPositionAtTimeT(long lookaheadMillis, LookAheadCuddleMotorController mpc);
    public void waitForMove(LookAheadCuddleMotorController m);
}
