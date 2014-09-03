package dk.bjop.wirecuddler;

import lejos.addon.gps.GPSListener;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;

/**
 * Created by bpeterse on 01-07-2014.
 *
 */
public class ExperimentOne extends Thread {
    float speed = 5; // cm/sec
    float lowerTicklePointDistanceCm = 50; // cm


    public ExperimentOne() {

    }

    public void run() {
        NXTRegulatedMotor m1 = new NXTRegulatedMotor(MotorPort.A);
        m1.setSpeed(0);
        m1.setAcceleration(0);
        m1.forward();

        long intValMillis = 1000;

        int tacho = m1.getTachoCount();
        int targetTacho = getExpectedTachoPosAtTimeT(20000, speed);
            long startTime =  System.currentTimeMillis();
            while (tacho < Math.abs(targetTacho)) {

                long t = System.currentTimeMillis();

                // Compare current tacho with where we should be
                int currTacho = m1.getTachoCount();
                int err = currTacho - getExpectedTachoPosAtTimeT(t-startTime, speed);

                int nextTachoPos = getExpectedTachoPosAtTimeT(t-startTime + intValMillis, speed); // Get the tacho-pos we want to be at 1 sec from now

                int tachosToEat = nextTachoPos - currTacho;
                m1.setAcceleration(10); // Test - must be calclulated properly at a latet time
                m1.setSpeed(tachosToEat); // Will be to small as it assumes instant acceleration


            try {
                Thread.sleep(intValMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public int getExpectedTachoPosAtTimeT(float elapsedTimeMillis, float speedCmSec) {
        // a = vandret sidelængde top
        // b = lodret sidelængde nedad (konstant)
        // c = hypotenusen

        float a = (elapsedTimeMillis / 1000) * speedCmSec;
        float b = lowerTicklePointDistanceCm;
        float c = (float) Math.sqrt(Math.pow(a,2) + Math.pow(b, 2));
        return cmToTacho(c-b); // we subtract b as we are only interested in the diff
    }

    public int cmToTacho(float lengthCm) {
        double barrelRevs = lengthCm / GPS.wireBarrelCircumference;
        double motorRevs = barrelRevs / GPS.gearing;
        int tachoCount = (int)motorRevs * 360;
        return tachoCount;
    }

    public float tachoToCm(int tachoCount) {
        float motorRevs = tachoCount/360;
        float barrelRevs = motorRevs * GPS.gearing;
        float length = barrelRevs * GPS.wireBarrelCircumference;
        return length;
    }


}
