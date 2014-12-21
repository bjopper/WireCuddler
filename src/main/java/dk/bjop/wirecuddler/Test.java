package dk.bjop.wirecuddler;

import dk.bjop.wirecuddler.motor.MotorPath;
import dk.bjop.wirecuddler.motor.NXTCuddleMotor;
import dk.bjop.wirecuddler.util.CalibValues;

/**
 * Created by bpeterse on 14-11-2014.
 */
public class Test {

    public Test() {

    }

    public void moveToPoint(int x, int y, int z) {
        // Assume we are at restpoint
        MotorPath m1;
        MotorPath m2;
        MotorPath m3;

        int[] coordBaseline = CalibValues.baseline;
        switch (coordBaseline[0]) {
            case 0:
        }

    }
}
