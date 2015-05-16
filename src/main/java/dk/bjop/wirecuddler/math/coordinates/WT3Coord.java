package dk.bjop.wirecuddler.math.coordinates;

import dk.bjop.wirecuddler.math.Utils;
import dk.bjop.wirecuddler.math.geometry.BaseGeometry;

/**
 * Created by bpeterse on 05-10-2014.
 *
 * WiresTachoCoord.
 *
 * Represents a position by the point where 3 straight lines of different lengths and originating from P1, P2 and P3 meet.
 * Lengths are given in tacho-counts.
 */
public class WT3Coord {
    public static boolean debug = false;

    private int[] tachos;
    private double[] wireLengths;
    private final double piHalf = Math.PI / 2d;



    public WT3Coord(int p1Tacho, int p2Tacho, int p3Tacho) {
        tachos = new int[]{p1Tacho, p2Tacho, p3Tacho};
    }

    public WT3Coord(int[] tachos) {
        this.tachos=tachos;
    }

    public String toString() {
        return "(P1, P2, P3) = (" + tachos[0] + "t, " + tachos[1] + "t, " + tachos[2] + "t) / (" + Utils.tachoToCm(tachos[0]) + "cm, " + Utils.tachoToCm(tachos[1]) + "cm, " + Utils.tachoToCm(tachos[2]) + "cm)";
    }

    public static WT3Coord createWireCoordFromWireLengthsCM(double[] wirelengthsCM){
        return new WT3Coord(Utils.cmToTacho(wirelengthsCM[0]), Utils.cmToTacho(wirelengthsCM[1]), Utils.cmToTacho(wirelengthsCM[2]));
    }

    public XYZCoord toCartesian() {

        long start=System.currentTimeMillis();

        BaseGeometry tri = BaseGeometry.getInstance();

        wireLengths = new double[]{Utils.tachoToCm(tachos[0]), Utils.tachoToCm(tachos[1]), Utils.tachoToCm(tachos[2])};

        double x = getXCoordinate(tri);
        double z = getZCoordinate(tri, x);
        //x = tri.getProjectedX(x);
        //z = tri.getProjectedZ(z);
        double y = getYCoordinate(x, z);

        boolean isValid = true;
        String errStr = "";
        if (Double.isNaN(x)) {
            errStr+="x is NAN\n";
            isValid=false;
        }
        if (Double.isNaN(y)) {
            errStr+="y is NAN\n";
            isValid=false;
        }
        if (Double.isNaN(z)) {
            errStr+="z is NAN\n";
            isValid=false;
        }

        XYZCoord pos = new XYZCoord(x, y, z);

        if (!isValid) {
            throw new RuntimeException("Invalid XYZ result: "+pos.toString() + " from WT3: "+this.toString());
        }


        debugPrint(pos.toString());

        //Utils.println("toCartesian time: "+ (System.currentTimeMillis()-start));

        return pos;
    }

    public WT3Coord subtract(WT3Coord wtc) {
        int[] c = wtc.getTachos();
        return new WT3Coord(new int[]{tachos[0]-c[0], tachos[1]-c[1], tachos[2]-c[2]});
    }

    public int[] getTachos() {
        return tachos;
    }

    private double getYCoordinate(double x, double z) {
        debugPrint("------------------------------ Height at intersecion point -----------------------------------------");

        //Distance from origo to intersection point
        double distToIntersectPoint = Math.sqrt( x*x + z*z );
        debugPrint("Dist to intersect point: " +distToIntersectPoint);

        return Math.sqrt(wireLengths[0] * wireLengths[0] - distToIntersectPoint * distToIntersectPoint);
    }

