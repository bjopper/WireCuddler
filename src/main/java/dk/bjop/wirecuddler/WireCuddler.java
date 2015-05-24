package dk.bjop.wirecuddler;

import dk.bjop.wirecuddler.config.CalibValues;
import dk.bjop.wirecuddler.gui.CuddleMenu;
import dk.bjop.wirecuddler.math.Utils;
import lejos.nxt.LCD;


/**
 * Created by bpeterse on 21-06-2014.
 *
 *  Installing lejos snapshot, see http://lejos.sourceforge.net/forum/viewtopic.php?t=1828&highlight=snapshot  (required to avoid TinyVM 64k linker limit)   See lejos trunk rev 6817
 *  If problem with "Bossa" port stuff, see: http://www.robotc.net/forums/viewtopic.php?f=1&t=6278&sid=bb8757e359d3bfdfcf574316b275cf0c&view=print
 *
 *  From 14/5-2015 running on lejos snapshot rev. 7036. Has a bug that means you cannot catch exceptions!?
 *
 */
public class WireCuddler {

    //http://www.tcsme.org/Papers/Vol36/Vol36No2Paper7.pdf

    // From a command line:
    // [leJOS Install Location]\bin\nxjchartinglogger.bat
    // Enter the NXT's name or address.  Be sure you have write permission
    // to the folder chosen by the utility.  If not, change the folder.
    // Click the Connect button


    public static final boolean devMode = true;

    public static boolean isDevMode() {
        return devMode;
    }


    public static void main(String[] args) throws InterruptedException {

        if (WireCuddler.isDevMode()) {
            Utils.println("------------------    DEV MODE  --------------------");

            CalibValues.setDevMode(true);

            // Set dev parameters
            CalibValues inst = CalibValues.setNewCalibInstance(0, 0, 1930, 1950, 1430, 0); // Afstande: M1-M2: 193cm   M1-M3: 195cm   M2-M3: 143cm
            inst.saveCalib();
        }

        if (CalibValues.calibrationFileExist()) CalibValues.loadCalib();


        // Profile testing...
        /*CuddleProfile.deleteProfile("WCProfile1.cfg");
        CuddleProfile.deleteProfile("WCProfile2.cfg");
        CuddleProfile.deleteProfile("WCProfile3.cfg");

        CuddleProfile.dumpProfileFilenames();


        XYZCoord[] tp = new XYZCoord[]{new XYZCoord(1,2,3), new XYZCoord(4,5,6), new XYZCoord(7,8,9), new XYZCoord(10,11,12)};

        CuddleProfile cp = new CuddleProfile(tp, null, null);

        String pName = null;
        try {
            pName = CuddleProfile.getFirstAvailableFilename();
        } catch (Exception e) {
            e.printStackTrace();
        }

        cp.saveProfile(pName, false);

        XYZCoord[] lp = new XYZCoord[]{new XYZCoord(13,14,15), new XYZCoord(16,17,18)};
        cp.setLegPoints(lp);
        cp.saveProfile(pName, true);

        XYZCoord[] ap = new XYZCoord[]{new XYZCoord(19,20,21), new XYZCoord(22,23,24)};
        cp.setArmPoints(ap);
        cp.saveProfile(pName, true);

        CuddleProfile.dumpProfileFilenames();

        CuddleProfile cpl = CuddleProfile.loadProfile("WCProfile1.cfg");
        Utils.println(cpl.toString());

        CuddleProfile.dumpProfileFilenames();



        terminateProgram(null);*/

/*
        // Plane-equation derive  testing
        Plane plane = new Plane(new XYZCoord(10, 10, 25), new XYZCoord(30, 10, 10), new XYZCoord(20, 5, 10));

        Utils.println(plane.toString());

        Utils.println("Y for p1: " + plane.findY(10,25));
        Utils.println("Y for p2: " + plane.findY(30,10));
        Utils.println("Y for p3: " + plane.findY(20,10));

        Utils.println("Y for TEST: " + plane.findY(0,0));
        Utils.println("Y for TEST: " + plane.findY(50,50));

        XYZCoord p1 = new XYZCoord(10,20,30);
        XYZCoord p2 = new XYZCoord(2,4,6);
        Utils.println("P1 midpoint test: " + p1.toString());
        Utils.println("P2 midpoint test: " + p2.toString());
        Utils.println("Midpoint is: "+ Utils.findMidpoint(p1, p2));

        terminateProgram(null);*/


       // new ValueSelect().selectValueMenu("  P1 - P2 dist");
        //terminateProgram("Testing...");

        new CuddleMenu().mainMenu();
    }


    /*private static double getOrigoToPointAngle(float x, float y) {
        CalibValues cv = BaseGeometry.getInstance().getp1
        cv.getP1P3heightDiffCm();
        return Math.tan(y/x);
    }*/

    // This is the only known way to make sure the NXT shuts down the running program!? (and then lejos turns off by auto after a while)
    public static void terminateProgram(String reason) {
        LCD.clear();
        LCD.drawString(" SHUTDOWN!!!", 0, 4, false);

        Utils.println("\n*\n*\n*\n---------- TERMINATING SYSTEM NOW! REASON: '" + reason + "' ----------\n*\n*\n*\n");
        throw new RuntimeException(reason == null ? "Terminating system." : "Terminating system. Reason: " + reason);
    }

}
