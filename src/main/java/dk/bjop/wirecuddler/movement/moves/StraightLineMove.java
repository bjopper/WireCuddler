package dk.bjop.wirecuddler.movement.moves;

import dk.bjop.wirecuddler.math.coordinates.SphericCoord;
import dk.bjop.wirecuddler.math.coordinates.WT3Coord;
import dk.bjop.wirecuddler.math.coordinates.XYZCoord;

/**
 * Created by bpeterse on 10-09-2014.
 *
 * This move will advance towards the targetpoint, but will not settle on the point.
 */
public class StraightLineMove implements MotorPathMove {
    boolean forceMoveEnd = false;

    // TODO high speeds (4+) causes the system to tighten the restpoint wire way too much when moving bcak to the restpoint. THis must be fixed

    float speedCmSec = 4;

    XYZCoord startPos = null;
    XYZCoord targetPos = null;
    float startToTargetDistance;
    long moveStartTime;
    long calculatedMoveTimeMillis;

    public StraightLineMove(XYZCoord target){
        this.targetPos = target;
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
        calculatedMoveTimeMillis = (long) ((startToTargetDistance / speedCmSec) * 1000f);
        forceMoveEnd = false;
    }

    @Override
    public void setSpeed(float speed) {
        this.speedCmSec = speed;
    }

    @Override
    public int[] getExpectedTachoPosAtTimeT(long t) {
        if (startPos == null) throw new RuntimeException("Move not initialized!");
        if (startPos == null || targetPos == null) throw new RuntimeException("startPos or targetPos is NULL!");
        long elapsedTimeMillis = t - moveStartTime;

        float distSinceStartAtTimeT = (elapsedTimeMillis / 1000f) * speedCmSec;
        /*if (distSinceStartAtTimeT > startToTargetDistance) {
            distSinceStartAtTimeT = startToTargetDistance;
        }*/
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
    public boolean isMoveDone(long t) {
        if (startPos == null) throw new RuntimeException("Move not initialized!");

        if (forceMoveEnd) return true;

        return t > moveStartTime + calculatedMoveTimeMillis;
        /**
         * This is how we determine whether the move is done, by checking that the distance from startpos to currentpos is less/more than distance between startpos and targetpos.
         * This methos is general and works with other types of movement-patterns such as a sine-move. (a sine-move may not end up at a exact position)
         *
         *
         */
        /*XYZCoord currPos = new WT3Coord(MotorGroup.getInstance().getTachoCounts()).toCartesian();
        Utils.println("Currpos: " + currPos + "   Tachos:" + MotorGroup.getInstance().tachosToString());
        Utils.println("Startpos: " + startPos);
        float currDist = (float) startPos.distanceTo(currPos);
        if (x++ == 10) {
            Utils.println("DIST: "+currDist+ " START_END DIST: "+startToTargetDistance);
            x = 0;
        }
       if (currDist >= startToTargetDistance-1) return true;
       else return false;*/
    }

    @Override
    public void setMoveTerminate() {
        this.forceMoveEnd = true;
    }

    @Override
    public XYZCoord getTarget() {
        return targetPos;
    }


}
