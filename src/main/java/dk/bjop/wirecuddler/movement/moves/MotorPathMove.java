package dk.bjop.wirecuddler.movement.moves;

import dk.bjop.wirecuddler.math.XYZCoord;

/**
 * Created by bpeterse on 10-09-2014.
 */
public interface MotorPathMove {
    public int[] getExpectedTachoPosAtTimeT(long t);
    public void initialize(XYZCoord startPos, long starttime);
    public boolean isMoveDone(long t);
    public void setMoveTerminate();
}
