package dk.bjop.wirecuddler.math;

/**
 * Created by bpeterse on 29-12-2014.
 */
public class SphericCoord {

    public double r;
    public double theta;
    public double phi;

    public SphericCoord(double r, double theta, double phi) {
        this.r = r;
        this.theta = theta;
        this.phi = phi;
    }

    public XYZCoord toCartesian() {
        double x = r * Math.sin(theta) * Math.cos(phi);
        double y = r * Math.sin(theta) * Math.sin(phi);
        double z = r * Math.cos(theta);
        return new XYZCoord(x, y, z);
    }

    public String toString() {
        return "radius="+r+"  theta="+Math.toDegrees(theta)+"  phi="+Math.toDegrees(phi);
    }

}
