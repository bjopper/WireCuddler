package dk.bjop.wirecuddler.movement.moves;

import dk.bjop.wirecuddler.math.XYZCoord;

/**
 * Created by bpeterse on 13-04-2015.
 */
public class OperatedMove implements MotorPathMove {
    private final float startSpeed = 2; // cm/sec

    public enum MotorDirection {
        POSITIVE, NEGATIVE
    }

    public enum CoordDirection {
        X, Y, Z, NONE;
    }

    boolean forceMoveEnd = false;

    private MotorDirection mdir = MotorDirection.POSITIVE;
    private CoordDirection cd = CoordDirection.NONE;

    //private boolean terminateMove = false;
    private long movementStarttime;
    private XYZCoord pos;
    private XYZCoord newPos;


    public OperatedMove() {}

    public void setMoveOn(MotorDirection dir, CoordDirection cod) {
        if (dir == mdir && cd == cod) return;

        this.mdir = dir;
        this.cd = cod;
        movementStarttime = System.currentTimeMillis();
    }

    @Override
    public void initialize(XYZCoord startPos, long starttime) {
        this.pos = startPos;
        this.newPos = pos;
    }

    public void stopMovement() {
        this.cd = CoordDirection.NONE;
        this.pos = newPos;
    }

    private float determineSpeed(long elapsedMillis) {
        // TODO Implement gradual acceleration
        return startSpeed;
    }

    public XYZCoord getCurrentPosition() {
        return newPos;
    }

    @Override
    public int[] getExpectedTachoPosAtTimeT(long t) {
        if (forceMoveEnd)  throw new RuntimeException("Move has been terminated.");

        if (cd != CoordDirection.NONE) {

            long elapsedTimeMillis = t - movementStarttime;
            float calcSpeed = determineSpeed(elapsedTimeMillis);
            float distSinceStartAtTimeT = (elapsedTimeMillis / 1000f) * calcSpeed;

            switch (cd) {
                case X:
                    newPos = new XYZCoord(pos.x + (mdir == MotorDirection.POSITIVE ? distSinceStartAtTimeT : -distSinceStartAtTimeT), pos.y, pos.z);
                    break;
                case Y:
                    newPos = new XYZCoord(pos.x, pos.y + (mdir == MotorDirection.POSITIVE ? distSinceStartAtTimeT : -distSinceStartAtTimeT), pos.z);
                    break;
                case Z:
                    newPos = new XYZCoord(pos.x, pos.y, pos.z + (mdir == MotorDirection.POSITIVE ? distSinceStartAtTimeT : -distSinceStartAtTimeT));
                    break;
            }
        }

        return newPos.toWiresTachoCoord().getTachos();
    }


    @Override
    public boolean isMoveDone(long t) {
        return forceMoveEnd;
    }

    @Override
    public void setMoveTerminate() {
        stopMovement();
        this.forceMoveEnd = true;
    }

}
