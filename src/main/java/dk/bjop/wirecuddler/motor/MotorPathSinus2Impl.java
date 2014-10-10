package dk.bjop.wirecuddler.motor;

/**
 * Created by bpeterse on 10-09-2014.
 * This sine-curve slowly increases the oscaillation speed as times go
 *
 */
public class MotorPathSinus2Impl implements MotorPath {

    int speed = 12; //Degrees pr second
    double scale = 700;
    int initialRaiseTime = 5; // sec
    long moveTimeSec = 40;

    public MotorPathSinus2Impl() {}

    public MotorPathSinus2Impl(int speedDegSec) {
        this.speed = speedDegSec;
    }

    @Override
    public int getExpectedTachoPosAtTimeT(long elapsedTimeMillis, int controllerID) {
        if (elapsedTimeMillis < initialRaiseTime*1000) {
            double increment = scale / (double)initialRaiseTime;
            return (int)Math.round(increment * (elapsedTimeMillis/1000f));
        }

        long adjustedTime = elapsedTimeMillis - initialRaiseTime*1000;
        double newSpeed= Math.round((double)speed + (adjustedTime/4000f));
        return (int) (Math.sin(Math.toRadians(Math.round((adjustedTime * newSpeed)/1000f))) * scale) + (int)scale;
    }

    @Override
    public boolean isMovementFinished(long elapsedTimeMillis) {
        return elapsedTimeMillis >= moveTimeSec*1000;
    }
}
