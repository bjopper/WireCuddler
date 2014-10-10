package dk.bjop.wirecuddler.motor;

/**
 * Created by bpeterse on 10-09-2014.
 */
public interface MotorPath {
    public int getExpectedTachoPosAtTimeT(long elapsedTimeMillis, int controllerID);
    public boolean isMovementFinished(long elapsedTimeMillis);
}
