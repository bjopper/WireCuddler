package dk.bjop.wirecuddler.util;

import dk.bjop.wirecuddler.model.Triangle;

/**
 * Created by bpeterse on 20-09-2014.
 */
public class XYZCoord {

    public final double x;
    public final double y;
    public final double z;

    public XYZCoord(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String toString() {
        return "(X, Y, Z) = ("+x+", "+y+", "+z+")";
    }

    public double[] toWirelengths() {

        XYZCoord[] trianglePoints = Triangle.getInstance().getTrianglePoints();

        double[] rev = new double[3];

        rev[0] = Math.sqrt( x*x + y*y + z*z );

        double xd2 = x - trianglePoints[1].x;
        double zd2 = z - trianglePoints[1].z;
        rev[1] = Math.sqrt( xd2*xd2 + y*y + zd2*zd2 );

        double xd3 = x - trianglePoints[2].x;
        rev[2] = Math.sqrt( xd3*xd3 + y*y + z*z );

        return rev;
    }

}
