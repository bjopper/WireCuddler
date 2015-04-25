package dk.bjop.wirecuddler.math;

/**
 * Created by bpeterse on 20-09-2014.
 */
public class XYZCoord {

    public double x;
    public double y;
    public double z;

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

    public WT3Coord toWiresTachoCoord() {
        return WT3Coord.createWireCoordFromWireLengthsCM(toWirelengths());
    }

    public SphericCoord toSpheric() {
        double r = Math.sqrt( x*x + y*y + z*z );
        double theta = Math.acos( z / r);
        double phi = Math.atan2(y, x);
        return new SphericCoord(r, theta, phi);
    }

    public double distanceTo(XYZCoord p1) {
        double dx   = p1.x - x;
        double dy   = p1.y - y;
        double dz   = p1.z - z;
        double dist = Math.sqrt( dx*dx + dy*dy + dz*dz );
        return dist;
    }

    public XYZCoord subtract(XYZCoord p) {
       return new XYZCoord(x-p.x, y-p.y, z-p.z);
    }

    public XYZCoord add(XYZCoord p) {
        return new XYZCoord(x+p.x, y+p.y, z+p.z);
    }

    public boolean isValid() {
        return !Double.isNaN(x) && !Double.isNaN(y) && !Double.isNaN(z);
    }
}
