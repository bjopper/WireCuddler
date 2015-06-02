package dk.bjop.wirecuddler.math;


import dk.bjop.wirecuddler.WireCuddler;
import dk.bjop.wirecuddler.math.coordinates.XYZCoord;
import lejos.nxt.comm.RConsole;

/**
 * Created by bpeterse on 10-09-2014.
 */
public class Utils {


    public static int cmToTacho(double lengthCm) {
        return (int) Math.round(lengthCm * Constants.tachosPrCm);
    }

    public static double tachoToCm(int tachoCount) {
        return tachoCount * Constants.cmPrTacho;
    }

    public static void println(String s) {
        if (WireCuddler.useRConsole) {
            if (WireCuddler.isDevMode() && RConsole.isOpen()) {
                RConsole.println(s);
            }
        }
        else {
            // Ignore all output
        }
    }

    public static XYZCoord findMidpoint(XYZCoord p1, XYZCoord p2) {
        return new XYZCoord((p1.x+p2.x)/2, (p1.y+p2.y)/2, (p1.z+p2.z)/2);
    }


    /*public static double distance(XYZCoord p1, XYZCoord p2) {
        double dx   = p1.x - p2.x;         //horizontal difference
        double dy   = p1.y - p2.y;         //vertical difference
        double dist = Math.sqrt( dx*dx + dy*dy ); //distance using Pythagoras theorem
        return dist;
    }*/

   /* public static double distanceTo(XZCoord p1, XZCoord p2) {
        double dx   = p1.x - p2.x;         //horizontal difference
        double dy   = p1.z - p2.z;         //vertical difference
        double dist = Math.sqrt( dx*dx + dy*dy ); //distance using Pythagoras theorem
        return dist;
    }*/

    /*public static double getCuddleHeight() {
        // This is actually a y-value - a function of x and z (must likely a tilted plane will suffice)
        return 100;
    }*/

    /*public static XYZCoord[] getCartesianPoints(double p1p2distCm, double p1p3distCm, double p2p3distCm, double p1p2heightDiffMm, double p1p3heightDiffMm) {
        // We create a triangular area from the three point we can infer from the params.
        // In a cartesian 2D coordinate system p1 and p3 are considered located on the x-axis, and p2 in the first quadrant between p1 and p3 wrt x. (ingen stumpe vinkler)
        // p1 is at height zero. p2 and p3 are at heights relative to p1 with a positive value og p2 if it is located higher than p1 and negative value if located lower.

        // Project triangular area onto xz-plane
        double p1p2distXcm = Math.sqrt(Math.pow(p1p2distCm, 2) - Math.pow(p1p2heightDiffMm,2));
        double p1p3distXcm = Math.sqrt(Math.pow(p1p3distCm, 2) - Math.pow(p1p3heightDiffMm,2));
        double p2p3distXcm = Math.sqrt(Math.pow(p2p3distCm, 2) - Math.pow(Math.max(p1p2heightDiffMm, p1p3heightDiffMm)-Math.min(p1p2heightDiffMm, p1p3heightDiffMm),2));

        // Find angles of the projected triangular area using the projected values. Cosine-relation...
        double p1AngleDeg = Math.toDegrees( Math.acos( Math.toRadians(Math.pow(p1p3distXcm, 2) + Math.pow(p1p2distXcm, 2) - Math.pow(p2p3distXcm, 2)) / (2 * p1p3distXcm * p1p2distXcm)));
        double p2AngleDeg = Math.toDegrees( Math.acos( Math.toRadians(Math.pow(p2p3distXcm, 2) + Math.pow(p1p2distXcm, 2) - Math.pow(p1p3distXcm, 2)) / (2 * p2p3distXcm * p1p2distXcm)));
        double p3AngleDeg = 180d - (p1AngleDeg + p2AngleDeg);

        // Build std cartesian coordinates of the points
        return new XYZCoord[] { new XYZCoord(0, 0, 0),
                new XYZCoord(Math.cos(Math.toRadians(p1AngleDeg))*p1p2distXcm, p1p2heightDiffMm, Math.sin(Math.toRadians(p1AngleDeg))*p1p2distXcm),
                new XYZCoord(p1p3distXcm, p1p3heightDiffMm, 0)};
    }*/

}
