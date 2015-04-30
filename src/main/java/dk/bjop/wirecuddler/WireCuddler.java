package dk.bjop.wirecuddler;

import dk.bjop.wirecuddler.config.CalibValues;
import dk.bjop.wirecuddler.gui.CuddleMenu;
import dk.bjop.wirecuddler.math.BaseGeometry;
import dk.bjop.wirecuddler.math.Utils;


/**
 * Created by bpeterse on 21-06-2014.
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
            //CalibValues inst = CalibValues.createCalibInstance(0, 0, Utils.cmToTacho(193), Utils.cmToTacho(195), Utils.cmToTacho(143), 0); // Afstande: M1-M2: 193cm   M1-M3: 195cm   M2-M3: 143cm
            //inst.saveCalib();
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

        new CuddleMenu().mainMenu();
    }


    private static double getOrigoToPointAngle(float x, float y) {
        CalibValues cv = BaseGeometry.getInstance().getCalibValues();
        cv.getP1P3heightDiffCm();
        return Math.tan(y/x);
    }

    // This is the only known way to make sure the NXT shuts down the running program!? (and then lejos turns off by auto after a while)
    public static void terminateProgram(String msg) {
        throw new RuntimeException(msg == null ? "Terminating system." : msg);
    }

}
