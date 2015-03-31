package dk.bjop.wirecuddler.movement.moves;

import dk.bjop.wirecuddler.math.XYZCoord;

/**
 * Created by bpeterse on 10-09-2014.
 */
public interface MotorPathMove {
    public int[] getExpectedTachoPosAtTimeT(long t);
    public boolean isAfterMove(long t);
    public boolean isBeforeMove(long t);
    public long getMoveEndtime();
    public void setMoveStarttime(long starttime);
    public long getMoveStartime();
    public long getMoveExpectedDuration();
    public XYZCoord getMoveTargetPos();
}
