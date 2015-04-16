package dk.bjop.wirecuddler.movement.moves;

import dk.bjop.wirecuddler.math.XYZCoord;

/**
 * Created by bpeterse on 10-09-2014.
 */
public interface MotorPathMove {
    public int[] getExpectedTachoPosAtTimeT(long t);
    public void initialize(XYZCoord startPos, long starttime);
    public boolean isAfterMove(long t);
    public boolean isBeforeMove(long t);
    public void setEndtime(long endtime); // TODO remove this method and replace it with a terminateMove-method. That makes more sense.
}
