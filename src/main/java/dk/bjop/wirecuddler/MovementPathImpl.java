package dk.bjop.wirecuddler;

import dk.bjop.wirecuddler.util.Utils;

/**
 * Created by bpeterse on 10-09-2014.
 */
public class MovementPathImpl implements MovementPath {

    long moveTimeSec = 20;

    @Override
    public int getExpectedTachoPosAtTimeT(long elapsedTimeMillis, float speedCmSec) {
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
