package dk.bjop.wirecuddler.util;

import dk.bjop.wirecuddler.model.Triangle;

/**
 * Created by bpeterse on 05-10-2014.
 */
public class WiresTachoCoord {

   public static boolean debug = false;

   public int[] tachos;

    public WiresTachoCoord(int p1Tacho, int p2Tacho, int p3Tacho) {
        tachos = new int[]{p1Tacho, p2Tacho, p3Tacho};
    }

    public WiresTachoCoord(int[] tachos) {
        this.tachos=tachos;
    }

    public String toString() {
        String s="";
        for (int i=0;i<tachos.length;i++) {
            s=s+"P" + (i+1) + " tacho: " + tachos[i] + "(" + Utils.tachoToCm(tachos[i]) + " cm)\n";
        }
        return s;
    }

    public static WiresTachoCoord createWireCoordFromWireLengthsCM(double[] wirelengthsCM){
        return new WiresTachoCoord(Utils.cmToTacho(wirelengthsCM[0]), Utils.cmToTacho(wirelengthsCM[1]), Utils.cmToTacho(wirelengthsCM[2]));
    }

    public XYZCoord toCartesian() {
        Triangle tri = Triangle.getInstance();

        double x = getXCoordinate(tri);
        double z = getZCoordinate(tri, x);
        double y = getYCoordinate(x, z);

        XYZCoord pos = new XYZCoord(x, y, z);
        debugPrint(pos.toString());
        return pos;
    }

    private double getYCoordinate(double x, double z) {
        debugPrint("------------------------------ Height at intersecion point -----------------------------------------");

        //Distance from origo to intersection point (pythagoras)
        double distToIntersectPoint = Math.sqrt( x*x + z*z );
        debugPrint("Dist to intersect point: " +distToIntersectPoint);

        double heightAtPoint = Math.sqrt(Utils.tachoToCm(tachos[0]) * Utils.tachoToCm(tachos[0]) - distToIntersectPoint * distToIntersectPoint);

        return heightAtPoint;
    }

    private double getZCoordinate(Triangle tri, double x) {
        XYZCoord[] trianglePoints = tri.getTrianglePoints();

        debugPrint("------------ Location inference test of point on p2p3 -------------------");

        int m2t = tachos[1];

        double m2m3Dist = Utils.tachoToCm(tri.getCalibValues().p2p3tachoDist);  // b
        double m2WireLength = Utils.tachoToCm(m2t);  // c
        double m3WireLength = Utils.tachoToCm(tachos[2]);

// Cosine relation... (Ref: http://da.wikipedia.org/wiki/Cosinusrelation) + http://www.studieportalen.dk/kompendier/matematik/formelsamling/trigonometri/begreber/hoejde-grundlinje
        double angleAtM3 = Math.toDegrees( Math.acos( (m2m3Dist*m2m3Dist + m3WireLength*m3WireLength - m2WireLength*m2WireLength) / (2*m2m3Dist*m3WireLength) ));  // A
        debugPrint("Angle at M3: " + angleAtM3);

// We now consider the rightangled triangle... (sine-relation here)
        double p2p3pos = m3WireLength * Math.sin(Math.toRadians(90 - angleAtM3));

        debugPrint("p2p3pos: " + p2p3pos);
        // Coordinates of the point between p2 and p3

        // z-coord and x-coord
        double p3AngleDeg = tri.getAngleAtP3();
        double zd = p2p3pos * Math.sin(Math.toRadians(p3AngleDeg));

        debugPrint("z: " + zd);

        double xd = trianglePoints[2].x - ( p2p3pos * Math.sin(Math.toRadians(90-p3AngleDeg)));

        debugPrint("Plane coords of point on the line between p2 and p3: (x, z) = (" + xd + ", " + zd + ")");

        debugPrint("------------------------------ Intersection of lines -----------------------------------------");


        double xDiff = xd - x;
        debugPrint("xDiff: " + xDiff);
        double z = zd + (xDiff * tri.getP2P3OrthogonalLineSlope());   // TODO check this!!!!
        debugPrint("finalZ: " + z);

        return z;
    }

    private double getXCoordinate(Triangle tri) {
        debugPrint("------------ Location inference test X -------------------");
        for (int i=0;i<tachos.length;i++) debugPrint("#Tacho ["+(i+1)+"] = "+tachos[i] + " (cm: " + Utils.tachoToCm(tachos[i]) + ")");

        int m1t = tachos[0];
        int m3t = tachos[2];

        double m1m3Dist = Utils.tachoToCm(tri.getCalibValues().p1p3tachoDist);  // b
        double m1wireLength = Utils.tachoToCm(m1t);  // c
        double m3WireLength = Utils.tachoToCm(m3t);  // a

        // Cosine relation... (Ref: http://da.wikipedia.org/wiki/Cosinusrelation) + http://www.studieportalen.dk/kompendier/matematik/formelsamling/trigonometri/begreber/hoejde-grundlinje
        double angleAtM1 = Math.toDegrees(Math.acos((m1m3Dist * m1m3Dist + m1wireLength * m1wireLength - m3WireLength * m3WireLength) / (2 * m1m3Dist * m1wireLength)));  // A


        debugPrint("X-infer: Angle is: " + angleAtM1);

        // We now consider the rightangled triangle... (sine-relation here)
        double triangleHeight = m1wireLength * Math.sin(Math.toRadians(angleAtM1)); // d or hb (height from b)
        double p1p3pos = m1wireLength * Math.sin(Math.toRadians(90 - angleAtM1));

        debugPrint("2D: X-pos of point is: "+ p1p3pos + "cm");
        debugPrint("2D: Height at X-pos: " + triangleHeight + "cm");
        return p1p3pos;
    }
    
    public void debugPrint(String s) {
        if (debug) Utils.println(s);
    }
}
