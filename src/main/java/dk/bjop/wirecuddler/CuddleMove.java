package dk.bjop.wirecuddler;

import dk.bjop.wirecuddler.util.Utils;
import dk.bjop.wirecuddler.util.XYZCoord;

/**
 * Created by bpeterse on 20-09-2014.
 */
public class CuddleMove {
    double moveSpeedCmSec = 5;

    public CuddleMove() {

    }

    public XYZCoord getPositionAtTimeT(long tMillis) {

        // Check boundaries

        return null;//new XYZCoord(Utils.millisToSec(tMillis) * moveSpeedCmSec, 0);
    }
}
