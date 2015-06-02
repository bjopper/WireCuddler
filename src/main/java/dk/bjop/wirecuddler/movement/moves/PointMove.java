package dk.bjop.wirecuddler.movement.moves;

import dk.bjop.wirecuddler.math.coordinates.SphericCoord;
import dk.bjop.wirecuddler.math.coordinates.WT3Coord;
import dk.bjop.wirecuddler.math.coordinates.XYZCoord;
import dk.bjop.wirecuddler.motor.MotorGroup;

/**
 * Created by bpeterse on 10-09-2014.
 *
 * This move will settle acuurately on the target point.
 */
public class PointMove implements MotorPathMove {
    boolean forceMoveEnd = false;

    float speedCmSec = 6;

    XYZCoord startPos = null;
    XYZCoord targetPos = null;
    float startToTargetDistance;
    long moveStartTime;
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
        if (startPos == null || targetPos == null) throw new RuntimeException("startPos or targetPos is NULL!");

        if (t >= moveStartTime + calculatedMoveTimeMillis) return targetTachos;

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

    int x = 0;
    @Override
    public boolean isMoveDone(long t) {
       if (startPos == null) throw new RuntimeException("Move not initialized!");

       if (forceMoveEnd) return true;

       if (t >= moveStartTime + calculatedMoveTimeMillis) {
           int[] currTachos = MotorGroup.getInstance().getTachoCounts();
           boolean terminate = true;
           int span = 1; // TODO This might need some adjustment
           for (int i=0;i<currTachos.length;i++) {
               terminate = terminate && (currTachos[i] <= targetTachos[i]+span && currTachos[i] >= targetTachos[i]-span);
           }
           return terminate;
       }
        else {
           return false;
       }
    }

    @Override
    public void setMoveTerminate() {
        this.forceMoveEnd = true;
    }

    @Override
    public XYZCoord getTarget() {
        return targetPos;
    }

    @Override
    public void setSpeed(float speed) {
        this.speedCmSec = speed;
    }

}
