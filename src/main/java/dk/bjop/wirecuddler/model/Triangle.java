package dk.bjop.wirecuddler.model;

import dk.bjop.wirecuddler.WireCuddler;
import dk.bjop.wirecuddler.util.CalibValues;
import dk.bjop.wirecuddler.util.Utils;
import dk.bjop.wirecuddler.util.XYZCoord;

/**
 * Created by bpeterse on 21-12-2014.
 */
public class Triangle {
    public static boolean debug = true;

    private static Triangle instance = null;

    XYZCoord[] trianglePoints;
    CalibValues cv;

    double p1p2distXcm;
    double p1p3distXcm;
    double p2p3distXcm;

    // Find angles of the projected triangular area using the projected values. Cosine-relation...
    double p1AngleDeg;
    double p2AngleDeg;
    double p3AngleDeg;

    // Slope a line orthogonal to the line spanned by p2-p3
    double p2p3OrthoSlope;

    private Triangle(CalibValues cv) {
        this.cv = cv;
        p1p2distXcm = Math.sqrt(Math.pow(Utils.tachoToCm(cv.p1p2tachoDist), 2) - Math.pow(cv.p1p2heightDiffCm,2));
        p1p3distXcm = Math.sqrt(Math.pow(Utils.tachoToCm(cv.p1p3tachoDist), 2) - Math.pow(cv.p1p3heightDiffCm,2));
        p2p3distXcm = Math.sqrt(Math.pow(Utils.tachoToCm(cv.p2p3tachoDist), 2) - Math.pow(Math.max(cv.p1p2heightDiffCm, cv.p1p3heightDiffCm)-Math.min(cv.p1p2heightDiffCm, cv.p1p3heightDiffCm),2));

        // Find angles of the projected triangular area using the projected values. Cosine-relation...
        p1AngleDeg = Math.toDegrees( Math.acos( (Math.pow(p1p3distXcm, 2) + Math.pow(p1p2distXcm, 2) - Math.pow(p2p3distXcm, 2)) / (2 * p1p3distXcm * p1p2distXcm)));
        p2AngleDeg = Math.toDegrees( Math.acos( (Math.pow(p2p3distXcm, 2) + Math.pow(p1p2distXcm, 2) - Math.pow(p1p3distXcm, 2)) / (2 * p2p3distXcm * p1p2distXcm)));
        p3AngleDeg = 180d - (p1AngleDeg + p2AngleDeg);

        // Build std cartesian coordinates of the points
        trianglePoints =  new XYZCoord[] { new XYZCoord(0, 0, 0),
                new XYZCoord(Math.cos(Math.toRadians(p1AngleDeg))*p1p2distXcm, cv.p1p2heightDiffCm, Math.sin(Math.toRadians(p1AngleDeg))*p1p2distXcm),
                new XYZCoord(p1p3distXcm, cv.p1p3heightDiffCm, 0)};

        debugPrint("-------------- Triangle points -----------------");
        debugPrint(getTrianglePointsString());

        debugPrint("-------------- Projected triangle points -----------------");
        debugPrint("Projected p1p2tachoDist CM: " + Utils.cmToTacho(p1p2distXcm) + " (cm: " + p1p2distXcm + ")");
        debugPrint("Projected p1p3tachoDist CM: " + Utils.cmToTacho(p1p3distXcm) + " (cm: " + p1p3distXcm + ")");
        debugPrint("Projected p2p3tachoDist CM: " + Utils.cmToTacho(p2p3distXcm) + " (cm: " + p2p3distXcm + ")");

        debugPrint("-------------- Triangle angles -----------------");
        debugPrint("p1AngleDeg: " + p1AngleDeg);
        debugPrint("p2AngleDeg: " + p2AngleDeg);
        debugPrint("p3AngleDeg: " + p3AngleDeg);

        debugPrint("-------------- Slope of line orthogonal to the line spanned by P2-P3 -----------------");
        double p2p3Slope =  (trianglePoints[2].z - trianglePoints[1].z) / (trianglePoints[2].x - trianglePoints[1].x);
        p2p3OrthoSlope = 1/p2p3Slope;
        debugPrint("p2p3Slope: " + p2p3Slope);
        debugPrint("p2p3 orthos-slope: " + p2p3OrthoSlope);
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

    public double getP2P3OrthogonalLineSlope() {
        return p2p3OrthoSlope;
    }

    public CalibValues getCalibValues() {
        return cv;
    }

    public String getTrianglePointsString() {
        String s = "";
        for (int i=0;i<trianglePoints.length;i++) {
            s=s+"P" + (i+1) + ": " + trianglePoints[i].toString();
        }
        return s;
    }

    public void debugPrint(String s) {
        if (debug) Utils.println(s);
    }
}
