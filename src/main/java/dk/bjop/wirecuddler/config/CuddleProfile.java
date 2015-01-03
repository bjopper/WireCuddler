package dk.bjop.wirecuddler.config;

/**
 * Created by bpeterse on 24-12-2014.
 */
public class CuddleProfile {

    double[] cuddlePlane; // 3 points making up a plane
    double[] cuddleArea; // The area within the cuddleplane to cuddle. A list of xyz-points, where the last point connects to the first which makes up an area.

    public CuddleProfile() {

    }

}
