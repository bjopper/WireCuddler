package dk.bjop.wirecuddler.config;

import dk.bjop.wirecuddler.math.Utils;

import java.io.*;
import java.util.Properties;

/**
 * Created by bpeterse on 05-11-2014.
 */
public class CalibValues {
    private static final String default_calibFile_dev = "dev_calib.bin";
    private static final String default_calibFile_prod = "prod_calib.bin";

    private static String P1_P2_DIST_MM = "p1p2DistMm";
    private static String P1_P3_DIST_MM = "p1p3DistMm";
    private static String P2_P3_DIST_MM = "p2p3DistMm";
    private static String P1_P2_HEIGHTDIFF_MM = "p1p2HeightDiffMm";
    private static String P1_P3_HEIGHTDIFF_MM = "p1p3HeightDiffMm";
    private static String RESTPOINT_INDEX = "restpointIndex";

    private static boolean isInDevMode = false;

    private String fileOrigin = null;

    private int p1p2heightDiffMm = 0;
    private int p1p3heightDiffMm = 0;

    private int p1p2distMm = -1;
    private int p1p3distMm = -1;
    private int p2p3distMm = -1;

    private int restpoint = -1; // motor-index MOTORPORT.A = 0, B = 1, C = 2

    //public static int cuddlePlaneHeights[] = new int[3]; // Three point that define the plane...
    private static CalibValues instance = null;

    public static CalibValues getInstance() {
        if (instance == null) throw new RuntimeException("Calib-object has not been initialized!");
        return instance;
    }

    private CalibValues(int p1p2heightDiffMm, int p1p3heightDiffMm, int p1p2DistMm, int p1p3DistMm, int p2p3DistMm, int restpoint, String fileOrigin) {
        this.p1p2heightDiffMm = p1p2heightDiffMm;
        this.p1p3heightDiffMm = p1p3heightDiffMm;
        this.p1p2distMm = p1p2DistMm;
        this.p1p3distMm = p1p3DistMm;
        this.p2p3distMm = p2p3DistMm;
        this.restpoint = restpoint;
        this.fileOrigin = fileOrigin;
    }

    public void saveCalib() {

        Properties p = new Properties();
        p.setProperty(P1_P2_DIST_MM,""+p1p2distMm);
        p.setProperty(P1_P3_DIST_MM, ""+p1p3distMm);
        p.setProperty(P2_P3_DIST_MM, ""+p2p3distMm);
        p.setProperty(P1_P2_HEIGHTDIFF_MM, ""+p1p2heightDiffMm);
        p.setProperty(P1_P3_HEIGHTDIFF_MM, ""+p1p3heightDiffMm);
        p.setProperty(RESTPOINT_INDEX, ""+restpoint);

        String filename =getFilename();
        FileOutputStream out = null; // declare outside the try block
        File data = new File(filename);
        try {
            out = new FileOutputStream(data);
            p.store(out, "Calibration data");
            out.close(); // flush the buffer and write the file
            Utils.println("Calibration data saved to file: '" + filename + "'\n" + toString());
        } catch (IOException e) {
            Utils.println(e.getMessage());
        }
    }

    public static CalibValues setNewCalibInstance(int p1p2heightDiffMm, int p1p3heightDiffMm, int p1p2distMm, int p1p3distMm, int p2p3distMm, int restpointIndex) {
        instance = new CalibValues(p1p2heightDiffMm, p1p3heightDiffMm, p1p2distMm, p1p3distMm, p2p3distMm, restpointIndex, null);
        return getInstance();
    }

    public static CalibValues createCalibInstance(int p1p2heightDiffMm, int p1p3heightDiffMm, int p1p2distMm, int p1p3distMm, int p2p3distMm, int restpointIndex) {
        return new CalibValues(p1p2heightDiffMm, p1p3heightDiffMm, p1p2distMm, p1p3distMm, p2p3distMm, restpointIndex, null);
    }

    public static boolean calibrationFileExist() {
        return new File(getFilename()).exists();
    }

    public static void setDevMode(boolean devMode) {
        isInDevMode = devMode;
    }

    public static boolean getDevMode() {
        return isInDevMode;
    }

    private static String getFilename() {
        return isInDevMode ? default_calibFile_dev : default_calibFile_prod;
    }

    public static CalibValues loadCalib() {
        String filename = getFilename();
        File data = new File(filename);
        Utils.println("Trying to load calib from file: '" + filename + "'");

        Properties p = new Properties();
        try {
            FileInputStream is = new FileInputStream(data);
            p.load(is);
            is.close();
            Utils.println("Calibration data loaded!");
            instance = new CalibValues(getIntProperty(P1_P2_HEIGHTDIFF_MM, p),
                    getIntProperty(P1_P3_HEIGHTDIFF_MM, p),
                    getIntProperty(P1_P2_DIST_MM, p),
                    getIntProperty(P1_P3_DIST_MM, p),
                    getIntProperty(P2_P3_DIST_MM, p),
                    getIntProperty(RESTPOINT_INDEX, p),
                    filename);
        } catch (IOException ioe) {
            Utils.println("Read Exception");
        }

        return getInstance();
    }

    private static int getIntProperty(String key, Properties p) {
        String v = p.getProperty(key);
        if (v == null) throw new RuntimeException("ERROR Unable to load property: '" + key + "' from file.");
        return Integer.parseInt(v);
    }

    public String toString() {
        String s="-------------------------------------------------------------\n";
        s=s+"Calib filename: '" + fileOrigin + "'\n";
        s=s+"p1p2heightDiffMm = " + p1p2heightDiffMm + " mm\n";
        s=s+"p1p3heightDiffMm = " + p1p3heightDiffMm + " mm\n";
        s=s+"p1p2distMm = " + p1p2distMm + " mm\n";
        s=s+"p1p3distMm = " + p1p3distMm + " mm\n";
        s=s+"p2p3distMm = " + p2p3distMm + " mm\n";
        s=s+"restpoint = P" + (restpoint+1) + "\n";
        s=s+"-------------------------------------------------------------\n";
        return s;
    }

    public int getP1P2heightDiffMm() {
        return p1p2heightDiffMm;
    }

    public int getP1P3heightDiffMm() {
        return p1p3heightDiffMm;
    }

    public int getP1P2distMm() {
        return p1p2distMm;
    }

    public int getP1P3distMm() {
        return p1p3distMm;
    }

    public int getP2P3distMm() {
        return p2p3distMm;
    }

    public int getRestpointIndex() {
        return restpoint;
    }
}
