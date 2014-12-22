package dk.bjop.wirecuddler.model;

import dk.bjop.wirecuddler.util.CalibValues;
import dk.bjop.wirecuddler.util.Utils;
import dk.bjop.wirecuddler.util.XYZCoord;

/**
 * Created by bpeterse on 21-12-2014.
 */
public class TriangleGeometry {

    double p1p2distXcm;
    double p1p3distXcm;
    double p2p3distXcm;

    // Find angles of the projected triangular area using the projected values. Cosine-relation...
    double p1AngleDeg;
    double p2AngleDeg;
    double p3AngleDeg;

    XYZCoord[] trianglePoints;


    public void TriangleGeometry() {
        p1p2distXcm = Math.sqrt(Math.pow(Utils.tachoToCm(CalibValues.p1p2tachoDist), 2) - Math.pow(CalibValues.p1p2heightDiffCm,2));
        p1p3distXcm = Math.sqrt(Math.pow(Utils.tachoToCm(CalibValues.p1p3tachoDist), 2) - Math.pow(CalibValues.p1p3heightDiffCm,2));
        p2p3distXcm = Math.sqrt(Math.pow(Utils.tachoToCm(CalibValues.p2p3tachoDist), 2) - Math.pow(Math.max(CalibValues.p1p2heightDiffCm, CalibValues.p1p3heightDiffCm)-Math.min(CalibValues.p1p2heightDiffCm, CalibValues.p1p3heightDiffCm),2));

        p1AngleDeg = Math.toDegrees( Math.acos( (Math.pow(p1p3distXcm, 2) + Math.pow(p1p2distXcm, 2) - Math.pow(p2p3distXcm, 2)) / (2 * p1p3distXcm * p1p2distXcm)));
        p2AngleDeg = Math.toDegrees( Math.acos( (Math.pow(p2p3distXcm, 2) + Math.pow(p1p2distXcm, 2) - Math.pow(p1p3distXcm, 2)) / (2 * p2p3distXcm * p1p2distXcm)));
        p3AngleDeg = 180d - (p1AngleDeg + p2AngleDeg);

        trianglePoints =  new XYZCoord[] { new XYZCoord(0, 0, 0),
                new XYZCoord(Math.cos(Math.toRadians(p1AngleDeg))*p1p2distXcm, CalibValues.p1p2heightDiffCm, Math.sin(Math.toRadians(p1AngleDeg))*p1p2distXcm),
                new XYZCoord(p1p3distXcm, CalibValues.p1p3heightDiffCm, 0)};
    }

    public XYZCoord[] getTrianglePoints() {
        return trianglePoints;
    }

}
