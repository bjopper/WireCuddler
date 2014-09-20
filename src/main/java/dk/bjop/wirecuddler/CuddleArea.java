package dk.bjop.wirecuddler;

/**
 * Created by bpeterse on 20-09-2014.
 *
 *
 */
public class CuddleArea {

    final double p1p2distCm;
    final double p1p3distCm;
    final double p2p3distCm;

    final double p1HeightCm = 0;
    final double p2HeightCm;
    final double p3HeightCm;

    final double p1p2distXcm;
    final double p1p3distXcm;
    final double p2p3distXcm;

    final double p1AngleDeg;
    final double p2AngleDeg;
    final double p3AngleDeg;

    XYCoord p1;
    XYCoord p2;
    XYCoord p3;

    double cuddlePointBelowP1Cm = 100;


    public CuddleArea(double p1p2distCm, double p1p3distCm, double p2p3distCm, double p1p2heightDiffCm, double p1p3heightDiffCm) {
        this.p1p2distCm = p1p2distCm;
        this.p1p3distCm = p1p3distCm;
        this.p2p3distCm = p2p3distCm;

        this.p2HeightCm = p1p2heightDiffCm;
        this.p3HeightCm = p1p3heightDiffCm;

        // We create a triangular area from the three point we can infer from the params.
        // In a cartesian 2D coordinate system p1 and p3 are considered located on the x-axis, and p2 in the first quadrant between p1 and p3 wrt x. (ingen stumpe vinkler)
        // p1 is at height zero. p2 and p3 are at heights relative to p1 with a positive value og p2 if it is located higher than p1 and negative value if located lower.

        // Project triangular area onto xz-plane
        p1p2distXcm = Math.sqrt(Math.pow(p1p2distCm, 2) - Math.pow(p1p2heightDiffCm,2));
        p1p3distXcm = Math.sqrt(Math.pow(p1p3distCm, 2) - Math.pow(p1p3heightDiffCm,2));
        p2p3distXcm = Math.sqrt(Math.pow(p2p3distCm, 2) - Math.pow(Math.max(p1p2heightDiffCm, p1p3heightDiffCm)-Math.min(p1p2heightDiffCm, p1p3heightDiffCm),2));

        // Find angles of the projected triangular area using the projected values. Cosine-relation...
        p1AngleDeg = Math.toDegrees( Math.acos( Math.toRadians(Math.pow(p1p3distXcm, 2) + Math.pow(p1p2distXcm, 2) - Math.pow(p2p3distXcm, 2)) / (2 * p1p3distXcm * p1p2distXcm)));
        p2AngleDeg = Math.toDegrees( Math.acos( Math.toRadians(Math.pow(p2p3distXcm, 2) + Math.pow(p1p2distXcm, 2) - Math.pow(p1p3distXcm, 2)) / (2 * p2p3distXcm * p1p2distXcm)));
        p3AngleDeg = 180d - (p1AngleDeg + p2AngleDeg);

        // Build std cartesian coordinates of the points
        p1 = new XYCoord(0,0);
        p2 = new XYCoord(Math.cos(Math.toRadians(p1AngleDeg))*p1p2distXcm, Math.sin(Math.toRadians(p1AngleDeg))*p1p2distXcm);
        p3 = new XYCoord(p1p3distXcm,0);
    }

  /*  public static CuddleArea loadFromFile() {
        return new CuddleArea(null, null, null);
    }*/

}
