package dk.bjop.wirecuddler;

import dk.bjop.wirecuddler.config.CalibValues;
import dk.bjop.wirecuddler.gui.CuddleMenu;
import dk.bjop.wirecuddler.math.RotationMatrix;
import dk.bjop.wirecuddler.math.Triangle;
import dk.bjop.wirecuddler.math.Utils;
import dk.bjop.wirecuddler.math.XYZCoord;

import java.io.IOException;


/**
 * Created by bpeterse on 21-06-2014.
 */
public class WireCuddler {

    public static String default_calibFile = "calib.bin";

    //http://www.tcsme.org/Papers/Vol36/Vol36No2Paper7.pdf


    public static void main(String[] args) throws InterruptedException, IOException {

        // From a command line:
        // [leJOS Install Location]\bin\nxjchartinglogger.bat
        // Enter the NXT's name or address.  Be sure you have write permission
        // to the folder chosen by the utility.  If not, change the folder.
        // Click the Connect button

        // 20 motor revs = 34cm snor
        // 3:1 gearing
        // Omkreds af rulle = 34cm/(20 revs/3) = 51mm
        //  51mm snor-længde ændring = +- 1080 grader på motoraksel
        // Hastighed på 5 cm giver altså ca. 1080 grader pr sek.


        //MovementPath path1 = new MovementPathImpl();
        //MovementPath path1 = new MovementPathSinusImpl(10); // Perfect
        //MovementPath path1 = new MovementPathSinusImpl(24); // Perfect
        //MovementPath path1 = new MovementPathSinusImpl(60); // A little behind. Set lookahead til 1100 i stedt for 1000, og så spiller det meget bedre
        //MovementPath path1 = new MovementPathSinusImpl(24);
        //MovementPath path1 = new MovementPathSinus2Impl();


        //MotorPathController p1 = new MotorPathController(new NXTCuddleMotor(MotorPort.A), 1);
        //MotorPathController p2 = new MotorPathController(new NXTCuddleMotor(MotorPort.B), 2);

        //MotorSyncController syncController = new MotorSyncController(p1, p2);
        //syncController.go();

        //CalibValues.setTestdata();
        //CalibValues.saveCalib();


        // cretaeTestdata(double p1p2heightDiffCm, double p1p3heightDiffCm, int p1p2tachoDist, int p1p3tachoDist, int p2p3tachoDist, int restpointIndex)

        //CalibValues test = CalibValues.cretaeTestdata(0, 0, 13114, 13428, 10537, 0); // Std triangle with all points at same height

        // Afstande: M1-M2: 193cm   M1-M3: 195cm   M2-M3: 143cm
        CalibValues test = CalibValues.cretaeTestdata(0, 200, Utils.cmToTacho(193), Utils.cmToTacho(195), Utils.cmToTacho(143), 0); //  P3 elevated

        Triangle tri = Triangle.getInstance();
        double alpha = tri.getAngleAtP1();
        double beta = tri.getAngleAtP2();

        RotationMatrix rm = new RotationMatrix();
        rm.changeangle(alpha, 0, 0);
        XYZCoord rotatedP1 = rm.transform(tri.getP3(),new XYZCoord(0,0,0));

        tri.getP1().toString();
        Utils.println("P1 () rotated by alpha only --> ()");

        //test.saveCalib(WireCuddler.default_calibFile);

        //System.exit(0);

        // TODO Create stop-system.


        new CuddleMenu().mainMenu();




       /* pr.addMovementPath(new MotorPathImpl());
        pr.addMovementPath(new MotorPathSinusImpl(10));
        pr.addMovementPath(new MotorPathSinus3Impl(24));
        pr.addMovementPath(new MotorPathSinusImpl(40));
        pr.addMovementPath(new MotorPathSinus3Impl(60));
        pr.addMovementPath(new MotorPathSinus2Impl());
        pr.start();
        pr.join();*/


    }


    private static double getOrigoToPointAngle(float x, float y) {
        CalibValues cv = Triangle.getInstance().getCalibValues();
        cv.getP1P3heightDiffCm();
        return Math.tan(y/x);
    }

}
