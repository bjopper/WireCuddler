package dk.bjop.wirecuddler;

/**
 * Created by bpeterse on 10-09-2014.
 */
public interface MovementPath {
    public int getExpectedTachoPosAtTimeT(long elapsedTimeMillis, float speedCmSec);
    public boolean isMovementFinished(long elapsedTimeMillis);
}
