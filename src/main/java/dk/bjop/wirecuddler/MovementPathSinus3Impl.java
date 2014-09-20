package dk.bjop.wirecuddler;

/**
 * Created by bpeterse on 10-09-2014.
 *
 * Std sine-curve
 * A speed of 12 is good.
 */
public class MovementPathSinus3Impl implements MovementPath {

    double speed = 12; //Degrees pr second
    double scale = 700;
    int initialRaiseTime = 5; // sec
    long moveTimeSec = 60;

    public MovementPathSinus3Impl() {}

    public MovementPathSinus3Impl(int speedDegSec) {
        this.speed = speedDegSec;
    }

    @Override
    public int getExpectedTachoPosAtTimeT(long elapsedTimeMillis, float speedCmSec) {
        if (elapsedTimeMillis < initialRaiseTime*1000) {
            double increment = scale / (double)initialRaiseTime;
            return (int)Math.round(increment * (elapsedTimeMillis/1000f));
        }

        long adjustedTime = elapsedTimeMillis - initialRaiseTime*1000;
        double newScale = scale - (adjustedTime/1000f)*15;
        return (int) (Math.sin(Math.toRadians((adjustedTime * speed)/1000f)) * newScale) + (int)newScale;
    }

    @Override
    public boolean isMovementFinished(long elapsedTimeMillis) {
        return elapsedTimeMillis >= moveTimeSec*1000;
    }
}
