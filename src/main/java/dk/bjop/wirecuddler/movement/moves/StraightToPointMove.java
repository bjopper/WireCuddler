package dk.bjop.wirecuddler.movement.moves;

import dk.bjop.wirecuddler.math.SphericCoord;
import dk.bjop.wirecuddler.math.Utils;
import dk.bjop.wirecuddler.math.WT3Coord;
import dk.bjop.wirecuddler.math.XYZCoord;
import dk.bjop.wirecuddler.motor.MotorGroup;

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
    //long calculatedMoveTimeMillis;

    public StraightToPointMove(XYZCoord target){
        this.targetPos = target;

        //calculatedMoveTimeMillis = (long) ((startToTargetDistance / speedCmSec) * 1000f);
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

        startToTargetDistance = (float) startPos.distanceTo(targetPos);
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

    int x = 0;
    @Override
    public boolean isAfterMove(long t) {
        if (startPos == null) throw new RuntimeException("Move not initialized!");

        /**
         * This is how we determine whether the move is done, by checking that the distance from startpos to currentpos is less/more than distance between startpos and targetpos.
         * This methos is general and works with other types of movement-patterns such as a sine-move. (a sine-move may not end up at a exact position)
         *
         *
         */
        float currDist = (float) startPos.distanceTo(new WT3Coord(MotorGroup.getInstance().getTachoCounts()).toCartesian());
        if (x++ == 10) {
            Utils.println("DIST: "+currDist+ " START_END DIST: "+startToTargetDistance);
            x = 0;
        }
       if (currDist >= startToTargetDistance-1) return true;
       else return false;
    }

    @Override
    public boolean isBeforeMove(long t) {
        if (startPos == null) throw new RuntimeException("Move not initialized!");
        return t < moveStartTime;
    }

    @Override
    public void setEndtime(long endtime) {
        this.moveEndTime = endtime;
    }

    @Override
    public XYZCoord getMoveTargetPos() {
        return targetPos;
    }

}
