package dk.bjop.wirecuddler.movement.moveproducers;

import dk.bjop.wirecuddler.config.CuddleProfile;
import dk.bjop.wirecuddler.math.coordinates.XYZCoord;
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

    /*Line2D top;
    Line2D bottom;
    Line2D left;
    Line2D right;*/

    private static Random rnd = new Random(System.currentTimeMillis()+1);


    public CuddleMoveProducerRandomProfile(CuddleProfile cp) {
        this.cp = cp;

        XYZCoord[] torsoPoints = cp.getTorsoPoints();
        topLeft = torsoPoints[0];
        topRight = torsoPoints[1];
        bottomRight = torsoPoints[2];
        bottomLeft = torsoPoints[3];

        /*top = new Line2D(topLeft, topRight);
        bottom = new Line2D(bottomLeft, bottomRight);
        left = new Line2D(topLeft, bottomLeft);
        right = new Line2D(topRight, bottomRight);*/

        double topLineAvgHeight = (torsoPoints[0].y + torsoPoints[1].y) / 2d;
        double bottomAvgLineHeight = (torsoPoints[2].y + torsoPoints[3].y) / 2d;

        double heightDiff = topLineAvgHeight - bottomAvgLineHeight;
    }

    public MotorPathMove getNewMove() {
        XYZCoord target = getMoveTarget();

        // And now...   the Y coordinate!!  Yiihaaaaa  :)
        target.y = height;

        return new StraightLineMove(target);
    }

    @Override
    public boolean hasMoreMoves() {
        return true;
    }

    private XYZCoord getRandomPointOnLine(XYZCoord p1, XYZCoord p2) {
        if (p1.equals(p2)) throw new RuntimeException("Error: points are equal!?");

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

        double slope =  1d / ((p1.z - p2.z) / (p2.x - p1.x));
        double rndX = rnd.nextDouble() * xDiff;

        return new XYZCoord(rndX + minX, 0, p1.z + rndX * slope);
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

    private XYZCoord getMoveTargetPoint() {
        int maxX = 130;
        int minX = 70;
        int maxZ = 60;
        int minZ = 10;


        int x = 0 ;
        int z = 0;
        int c;
        // rnd 1-4    random.nextInt(max - min + 1) + min
        Random rnd = new Random(System.currentTimeMillis()+1);

        c = (rnd.nextInt(40)+10) / 10; // Weird...  rnd.nextInt(4)+1;  only output sequence 1 2 2 1 2 2 1 2 2 1 2 2 1 for ever???

        if (c == 1) {
            x = minX;
            z = rnd.nextInt(maxZ - minZ + 1) + minZ;
        }
        else if (c == 2) {
            x = maxX;
            z = rnd.nextInt(maxZ - minZ + 1) + minZ;
        }
        else if (c == 3) {
            z = minZ;
            x = rnd.nextInt(maxX - minX + 1) + minX;
        }
        else if (c == 4) {
            z = maxZ;
            x = rnd.nextInt(maxX - minX + 1) + minX;
        }

        return new XYZCoord(x, height, z);
    }

}
