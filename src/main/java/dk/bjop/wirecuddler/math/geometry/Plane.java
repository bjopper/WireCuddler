package dk.bjop.wirecuddler.math.geometry;

import dk.bjop.wirecuddler.math.coordinates.XYZCoord;

/**
 * Created by bpeterse on 14-05-2015.
 *
 * http://www.had2know.com/academics/equation-plane-through-3-points.html
 * or
 *  Ref: http://keisan.casio.com/exec/system/1223596129
 *
 *  Plane equation: ax+by+cz+d=0
 *
 */
public class Plane {
    XYZCoord p1;
    XYZCoord p2;
    XYZCoord p3;

    double[] eq;

    public Plane(XYZCoord p1, XYZCoord p2, XYZCoord p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;

        // Plane equation: ax+by+cz+d=0

        // Calc cross-product of the 2 vectors spanned by the points (the normal vector components are also the coeficients of the equation)
        double a = (p2.y - p1.y) * (p3.z - p1.z) - (p3.y - p1.y) * (p2.z - p1.z);
        double b = (p2.z - p1.z) * (p3.x - p1.x) - (p3.z - p1.z) * (p2.x - p1.x);
        double c = (p2.x - p1.x) * (p3.y - p1.y) - (p3.x - p1.x) * (p2.y - p1.y);

        // Find d by plugging any one of the three points into the equation
        double d = -(a*p1.x + b*p1.y + c* p1.z);

        this.eq = new double[]{a, b, c, d};
    }

    public double findY(double x, double z) {
        return - (eq[0]*x + eq[2]*z + eq[3]) / eq[1];
    }

    public String toString() {
        String s ="-----------------------------------------------\n";
        s+= "Plane equation is: " +  eq[0] + "x " + (eq[1]>=0?"+":"-") +" " + Math.abs(eq[1]) + "y " + (eq[2]>=0?"+":"-") + " " + Math.abs(eq[2]) + "z " + (eq[3]>=0?"+":"-") + " " + Math.abs(eq[3]) + " = 0\n";
        s+= "Plane points: \n";
        s+= "P1: " + p1.toString() + "\n";
        s+= "P2: " + p2.toString() + "\n";
        s+= "P3: " + p3.toString() + "\n";
        s+="-----------------------------------------------\n";
        return s;
    }
}
