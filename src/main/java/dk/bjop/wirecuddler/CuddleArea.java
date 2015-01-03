package dk.bjop.wirecuddler;

import dk.bjop.wirecuddler.math.XYZCoord;

import java.util.ArrayList;

/**
 * Created by bpeterse on 03-01-2015.
 *
 * Some area within the bounding triangle that defines the area to be cuddled.
 *
 * The area is defined as:
 * First point in list is the starting point that connects to the next point in the list. The last point in the list implicitly connects to the first. The polygon that arises may not intersect itself.
 *
 * Notice that in this impl the y-coordinate is not used, as this is defined by a different algorithm elsewhere.
 *
 * The definition of the point should be loaded from config/profile on startup.
 */
public class CuddleArea {

    private ArrayList<XYZCoord> boundingPoints = new ArrayList<XYZCoord>();

    public CuddleArea() {

    }

    /**
     * Should return a coordinate on the boundary of the boundign polygon that will serve as the new target-position.
     */
    public XYZCoord getNextTargetPos(XYZCoord currentPosition) {
        // Verify that the current position is within the bounding polygon.

        // Find new target pos in the polygon.

        return null;
    }
}
