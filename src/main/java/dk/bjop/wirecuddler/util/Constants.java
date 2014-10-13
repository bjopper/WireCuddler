package dk.bjop.wirecuddler.util;

/**
 * Created by bpeterse on 10-09-2014.
 */
public class Constants {

    public static final double wireBarrelCircumference =5.1d; // in CM
    public static final int gearing =3; // 1:3

    // Basic measurements and settings.
    // TODO Should be obtained from calibration where possible....
    // Given these we can compute the geometry of the triangular area the three points span.
    public static double p1p2distCm = 196;
    public static double p1p3distCm = 144;
    public static double p2p3distCm = 196;
    public static final double p1p2heightDiffCm = 0;
    public static final double p1p3heightDiffCm = 0;
    public static final int restPoint = 1; // The point at which we rest the CuddlePoint when cuddler is off. ATTENTION! The restpoint must have a trigger associated with it!!!!
    public static final int[] baseLine = new int[]{2,3}; // First point is origo. Second point is located on positive part of x-axis
    public static final int[] overloadProtectedMotors = new int[]{1,2}; // These motors have sensors that protect from tension overloading



    public static final XYZCoord[] trianglePoints;


    static {
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
        trianglePoints =  new XYZCoord[] { new XYZCoord(0, 0, 0),
                new XYZCoord(Math.cos(Math.toRadians(p1AngleDeg))*p1p2distXcm, p1p2heightDiffCm, Math.sin(Math.toRadians(p1AngleDeg))*p1p2distXcm),
                new XYZCoord(p1p3distXcm, p1p3heightDiffCm, 0)};
    }

}
