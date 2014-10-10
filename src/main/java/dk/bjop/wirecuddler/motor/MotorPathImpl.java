package dk.bjop.wirecuddler.motor;

import dk.bjop.wirecuddler.util.Utils;

/**
 * Created by bpeterse on 10-09-2014.
 */
public class MotorPathImpl implements MotorPath {

    long moveTimeSec = 20;
    float speedCmSec = 5;

    @Override
    public int getExpectedTachoPosAtTimeT(long elapsedTimeMillis, int controllerID) {
        // a = vandret sidelængde top
        // b = lodret sidelængde nedad (konstant)
        // c = hypotenusen

        float a = (elapsedTimeMillis / 1000f) * speedCmSec;
        float b = 50;
        float c = (float) Math.sqrt(Math.pow(a,2) + Math.pow(b, 2));
        return Utils.cmToTacho(c - b); // we subtract b as we are only interested in the diff
    }

    @Override
    public boolean isMovementFinished(long elapsedTimeMillis) {
        return elapsedTimeMillis >= moveTimeSec*1000;
    }

}
