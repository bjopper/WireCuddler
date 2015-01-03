package dk.bjop.wirecuddler.math;

import dk.bjop.wirecuddler.WireCuddler;
import dk.bjop.wirecuddler.config.CalibValues;

/**
 * Created by bpeterse on 21-12-2014.
 *
 * The system must be set up so that:
 *
 * - P1 is the lowest point. P2 is the next point when going clockwise round. P3 is the point after P2 when continuing clockwise.
 *
 * Internally we set it up so that
 * - P1 is always at origo and P3 is located on the positive x-axis
 *
 * This relates to the motors like:
 * - Motor M1 drives the wire at P1 and so forth.
 *
 */
public class Triangle {
    public static boolean debug = true;

    private static Triangle instance = null;

    private final double piHalf = Math.PI / 2d;

    XYZCoord[] trianglePoints;
    CalibValues cv;

    double p1p2distXcm;
    double p1p3distXcm;
    double p2p3distXcm;

    // The triangle projected down unto the XZ-plane.
    double projectedP1P2distXcm;
    double projectedP1P3distXcm;
    double projectedP2P3distXcm;

    // Find angles of the projected triangular area using the projected values. Cosine-relation...
    double p1AngleDeg;
    double p2AngleDeg;
    double p3AngleDeg;

    // Angle between the lines spanned by p1p2 and p1p3 and the XZ-plane. (vertical when points are not at same height)
    double p1p2AngleDeg;
    double p1p3AngleDeg;

   // double projectedLengthMultiplierX;
   // double projectedLengthMultiplierZ;

    // Slope of line orthogonal to the line spanned by P2-P3 in the plane spanned by P1, P2 and P3
    double p1p2OrthoSlope;

    private Triangle(CalibValues cv) {
        this.cv = cv;

        // Temporarily constrain triangle. Require:
        // P1 height = P3 height
        // P2 height >= P1 height
       // if (cv.getP1P3heightDiffCm() != 0) throw new RuntimeException("Violation of (temporary) constraint: P1 and P3 are currently required to be at same height.");
        if (cv.getP1P2heightDiffCm() < 0) throw new RuntimeException("Violation of constraint: P2 is required to have a height >= P1");
        if (cv.getP1P3heightDiffCm() < 0) throw new RuntimeException("Violation of constraint: P3 is required to have a height >= P1");
// TODO implemnt constraints


        calcTriangleValues();
        calcHeightDiffAngles();
        calcTrianglePoints();
        calcSlopes();
        calcProjectedDistances();

        printValues();
    }

    private void printValues() {
        debugPrint("\n-------------- Triangle points -----------------");
        debugPrint(getTrianglePointsString());

        debugPrint("\n-------------- Point distances (given by config) -----------------");
        debugPrint("p1p2tachoDist CM: " + Utils.cmToTacho(p1p2distXcm) + " (cm: " + p1p2distXcm + ")");
        debugPrint("p1p3tachoDist CM: " + Utils.cmToTacho(p1p3distXcm) + " (cm: " + p1p3distXcm + ")");
        debugPrint("p2p3tachoDist CM: " + Utils.cmToTacho(p2p3distXcm) + " (cm: " + p2p3distXcm + ")");

        debugPrint("\n-------------- Triangle angles -----------------");
        debugPrint("p1AngleDeg: " + Math.toDegrees(p1AngleDeg));
        debugPrint("p2AngleDeg: " + Math.toDegrees(p2AngleDeg));
        debugPrint("p3AngleDeg: " + Math.toDegrees(p3AngleDeg));

        debugPrint("\n-------------- Projected triangle points distances -----------------");
        debugPrint("projectedP1P2distXcm CM: " + Utils.cmToTacho(projectedP1P2distXcm) + " (cm: " + projectedP1P2distXcm + ")");
        debugPrint("projectedP1P3distXcm CM: " + Utils.cmToTacho(projectedP1P3distXcm) + " (cm: " + projectedP1P3distXcm + ")");
        debugPrint("projectedP2P3distXcm CM: " + Utils.cmToTacho(projectedP2P3distXcm) + " (cm: " + projectedP2P3distXcm + ")");

        debugPrint("\n-------------- Height-diff angles -----------------");
        debugPrint("Angle between line P1 to P3 and x-axis: " + Math.toDegrees(p1p3AngleDeg));
        debugPrint("Angle between line P1 to P2 and z-axis: " + Math.toDegrees(p1p2AngleDeg));

        /*debugPrint("\n-------------- Length/projected length multipliers -----------------");
        debugPrint("projectedLengthMultiplierX: " + projectedLengthMultiplierX);
        debugPrint("projectedLengthMultiplierZ: " + projectedLengthMultiplierZ);*/

        debugPrint("\n-------------- Slope of line orthogonal to the line spanned by P1-P2 -----------------");
        debugPrint("p1p2 orthos-slope: " + p1p2OrthoSlope);
    }

