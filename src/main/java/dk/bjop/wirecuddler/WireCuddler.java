package dk.bjop.wirecuddler;

import dk.bjop.wirecuddler.util.CalibValues;
import dk.bjop.wirecuddler.util.Utils;
import dk.bjop.wirecuddler.util.XYZCoord;

import java.io.IOException;


/**
 * Created by bpeterse on 21-06-2014.
 */
public class WireCuddler {






    public static void main(String[] args) throws InterruptedException, IOException {

        // From a command line:
        // [leJOS Install Location]\bin\nxjchartinglogger.bat
        // Enter the NXT's name or address.  Be sure you have write permission
        // to the folder chosen by the utility.  If not, change the folder.
        // Click the Connect button

        // 20 motor revs = 34cm snor
        // 3:1 gearing
        // Omkreds af rulle = 34cm/(20 revs/3) = 51mm
        //  51mm snor-længde ændring = +- 1080 grader på motoraksel
        // Hastighed på 5 cm giver altså ca. 1080 grader pr sek.


        //MovementPath path1 = new MovementPathImpl();
        //MovementPath path1 = new MovementPathSinusImpl(10); // Perfect
        //MovementPath path1 = new MovementPathSinusImpl(24); // Perfect
        //MovementPath path1 = new MovementPathSinusImpl(60); // A little behind. Set lookahead til 1100 i stedt for 1000, og så spiller det meget bedre
        //MovementPath path1 = new MovementPathSinusImpl(24);
        //MovementPath path1 = new MovementPathSinus2Impl();


        //MotorPathController p1 = new MotorPathController(new NXTCuddleMotor(MotorPort.A), 1);
        //MotorPathController p2 = new MotorPathController(new NXTCuddleMotor(MotorPort.B), 2);

        //MotorSyncController syncController = new MotorSyncController(p1, p2);
        //syncController.go();

        //CalibValues.setTestdata();
        //CalibValues.saveCalib();
        CalibValues.loadCalib();


        Utils.println("-------------- Triangle setup calcs-----------------");


        double p1p2distXcm = Math.sqrt(Math.pow(Utils.tachoToCm(CalibValues.p1p2tachoDist), 2) - Math.pow(CalibValues.p1p2heightDiffCm,2));
        final double p1p3distXcm = Math.sqrt(Math.pow(Utils.tachoToCm(CalibValues.p1p3tachoDist), 2) - Math.pow(CalibValues.p1p3heightDiffCm,2));
        final double p2p3distXcm = Math.sqrt(Math.pow(Utils.tachoToCm(CalibValues.p2p3tachoDist), 2) - Math.pow(Math.max(CalibValues.p1p2heightDiffCm, CalibValues.p1p3heightDiffCm)-Math.min(CalibValues.p1p2heightDiffCm, CalibValues.p1p3heightDiffCm),2));

        Utils.println("Projected p1p2tachoDist CM: " + Utils.cmToTacho(p1p2distXcm) + " (cm: " + p1p2distXcm + ")");
        Utils.println("Projected p1p3tachoDist CM: " + Utils.cmToTacho(p1p3distXcm) + " (cm: " + p1p3distXcm + ")");
        Utils.println("Projected p2p3tachoDist CM: " + Utils.cmToTacho(p2p3distXcm) + " (cm: " + p2p3distXcm + ")");

        // Find angles of the projected triangular area using the projected values. Cosine-relation...
        final double p1AngleDeg = Math.toDegrees( Math.acos( (Math.pow(p1p3distXcm, 2) + Math.pow(p1p2distXcm, 2) - Math.pow(p2p3distXcm, 2)) / (2 * p1p3distXcm * p1p2distXcm)));
        final double p2AngleDeg = Math.toDegrees( Math.acos( (Math.pow(p2p3distXcm, 2) + Math.pow(p1p2distXcm, 2) - Math.pow(p1p3distXcm, 2)) / (2 * p2p3distXcm * p1p2distXcm)));
        final double p3AngleDeg = 180d - (p1AngleDeg + p2AngleDeg);

        Utils.println("p1AngleDeg: " + p1AngleDeg);
        Utils.println("p2AngleDeg: " + p2AngleDeg);
        Utils.println("p3AngleDeg: " + p3AngleDeg);

        // Build std cartesian coordinates of the points
        XYZCoord[] trianglePoints =  new XYZCoord[] { new XYZCoord(0, 0, 0),
                new XYZCoord(Math.cos(Math.toRadians(p1AngleDeg))*p1p2distXcm, CalibValues.p1p2heightDiffCm, Math.sin(Math.toRadians(p1AngleDeg))*p1p2distXcm),
                new XYZCoord(p1p3distXcm, CalibValues.p1p3heightDiffCm, 0)};


        Utils.println("-------------- Triangle points -----------------");
        for (int i=0;i<trianglePoints.length;i++) {
            Utils.println(trianglePoints[i].toString());
        }


        Utils.println("-------------- Calib values and tests -----------------");


        Utils.println("p1p2tachoDist: " + CalibValues.p1p2tachoDist + " (cm: " + Utils.tachoToCm(CalibValues.p1p2tachoDist) + ")");
        Utils.println("p1p3tachoDist: " + CalibValues.p1p3tachoDist + " (cm: " + Utils.tachoToCm(CalibValues.p1p3tachoDist) + ")");
        Utils.println("p2p3tachoDist: " + CalibValues.p2p3tachoDist + " (cm: " + Utils.tachoToCm(CalibValues.p2p3tachoDist) + ")");

        Utils.println("Conversion test: 10,7cm is in tacho: " + Utils.cmToTacho(10.7f));
        Utils.println("Conversion test: 10680 tachos is in CM: "+Utils.tachoToCm(10680));

        Utils.println("------------ Location inference test X -------------------");

        int[] tachos = new int[]{10000, 10000 , 10000};
        for (int i=0;i<tachos.length;i++) Utils.println("#Tacho ["+(i+1)+"] = "+tachos[i] + " (cm: " + Utils.tachoToCm(tachos[i]) + ")");

        int m1t = tachos[0];
        int m3t = tachos[2];

        double m1m3Dist = Utils.tachoToCm(CalibValues.p1p3tachoDist);  // b
        double m1wireLength = Utils.tachoToCm(m1t);  // c
        double m3WireLength = Utils.tachoToCm(m3t);  // a

        // Cosine relation... (Ref: http://da.wikipedia.org/wiki/Cosinusrelation) + http://www.studieportalen.dk/kompendier/matematik/formelsamling/trigonometri/begreber/hoejde-grundlinje
        double angleAtM1 = Math.toDegrees(Math.acos((m1m3Dist * m1m3Dist + m1wireLength * m1wireLength - m3WireLength * m3WireLength) / (2 * m1m3Dist * m1wireLength)));  // A


        Utils.println("X-infer: Angle is: " + angleAtM1);

        // We now consider the rightangled triangle... (sine-relation here)
        double triangleHeight = m1wireLength * Math.sin(Math.toRadians(angleAtM1)); // d or hb (height from b)
        double p1p3pos = m1wireLength * Math.sin(Math.toRadians(90 - angleAtM1));

        Utils.println("2D: X-pos of point is: "+ p1p3pos + "cm");
        Utils.println("2D: Height at X-pos: " + triangleHeight + "cm");


        Utils.println("------------ p2p3 slope -------------------");


       double p2p3Slope =  (trianglePoints[2].z - trianglePoints[1].z) / (trianglePoints[2].x - trianglePoints[1].x);
        double orthoSlope = 1/p2p3Slope;
       Utils.println("p2p3Slope: " + p2p3Slope);
       Utils.println("p2p3 orthos-slope: " + orthoSlope);


        Utils.println("------------ Location inference test of point on p2p3 -------------------");

        int m2t = tachos[1];

        double m2m3Dist = Utils.tachoToCm(CalibValues.p2p3tachoDist);  // b
        double m2wireLength = Utils.tachoToCm(m2t);  // c

// Cosine relation... (Ref: http://da.wikipedia.org/wiki/Cosinusrelation) + http://www.studieportalen.dk/kompendier/matematik/formelsamling/trigonometri/begreber/hoejde-grundlinje
        double angleAtM3 = Math.toDegrees( Math.acos( (m2m3Dist*m2m3Dist + m2wireLength*m2wireLength - m3WireLength*m3WireLength) / (2*m2m3Dist*m2wireLength) ));  // A
        Utils.println("Angle at M3: " + angleAtM3);

// We now consider the rightangled triangle... (sine-relation here)
        double triangleHeight2 = m2wireLength * Math.sin(Math.toRadians(angleAtM3)); // d or hb (height from b)
        double p2p3pos = m2wireLength * Math.sin(Math.toRadians(90 - angleAtM3));

        Utils.println("p2p3pos: " + p2p3pos);
        // Coordinates of the point between p2 and p3

        // z-coord and x-coord
        double z = p2p3pos * Math.sin(Math.toRadians(p3AngleDeg));

        Utils.println("z: " + z);

        double x = trianglePoints[2].x - ( p2p3pos * Math.sin(Math.toRadians(90-p3AngleDeg)));

        Utils.println("Plane coords of point on the line between p2 and p3: (x, z) = (" + x + ", " + z + ")");

        Utils.println("------------------------------ Intersection of lines -----------------------------------------");


        double xDiff = x - p1p3pos;
        Utils.println("xDiff: " + xDiff);
        double finalZ = z + (xDiff * orthoSlope);   // TODO check this!!!!
        Utils.println("finalZ: " + finalZ);

        Utils.println("------------------------------ Height at intersecion point -----------------------------------------");

        //Distance from origo to intersection point (pythagoras)
        double distToIntersectPoint = Math.sqrt( p1p3pos*p1p3pos + finalZ*finalZ );
        Utils.println("Dist to intersect point: " +distToIntersectPoint);


        double heightAtPoint = Math.sqrt(Utils.tachoToCm(tachos[0]) * Utils.tachoToCm(tachos[0]) - distToIntersectPoint * distToIntersectPoint);

        XYZCoord pos = new XYZCoord(p1p3pos, heightAtPoint, finalZ);
        Utils.println(pos.toString());

        Utils.println("--------------------- DONE - reversing -----------------------------");

        double[] rev = new double[3];

        rev[0] = Math.sqrt( pos.x*pos.x + pos.y*pos.y + pos.z*pos.z );

        double xd2 = pos.x - trianglePoints[1].x;
        double zd2 = pos.z - trianglePoints[1].z;
        rev[1] = Math.sqrt( xd2*xd2 + pos.y*pos.y + zd2*zd2 );

        double xd3 = pos.x - trianglePoints[2].x;
        rev[2] = Math.sqrt( xd3*xd3 + pos.y*pos.y + pos.z*pos.z );


        Utils.println("Converting the pos found back to tacho-array...");
        for (int i=0;i<rev.length;i++) {
            Utils.println("p" + (i+1) + " wire tacho: " + Utils.cmToTacho(rev[i]) + " (" + rev[i] + " cm)");
        }




        System.exit(0);

        try {
            new CuddleMenu().mainMenu();
        }
        catch (Exception e) {
            Utils.println("FUCKUP!!!!");
        }

        //TODO Husk mail om ug6 via intra


       /* pr.addMovementPath(new MotorPathImpl());
        pr.addMovementPath(new MotorPathSinusImpl(10));
        pr.addMovementPath(new MotorPathSinus3Impl(24));
        pr.addMovementPath(new MotorPathSinusImpl(40));
        pr.addMovementPath(new MotorPathSinus3Impl(60));
        pr.addMovementPath(new MotorPathSinus2Impl());
        pr.start();
        pr.join();*/


    }

}
