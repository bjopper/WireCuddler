package dk.bjop.wirecuddler.movement.moves;

import dk.bjop.wirecuddler.math.Utils;

/**
 * Created by bpeterse on 10-09-2014.
 */
public class MotorPathMoveImpl  {

    long moveTimeSec = 20;
    float speedCmSec = 5;


    public int getExpectedTachoPosAtTimeT(long elapsedTimeMillis, int controllerID) {
        // a = vandret sidelængde top
        // b = lodret sidelængde nedad (konstant)
        // c = hypotenusen

        float a = (elapsedTimeMillis / 1000f) * speedCmSec;
        float b = 50;
        float c = (float) Math.sqrt(Math.pow(a,2) + Math.pow(b, 2));
        return Utils.cmToTacho(c - b); // we subtract b as we are only interested in the diff
    }


    public boolean isMovementFinished(long elapsedTimeMillis) {
        return elapsedTimeMillis >= moveTimeSec*1000;
    }

}
