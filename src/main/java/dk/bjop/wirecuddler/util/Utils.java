package dk.bjop.wirecuddler.util;


import lejos.nxt.comm.RConsole;

/**
 * Created by bpeterse on 10-09-2014.
 */
public class Utils {


    public static int cmToTacho(float lengthCm) {
        double barrelRevs = lengthCm / Constants.wireBarrelCircumference;
        double motorRevs = barrelRevs * Constants.gearing;
        int tachoCount = (int) (motorRevs * 360f);
        return tachoCount;
    }

    public float tachoToCm(int tachoCount) {
        float motorRevs = tachoCount/360f;
        float barrelRevs = motorRevs * Constants.gearing;
        float length = barrelRevs * Constants.wireBarrelCircumference;
        return length;
    }

    public static void println(String s) {
        if (RConsole.isOpen()) {
            RConsole.println(s);
        }
    }

}
