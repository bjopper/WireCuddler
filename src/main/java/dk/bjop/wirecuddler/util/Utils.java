package dk.bjop.wirecuddler.util;


import lejos.nxt.comm.RConsole;

/**
 * Created by bpeterse on 10-09-2014.
 */
public class Utils {


    public static int cmToTacho(float lengthCm) {
        double barrelRevs = lengthCm / Constants.wireBarrelCircumference;
        double motorRevs = barrelRevs * Constants.gearing;
        int tachoCount = (int) (motorRevs * 360f);
        return tachoCount;
    }

    public double tachoToCm(int tachoCount) {
        double motorRevs = tachoCount/360f;
        double barrelRevs = motorRevs * Constants.gearing;
        double length = barrelRevs * Constants.wireBarrelCircumference;
        return length;
    }

    public static void println(String s) {
        if (RConsole.isOpen()) {
            RConsole.println(s);
        }
    }

    public static double millisToSec(long millis) {
        return Math.round(millis/1000d);
    }

    public static double distance(XYZCoord p1, XYZCoord p2) {
        double dx   = p1.x - p2.x;         //horizontal difference
        double dy   = p1.y - p2.y;         //vertical difference
        double dist = Math.sqrt( dx*dx + dy*dy ); //distance using Pythagoras theorem
        return dist;
    }

    public static double distance(XZCoord p1, XZCoord p2) {
        double dx   = p1.x - p2.x;         //horizontal difference
        double dy   = p1.z - p2.z;         //vertical difference
        double dist = Math.sqrt( dx*dx + dy*dy ); //distance using Pythagoras theorem
        return dist;
    }

    public static WCCoord toWCCoord(XZCoord p) {
        XZCoord p1 = truncate(Constants.trianglePoints[0]);
        XZCoord p2 = truncate(Constants.trianglePoints[1]);
        XZCoord p3 = truncate(Constants.trianglePoints[2]);

        double h1 = getCuddleHeight();
        double h2 = getCuddleHeight() + Constants.trianglePoints[1].y;
        double h3 = getCuddleHeight() + Constants.trianglePoints[2].y;

        double l1plane = distance(p, p1);
        double l2plane = distance(p, p2);
        double l3plane = distance(p, p3);

        double l1 = Math.sqrt(h1*h1 + l1plane*l1plane);
        double l2 = Math.sqrt(h2*h2 + l2plane*l2plane);
        double l3 = Math.sqrt(h3*h3 + l3plane*l3plane);

        return new WCCoord(l1, l2, l3);
    }

    public static XZCoord truncate(XYZCoord p) {
        return new XZCoord(p.x, p.z);
    }

    public static double getCuddleHeight() {
        // This is actually a y-value - a function of x and z (must likely a tilted plane will suffice)
        return 100;
    }

    public static XYZCoord[] getCartesianPoints(double p1p2distCm, double p1p3distCm, double p2p3distCm, double p1p2heightDiffCm, double p1p3heightDiffCm) {
        // We create a triangular area from the three point we can infer from the params.
        // In a cartesian 2D coordinate system p1 and p3 are considered located on the x-axis, and p2 in the first quadrant between p1 and p3 wrt x. (ingen stumpe vinkler)
        // p1 is at height zero. p2 and p3 are at heights relative to p1 with a positive value og p2 if it is located higher than p1 and negative value if located lower.

        // Project triangular area onto xz-plane
        double p1p2distXcm = Math.sqrt(Math.pow(p1p2distCm, 2) - Math.pow(p1p2heightDiffCm,2));
        double p1p3distXcm = Math.sqrt(Math.pow(p1p3distCm, 2) - Math.pow(p1p3heightDiffCm,2));
        double p2p3distXcm = Math.sqrt(Math.pow(p2p3distCm, 2) - Math.pow(Math.max(p1p2heightDiffCm, p1p3heightDiffCm)-Math.min(p1p2heightDiffCm, p1p3heightDiffCm),2));

        // Find angles of the projected triangular area using the projected values. Cosine-relation...
        double p1AngleDeg = Math.toDegrees( Math.acos( Math.toRadians(Math.pow(p1p3distXcm, 2) + Math.pow(p1p2distXcm, 2) - Math.pow(p2p3distXcm, 2)) / (2 * p1p3distXcm * p1p2distXcm)));
        double p2AngleDeg = Math.toDegrees( Math.acos( Math.toRadians(Math.pow(p2p3distXcm, 2) + Math.pow(p1p2distXcm, 2) - Math.pow(p1p3distXcm, 2)) / (2 * p2p3distXcm * p1p2distXcm)));
        double p3AngleDeg = 180d - (p1AngleDeg + p2AngleDeg);

        // Build std cartesian coordinates of the points
        return new XYZCoord[] { new XYZCoord(0, 0, 0),
                new XYZCoord(Math.cos(Math.toRadians(p1AngleDeg))*p1p2distXcm, p1p2heightDiffCm, Math.sin(Math.toRadians(p1AngleDeg))*p1p2distXcm),
                new XYZCoord(p1p3distXcm, p1p3heightDiffCm, 0)};
    }
}
