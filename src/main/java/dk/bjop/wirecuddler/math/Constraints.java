package dk.bjop.wirecuddler.math;

/**
 * Created by bpeterse on 03-01-2015.
 */
public class Constraints {

    public static final int minWireLengthTacho = 360;


    public static boolean isValidCoordinate(XYZCoord p) {
        return isValidCoordinate(p.toWiresTachoCoord());
    }

    public static boolean isValidCoordinate(WT3Coord p) {
        int[] tachos = p.getTachos();
        for (int i = 0;i<tachos.length;i++) {
            if (tachos[i] < minWireLengthTacho) {
                Utils.println("VIOLATION OF CONSTRAINT: wire-length in tacho is: '" + tachos[i] + "' min is: '" + minWireLengthTacho + "' Violating coord is:\n" + p.toString());
                return false;
            }
        }
        return true;
    }

    /*public static boolean isWithinXZBounds(XYZCoord p) {
        // If distance from this point to any of P1, P2 or P3 is more than the distance between P1-P2, P1-P3 etc. we're out of bounds
        double distToP1 = p.distanceTo(Triangle.getInstance().getP1());
        double distToP2 = p.distanceTo(Triangle.getInstance().getP2());
        double distToP3 = p.distanceTo(Triangle.getInstance().getP3());
       return false;
    }*/

}