   /* private double getZCoordinateOld_deleteme(Triangle tri, double x) {
        XYZCoord[] trianglePoints = tri.getTrianglePoints();

        debugPrint("------------ Location inference test of point on p2p3 -------------------");

        double m2m3Dist = Utils.tachoToCm(tri.getCalibValues().p2p3tachoDist);  // b

// Cosine relation... (Ref: http://da.wikipedia.org/wiki/Cosinusrelation) + http://www.studieportalen.dk/kompendier/matematik/formelsamling/trigonometri/begreber/hoejde-grundlinje
        double angleAtM3 =  Math.acos((m2m3Dist * m2m3Dist + wireLengths[2] * wireLengths[2] - wireLengths[1] * wireLengths[1]) / (2 * m2m3Dist * wireLengths[2]));  // A
        debugPrint("Angle at M3: " + Math.toDegrees(angleAtM3));

// We now consider the rightangled triangle... (sine-relation here)
        double p2p3pos = wireLengths[2] * Math.sin(piHalf - angleAtM3);

        debugPrint("p2p3pos: " + p2p3pos);
        // Coordinates of the point between p2 and p3

        // z-coord and x-coord
        double zd = p2p3pos * Math.sin(tri.getAngleAtP3());
        double xd = trianglePoints[2].x - ( p2p3pos * Math.sin(piHalf-tri.getAngleAtP3()));

        debugPrint("Plane coords of point on the line between p2 and p3: (x, z) = (" + xd + ", " + zd + ")");
        debugPrint("------------------------------ Intersection of lines -----------------------------------------");


        double xDiff = xd - x;
        debugPrint("xDiff: " + xDiff);
        double z = zd + (xDiff * tri.getP1P2OrthogonalLineSlope());   // TODO check this!!!!
        debugPrint("finalZ: " + z);

        return z;
    }*/

    private double getZCoordinate(BaseGeometry tri, double x) {
        XYZCoord[] trianglePoints = tri.getTrianglePoints();

        debugPrint("------------ Location inference test of point on p1p2 -------------------");

        double m1m2Dist = Utils.tachoToCm(tri.getCalibValues().p1p2tachoDist);  // b

// Cosine relation... (Ref: http://da.wikipedia.org/wiki/Cosinusrelation) + http://www.studieportalen.dk/kompendier/matematik/formelsamling/trigonometri/begreber/hoejde-grundlinje
        double angleAtM1 =  Math.acos((wireLengths[0] * wireLengths[0] + m1m2Dist*m1m2Dist - wireLengths[1] * wireLengths[1]) / (2 * wireLengths[0] * m1m2Dist));  // A
        debugPrint("Angle at M1: " + Math.toDegrees(angleAtM1));

// We now consider the rightangled triangle... (sine-relation here)
        double p1p2pos = wireLengths[0] * Math.sin(piHalf - angleAtM1);

        debugPrint("p1p2pos: " + p1p2pos);
        // Coordinates of the point between p2 and p3

        // z-coord and x-coord
        // TODO sine stuff can be precalculated
        double zd = p1p2pos * Math.sin(tri.getAngleAtP1());
        double xd = p1p2pos * Math.sin(piHalf-tri.getAngleAtP1());

        debugPrint("Plane coords of point on the line between p1 and p2: (x, z) = (" + xd + ", " + zd + ")");
        debugPrint("------------------------------ Intersection of lines -----------------------------------------");


        double xDiff = xd - x;
        debugPrint("xDiff: " + xDiff);
        double z = zd + (xDiff * tri.getP1P2OrthogonalLineSlope());   // TODO check this!!!!
        debugPrint("finalZ: " + z);

        return z;
    }

    private double getXCoordinate(BaseGeometry tri) {
        debugPrint("------------ Location inference test X -------------------");
        for (int i=0;i<tachos.length;i++) debugPrint("#Tacho ["+(i+1)+"] = "+tachos[i] + " (cm: " + Utils.tachoToCm(tachos[i]) + ")");

        double m1m3Dist = Utils.tachoToCm(tri.getCalibValues().p1p3tachoDist);  // b

        // Cosine relation... (Ref: http://da.wikipedia.org/wiki/Cosinusrelation) + http://www.studieportalen.dk/kompendier/matematik/formelsamling/trigonometri/begreber/hoejde-grundlinje
        double angleAtM1 = Math.acos((m1m3Dist * m1m3Dist + wireLengths[0] * wireLengths[0] - wireLengths[2] * wireLengths[2]) / (2 * m1m3Dist * wireLengths[0]));  // A


        debugPrint("X-infer: Angle is: " + Math.toDegrees(angleAtM1));

        // We now consider the rightangled triangle... (sine-relation here)
        double triangleHeight = wireLengths[0] * Math.sin(angleAtM1); // d or hb (height from b)
        double p1p3pos = wireLengths[0] * Math.sin(piHalf - angleAtM1);

        debugPrint("2D: X-pos of point is: "+ p1p3pos + "cm");
        debugPrint("2D: Height at X-pos: " + triangleHeight + "cm");
        return p1p3pos;
    }
    
    public void debugPrint(String s) {
        if (debug) Utils.println(s);
    }
}
