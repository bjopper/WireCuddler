package dk.bjop.wirecuddler.motor;

import lejos.nxt.NXTRegulatedMotor;

/**
 * Created by bpeterse on 13-09-2014.
 */
public class MotorSyncController {
    NXTRegulatedMotor m1;
    NXTRegulatedMotor m2;
    NXTRegulatedMotor m3;

    MotorPathController p1;
    MotorPathController p2;
    MotorPathController p3;


    public MotorSyncController(NXTRegulatedMotor... motors) {

    }
}
