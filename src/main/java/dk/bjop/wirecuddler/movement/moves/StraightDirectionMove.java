package dk.bjop.wirecuddler.movement.moves;

import dk.bjop.wirecuddler.math.SphericCoord;
import dk.bjop.wirecuddler.math.WT3Coord;
import dk.bjop.wirecuddler.math.XYZCoord;

/**
 * Created by bpeterse on 03-01-2015.
 *
 * TODO This should really be a super-class of StraightToPointMove...
 *
 */
public class StraightDirectionMove implements MotorPathMove {
    long moveTimeSec = 120;
    float speedCmSec = 2;

    XYZCoord startPos = null;
    XYZCoord targetPos = null;
    float startToTargetDistance;

    public StraightDirectionMove(XYZCoord directionalPoint) {
        this.targetPos = directionalPoint;
    }

    @Override
    public int[] getExpectedTachoPosAtTimeT(long elapsedTimeMillis) {
        if (startPos == null || targetPos == null) throw new RuntimeException("startPos or targetPos is NULL!");

        float distSinceStartAtTimeT = (elapsedTimeMillis / 1000f) * speedCmSec;

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
    public boolean isMovementFinished(long elapsedTimeMillis) {
        return false;
    }

    @Override
    public void setStartPos(XYZCoord startPos) {
        this.startPos = startPos;
        startToTargetDistance = (float) startPos.distanceTo(targetPos);
    }

    @Override
    public XYZCoord getTargetPos() {
        return null;
    }
}
