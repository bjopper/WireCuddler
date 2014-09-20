package dk.bjop.wirecuddler;

/**
 * Created by bpeterse on 20-09-2014.
 */
public class XYCoord {

    double x;
    double y;

    public XYCoord(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
