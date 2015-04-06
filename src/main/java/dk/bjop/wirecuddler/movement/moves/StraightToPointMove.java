package dk.bjop.wirecuddler.movement.moves;

import dk.bjop.wirecuddler.math.SphericCoord;
import dk.bjop.wirecuddler.math.WT3Coord;
import dk.bjop.wirecuddler.math.XYZCoord;

/**
 * Created by bpeterse on 10-09-2014.
 */
public class StraightToPointMove implements MotorPathMove {

    float speedCmSec = 2;

    XYZCoord startPos = null;
    XYZCoord targetPos = null;
    float startToTargetDistance;
    long moveStartTime;
    long moveEndTime;
    long calculatedMoveTimeMillis;

    public StraightToPointMove(XYZCoord target){
        this.targetPos = target;
        startToTargetDistance = (float) startPos.distanceTo(targetPos);
        calculatedMoveTimeMillis = (long) ((startToTargetDistance / speedCmSec) * 1000f);
    }

    /**
     *
     * Set the move as started/initiated. At
     *
     * @param startPos
     */
    @Override
    public void initialize(XYZCoord startPos, long starttime) {
        this.startPos = startPos;
        this.moveStartTime = starttime;
    }

    @Override
    public int[] getExpectedTachoPosAtTimeT(long t) {
        if (startPos == null) throw new RuntimeException("Move not initialized!");

        long elapsedTimeMillis = t - moveStartTime;

        if (startPos == null || targetPos == null) throw new RuntimeException("startPos or targetPos is NULL!");

        float distSinceStartAtTimeT = (elapsedTimeMillis / 1000f) * speedCmSec;
        if (distSinceStartAtTimeT > startToTargetDistance) {
            distSinceStartAtTimeT = startToTargetDistance;
        }
        //TODO optimize this for speed
        XYZCoord g1 = targetPos.subtract(startPos);
        SphericCoord sp = g1.toSpheric();
        sp.r = distSinceStartAtTimeT;
        g1 = sp.toCartesian();
        XYZCoord f = g1.add(startPos);
        WT3Coord f2 = f.toWiresTachoCoord();
        return f2.getTachos();
    }

    @Override
    public boolean isAfterMove(long t) {
        if (startPos == null) throw new RuntimeException("Move not initialized!");
        return t > moveEndTime;
    }

    @Override
    public boolean isBeforeMove(long t) {
        if (startPos == null) throw new RuntimeException("Move not initialized!");
        return t < moveStartTime;
    }

   /* @Override
    public long getMoveEndtime() {
        return moveStartTime + getMoveExpectedDuration();
    }*/

    @Override
    public void setEndtime(long endtime) {
        this.moveEndTime = endtime;
    }

    /*@Override
    public void setMoveStarttime(long starttime) {
        if (startPos == null) throw new RuntimeException("Move not initialized!");
        moveStartTime = starttime;
    }

    @Override
    public long getMoveStartime() {
        if (startPos == null) throw new RuntimeException("Move not initialized!");
        return moveStartTime;
    }

    @Override
    public long getMoveExpectedDuration() {
        return calculatedMoveTimeMillis;
    }*/

    @Override
    public XYZCoord getMoveTargetPos() {
        return targetPos;
    }

}
