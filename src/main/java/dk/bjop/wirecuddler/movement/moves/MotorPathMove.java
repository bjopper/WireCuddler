package dk.bjop.wirecuddler.movement.moves;

import dk.bjop.wirecuddler.math.XYZCoord;

/**
 * Created by bpeterse on 10-09-2014.
 */
public interface MotorPathMove {
    public int[] getExpectedTachoPosAtTimeT(long elapsedTimeMillis);
    public boolean isMovementFinished(long elapsedTimeMillis);
    public void setStartPos(XYZCoord startPos);
    public XYZCoord getTargetPos();
}
