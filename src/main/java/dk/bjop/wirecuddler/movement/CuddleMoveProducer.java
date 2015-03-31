package dk.bjop.wirecuddler.movement;

import dk.bjop.wirecuddler.math.XYZCoord;
import dk.bjop.wirecuddler.movement.moves.MotorPathMove;
import dk.bjop.wirecuddler.movement.moves.StraightToPointMove;

import java.util.Random;

/**
 * Created by bpeterse on 20-09-2014.
 */
public class CuddleMoveProducer {

    final double height = 100;

    public CuddleMoveProducer() {}

    public MotorPathMove getNewMove(XYZCoord startPos) {
        return new StraightToPointMove(startPos, getMoveTargetPoint());
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
