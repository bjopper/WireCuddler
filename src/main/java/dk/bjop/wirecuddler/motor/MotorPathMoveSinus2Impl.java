package dk.bjop.wirecuddler.motor;

/**
 * Created by bpeterse on 10-09-2014.
 * This sine-curve slowly increases the oscaillation speed as times go
 *
 */
public class MotorPathMoveSinus2Impl  {

    int speed = 12; //Degrees pr second
    double scale = 700;
    int initialRaiseTime = 5; // sec
    long moveTimeSec = 40;

    public MotorPathMoveSinus2Impl() {}

    public MotorPathMoveSinus2Impl(int speedDegSec) {
        this.speed = speedDegSec;
    }


    public int getExpectedTachoPosAtTimeT(long elapsedTimeMillis, int controllerID) {
        if (elapsedTimeMillis < initialRaiseTime*1000) {
            double increment = scale / (double)initialRaiseTime;
            return (int)Math.round(increment * (elapsedTimeMillis/1000f));
        }

        long adjustedTime = elapsedTimeMillis - initialRaiseTime*1000;
        double newSpeed= Math.round((double)speed + (adjustedTime/4000f));
        return (int) (Math.sin(Math.toRadians(Math.round((adjustedTime * newSpeed)/1000f))) * scale) + (int)scale;
    }


    public boolean isMovementFinished(long elapsedTimeMillis) {
        return elapsedTimeMillis >= moveTimeSec*1000;
    }
}
