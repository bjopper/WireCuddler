package dk.bjop.wirecuddler;

import dk.bjop.wirecuddler.config.CalibValues;
import dk.bjop.wirecuddler.gui.CuddleMenu;
import dk.bjop.wirecuddler.math.Triangle;
import dk.bjop.wirecuddler.math.Utils;

import java.io.IOException;


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


    public static void main(String[] args) throws InterruptedException, IOException {
        if (WireCuddler.isDevMode()) {
            Utils.println("------------------    DEV MODE  --------------------");

            // Set dev parameters
            CalibValues inst = CalibValues.createCalibInstance(0, 0, Utils.cmToTacho(193), Utils.cmToTacho(195), Utils.cmToTacho(143), 0); // Afstande: M1-M2: 193cm   M1-M3: 195cm   M2-M3: 143cm
            //inst.saveCalib();
        }
        else {

            // Load prod parameters
            CalibValues.loadCalib();
        }

        // TODO Create stop-system.
        new CuddleMenu().mainMenu();
    }


    private static double getOrigoToPointAngle(float x, float y) {
        CalibValues cv = Triangle.getInstance().getCalibValues();
        cv.getP1P3heightDiffCm();
        return Math.tan(y/x);
    }

}
