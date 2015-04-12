package dk.bjop.wirecuddler.movement.moves;

import dk.bjop.wirecuddler.math.SphericCoord;
import dk.bjop.wirecuddler.math.Utils;
import dk.bjop.wirecuddler.math.WT3Coord;
import dk.bjop.wirecuddler.math.XYZCoord;
import dk.bjop.wirecuddler.motor.MotorGroup;

/**
 * Created by bpeterse on 10-09-2014.
 *
 * This move will settle acuurately on the target point.
 */
public class PointMove implements MotorPathMove {


    float speedCmSec = 4;

    XYZCoord startPos = null;
    XYZCoord targetPos = null;
    float startToTargetDistance;
    long moveStartTime;
    long moveEndTime;
    long calculatedMoveTimeMillis;

    int[] targetTachos;

    public PointMove(XYZCoord target){
        this.targetPos = target;

        //calculatedMoveTimeMillis = (long) ((startToTargetDistance / speedCmSec) * 1000f);
        targetTachos = target.toWiresTachoCoord().getTachos();
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

       // Utils.println("Start-target distance: "+startToTargetDistance);
    }

    @Override
    public int[] getExpectedTachoPosAtTimeT(long t) {
        if (startPos == null) throw new RuntimeException("Move not initialized!");

        if (t >= moveStartTime + calculatedMoveTimeMillis) return targetTachos;

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

       if (t >= moveStartTime + calculatedMoveTimeMillis) {
           int[] currTachos = MotorGroup.getInstance().getTachoCounts();
           boolean terminate = true;
           Utils.println("Diff: "+targetPos.toWiresTachoCoord().subtract(new WT3Coord(currTachos)).toString());
           int span = 1; // TODO This might need some adjustment
           for (int i=0;i<currTachos.length;i++) {
               terminate = terminate && (targetTachos[i]+span > currTachos[i] && currTachos[i] > targetTachos[0]-span);
           }
           return terminate;
       }
        else {
           return false;
       }
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