    private void calcHeightDiffAngles() {
        p1p3AngleDeg = Math.asin(cv.getP1P3heightDiffCm() / p1p3distXcm);
        p1p2AngleDeg = Math.asin(cv.getP1P2heightDiffCm() / p1p2distXcm);
    }

    private double getP2P3HeightDiffCm() {
        return Math.max(cv.getP1P2heightDiffCm(), cv.getP1P3heightDiffCm()) - Math.min(cv.getP1P2heightDiffCm(), cv.getP1P3heightDiffCm());
    }

    private void calcProjectedDistances() {
        projectedP1P3distXcm = Math.sqrt(p1p3distXcm*p1p3distXcm - cv.getP1P3heightDiffCm()*cv.getP1P3heightDiffCm());
        projectedP1P2distXcm = Math.sqrt(p1p2distXcm*p1p2distXcm - cv.getP1P2heightDiffCm()*cv.getP1P2heightDiffCm());
        double p2p3heightDiffCm = getP2P3HeightDiffCm();
        projectedP2P3distXcm = Math.sqrt(p2p3distXcm*p2p3distXcm - p2p3heightDiffCm*p2p3heightDiffCm);

        //projectedLengthMultiplierX = projectedP1P3distXcm / p1p3distXcm;
        //projectedLengthMultiplierZ = projectedP1P2distXcm / p1p2distXcm;
    }

    private void calcTriangleValues() {
        p1p2distXcm = Utils.tachoToCm(cv.p1p2tachoDist);
        p1p3distXcm = Utils.tachoToCm(cv.p1p3tachoDist);
        p2p3distXcm = Utils.tachoToCm(cv.p2p3tachoDist);

        // Find angles. Cosine-relation...
        p1AngleDeg = Math.acos((Math.pow(p1p3distXcm, 2) + Math.pow(p1p2distXcm, 2) - Math.pow(p2p3distXcm, 2)) / (2 * p1p3distXcm * p1p2distXcm));
        p2AngleDeg = Math.acos((Math.pow(p2p3distXcm, 2) + Math.pow(p1p2distXcm, 2) - Math.pow(p1p3distXcm, 2)) / (2 * p2p3distXcm * p1p2distXcm));
        p3AngleDeg = Math.PI - (p1AngleDeg + p2AngleDeg);
    }

    private void calcTrianglePoints() {
        // Build std cartesian coordinates of the points

        XYZCoord p1 = new XYZCoord(0, 0, 0);
        XYZCoord p2 = new XYZCoord(Math.cos(p1AngleDeg)*p1p2distXcm, cv.p1p2heightDiffCm, Math.sin(p1AngleDeg)*p1p2distXcm);
        XYZCoord p3 = new XYZCoord(p1p3distXcm * Math.sin(piHalf - p1p3AngleDeg), cv.p1p3heightDiffCm, 0);

        trianglePoints =  new XYZCoord[] {p1, p2, p3};
    }

    private void calcSlopes() {
        double p1p2Slope =  (trianglePoints[1].z - trianglePoints[0].z) / (trianglePoints[1].x - trianglePoints[0].x);
        p1p2OrthoSlope = 1/p1p2Slope;
    }

    public static Triangle getInstance() {
        if (instance == null) {
            instance = new Triangle(CalibValues.loadCalib(WireCuddler.default_calibFile));
        }
        return instance;
    }


    public XYZCoord[] getTrianglePoints() {
        return trianglePoints;
    }

    public XYZCoord getP1() {
        return trianglePoints[0];
    }

    public XYZCoord getP2() {
        return trianglePoints[1];
    }

    public XYZCoord getP3() {
        return trianglePoints[2];
    }

    public double getP1P3tachoDist() {
        return cv.p1p3tachoDist;
    }

    public double getP1P2tachoDist() {
        return cv.p1p2tachoDist;
    }

    public double getP2P3tachoDist() {
        return cv.p2p3tachoDist;
    }

    public double getP2P3Dist() {
        return p2p3distXcm;
    }

    public double getP1P3Dist() {
        return p1p3distXcm;
    }

    public double getP1P2Dist() {
        return p1p2distXcm;
    }

    public double getAngleAtP1() {
        return p1AngleDeg;
    }

    public double getAngleAtP2() {
        return p2AngleDeg;
    }

    public double getAngleAtP3() {
        return p3AngleDeg;
    }

    public double getP1P2OrthogonalLineSlope() {
        return p1p2OrthoSlope;
    }

    /*public double getProjectedX(double ux) {
        return ux * projectedLengthMultiplierX;
    }*/

    /*public double getProjectedZ(double uz) {
        return uz * projectedLengthMultiplierZ;
    }*/

    public CalibValues getCalibValues() {
        return cv;
    }

    public String getTrianglePointsString() {
        String s = "----- Triangle points -----\n";
        for (int i=0;i<trianglePoints.length;i++) {
            s=s+"P" + (i+1) + ": " + trianglePoints[i].toString()+"\n";
        }
        return s;
    }

    public void debugPrint(String s) {
        if (debug) Utils.println(s);
    }
}
