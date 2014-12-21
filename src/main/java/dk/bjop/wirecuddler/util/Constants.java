package dk.bjop.wirecuddler.util;

/**
 * Created by bpeterse on 10-09-2014.
 */
public class Constants {

    public static final double wireBarrelCircumference = 5.1d; // in CM
    public static final int gearing = 1 ;//3; // 1:3

    // Basic measurements and settings.
    // TODO Should be obtained from calibration where possible....
    // Given these we can compute the geometry of the triangular area the three points span.
   /* public static double p1p2distCm = 196;
    public static double p1p3distCm = 144;
    public static double p2p3distCm = 196;*/

    public static final double tachosPrCm = (360d / wireBarrelCircumference);
    public static final double cmPrTacho = wireBarrelCircumference / 360d;

    // Tacho-distance (offset ~10cm from hookpoint)
    // M2-M1: 13114
    // M1-M3: 13428
    // M3-M2: 10537


    // We create a triangular area from the three point we can infer from the params.
    // In a cartesian 2D coordinate system p1 and p3 are considered located on the x-axis, and p2 in the first quadrant between p1 and p3 wrt x. (ingen stumpe vinkler)
    // p1 is at height zero. p2 and p3 are at heights relative to p1 with a positive value og p2 if it is located higher than p1 and negative value if located lower.

    // Project triangular area onto xz-plane
    public static final double p1p2distXcm = Math.sqrt(Math.pow(Utils.tachoToCm(CalibValues.p1p2tachoDist), 2) - Math.pow(CalibValues.p1p2heightDiffCm,2));
    public static final double p1p3distXcm = Math.sqrt(Math.pow(Utils.tachoToCm(CalibValues.p1p3tachoDist), 2) - Math.pow(CalibValues.p1p3heightDiffCm,2));
    public static final double p2p3distXcm = Math.sqrt(Math.pow(Utils.tachoToCm(CalibValues.p2p3tachoDist), 2) - Math.pow(Math.max(CalibValues.p1p2heightDiffCm, CalibValues.p1p3heightDiffCm)-Math.min(CalibValues.p1p2heightDiffCm, CalibValues.p1p3heightDiffCm),2));

    // Find angles of the projected triangular area using the projected values. Cosine-relation...
    public static final double p1AngleDeg = Math.toDegrees( Math.acos( Math.toRadians(Math.pow(p1p3distXcm, 2) + Math.pow(p1p2distXcm, 2) - Math.pow(p2p3distXcm, 2)) / (2 * p1p3distXcm * p1p2distXcm)));
    public static final double p2AngleDeg = Math.toDegrees( Math.acos( Math.toRadians(Math.pow(p2p3distXcm, 2) + Math.pow(p1p2distXcm, 2) - Math.pow(p1p3distXcm, 2)) / (2 * p2p3distXcm * p1p2distXcm)));
    public static final double p3AngleDeg = 180d - (p1AngleDeg + p2AngleDeg);

    // Build std cartesian coordinates of the points
    public static final XYZCoord[] trianglePoints =  new XYZCoord[] { new XYZCoord(0, 0, 0),
            new XYZCoord(Math.cos(Math.toRadians(p1AngleDeg))*p1p2distXcm, CalibValues.p1p2heightDiffCm, Math.sin(Math.toRadians(p1AngleDeg))*p1p2distXcm),
            new XYZCoord(p1p3distXcm, CalibValues.p1p3heightDiffCm, 0)};
}
