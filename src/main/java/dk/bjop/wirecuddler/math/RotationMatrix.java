package dk.bjop.wirecuddler.math;

/**
 * Created by bpeterse on 29-03-2015.
 *
 * Found at: http://www.java-gaming.org/index.php?topic=30834.0
 */
public class RotationMatrix
{
    public double alpha;
    public double beta;
    public double gamma;
    double rm[];

    public void changeangle(double rotA, double rotB, double rotC)
    {
        alpha = rotA;
        beta = rotB;
        gamma = rotC;
        double sin_a = Math.sin(alpha);
        double sin_b = Math.sin(beta);
        double sin_g = Math.sin(gamma);

        double cos_a = Math.cos(alpha);
        double cos_b = Math.cos(beta);
        double cos_g = Math.cos(gamma);

        rm = new double[] { cos_a * cos_b, (cos_a * sin_b * sin_g) - (sin_a * cos_g), (cos_a * sin_b * cos_g) + (sin_a * sin_g),
                sin_a * cos_b, (sin_a * sin_b * sin_g) + (cos_a * cos_g), (sin_a * sin_b * cos_g) - (cos_a * sin_g),
                -sin_b        ,  cos_b * sin_g                           , cos_b * cos_g                            };

    }

    public XYZCoord transform(XYZCoord rotpoint, XYZCoord centerpoint)
    {
        double px = rotpoint.x - centerpoint.x,
                py = rotpoint.y - centerpoint.y,
                pz = rotpoint.z - centerpoint.z;

        rotpoint.x = rm[0]*px + rm[1]*py + rm[2]*pz;
        rotpoint.y = rm[3]*px + rm[4]*py + rm[5]*pz;
        rotpoint.z = rm[6]*px + rm[7]*py + rm[8]*pz;

        return new XYZCoord(rotpoint.x += centerpoint.x,
                rotpoint.y += centerpoint.y,
                rotpoint.z += centerpoint.z);
    }
}