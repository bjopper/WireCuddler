package dk.bjop.wirecuddler.movement.moveproducers;

import dk.bjop.wirecuddler.config.CuddleProfile;
import dk.bjop.wirecuddler.math.Utils;
import dk.bjop.wirecuddler.math.coordinates.XYZCoord;
import dk.bjop.wirecuddler.math.geometry.Plane;
import dk.bjop.wirecuddler.movement.moves.MotorPathMove;
import dk.bjop.wirecuddler.movement.moves.StraightLineMove;

import java.util.Random;

/**
 * Created by bpeterse on 20-09-2014.
 */
public class CuddleMoveProducerRandomProfile implements CuddleMoveProducer {

    final double height = 100;

    CuddleProfile cp = null;

    XYZCoord topLeft = null;
    XYZCoord topRight = null;
    XYZCoord bottomRight = null;
    XYZCoord bottomLeft = null;

    Plane cuddlePlane;


    private static Random rnd = new Random(System.currentTimeMillis()+1);


    public CuddleMoveProducerRandomProfile(CuddleProfile cp) {
        this.cp = cp;

        XYZCoord[] torsoPoints = cp.getTorsoPoints();
        topLeft = torsoPoints[0];
        topRight = torsoPoints[1];
        bottomRight = torsoPoints[2];
        bottomLeft = torsoPoints[3];

        // Get cuddle-plane
        XYZCoord bottomMidpoint = Utils.findMidpoint(bottomRight, bottomLeft);

        cuddlePlane = new Plane(topLeft, topRight, bottomMidpoint);
    }

    public MotorPathMove getNewMove() {
        XYZCoord target = getMoveTarget();
        target.y = cuddlePlane.findY(target.x, target.z);
        return new StraightLineMove(target);
    }

    @Override
    public boolean hasMoreMoves() {
        return true;
    }

    private XYZCoord getRandomPointOnLine(XYZCoord p1, XYZCoord p2) {
        if (p1.equals(p2)) throw new RuntimeException("Error: points are equal!?");

        Utils.println("P1: "+p1.toString());
        Utils.println("P2: "+p2.toString());

        double maxX = Math.max(p1.x, p2.x);
        double minX = Math.min(p1.x, p2.x);
        double xDiff = maxX-minX;

        if (xDiff == 0) {
            // Find point beteween minZ and maxZ
            double maxZ = Math.max(p1.z, p2.z);
            double minZ = Math.min(p1.z, p2.z);
            double zDiff = maxZ-minZ;

            return new XYZCoord(p1.x, 0, rnd.nextDouble() * zDiff + minZ);
        }
        else if (p1.z == p2.z) {
            // Find point beteween minX and maxX
            return new XYZCoord(rnd.nextDouble() * xDiff + minX, 0, p1.z);
        }

        double zDiff = p2.z - p1.z;
        Utils.println("zDiff: "+zDiff);
        double slope = (zDiff) / (p2.x - p1.x);
        Utils.println("xDiff: "+xDiff);
        Utils.println("slope value: "+slope);
        double rndxxx = rnd.nextDouble();
        Utils.println("rndxxx: "+rndxxx);
        double rndX = rndxxx * (p2.x - p1.x);
        Utils.println("rndX: "+rndX);
        Utils.println("p1.z: "+p1.z);

        double zAdd = rndX * slope;
        Utils.println("zAdd: "+zAdd);

        return new XYZCoord(rndX + minX, 0, p1.z + zAdd);
    }


    private XYZCoord getMoveTarget() {
        XYZCoord target = null;
        int c = (rnd.nextInt(40)+10) / 10; // Weird...  rnd.nextInt(4)+1;  only output sequence 1 2 2 1 2 2 1 2 2 1 2 2 1 for ever???
        switch (c) {
            case 1: target = getRandomPointOnLine(topLeft, topRight);break;
            case 2: target = getRandomPointOnLine(topRight, bottomRight);break;
            case 3: target = getRandomPointOnLine(bottomLeft, bottomRight);break;
            case 4: target = getRandomPointOnLine(topLeft, bottomLeft);break;
        }
        return target;
    }

}
