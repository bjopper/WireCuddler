package dk.bjop.wirecuddler.movement.moves;

/**
 * Created by bpeterse on 10-09-2014.
 *
 * Std sine-curve
 * A speed of 12 is good.
 */
public class MotorPathMoveSinusImpl  {

    int speed = 12; //Degrees pr second
    double scale = 700;
    int initialRaiseTime = 5; // sec
    long moveTimeSec = 40;

    public MotorPathMoveSinusImpl() {}

    public MotorPathMoveSinusImpl(int speedDegSec) {
        this.speed = speedDegSec;
    }


    public int getExpectedTachoPosAtTimeT(long elapsedTimeMillis, int controllerID) {
        if (elapsedTimeMillis < initialRaiseTime*1000) {
            double increment = scale / (double)initialRaiseTime;
            return (int)Math.round(increment * (elapsedTimeMillis/1000f));
        }

        long adjustedTime = elapsedTimeMillis - initialRaiseTime*1000;
        return (int) (Math.sin(Math.toRadians((adjustedTime * speed)/1000f)) * scale) + (int)scale;
    }


    public boolean isMovementFinished(long elapsedTimeMillis) {
        return elapsedTimeMillis >= moveTimeSec*1000;
    }
}
