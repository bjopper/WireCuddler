package dk.bjop.wirecuddler.config;

import dk.bjop.wirecuddler.math.Utils;

import java.io.*;

/**
 * Created by bpeterse on 05-11-2014.
 */
public class CalibValues {
    public static String default_calibFile = "calib.bin";

    public String fileOrigin = null;

    public double p1p2heightDiffCm = 0;
    public double p1p3heightDiffCm = 0;

    public int p1p2tachoDist = -1;
    public int p1p3tachoDist = -1;
    public int p2p3tachoDist = -1;

    public int restpoint = -1; // motor-index MOTORPORT.A = 0, B = 1, C = 2

    //public static int cuddlePlaneHeights[] = new int[3]; // Three point that define the plane...
    private static CalibValues instance = null;

    public static CalibValues getInstance() {
        if (instance == null) throw new RuntimeException("Calib-object has not been initialized!");
        return instance;
    }

    private CalibValues(double p1p2heightDiffCm, double p1p3heightDiffCm, int p1p2tachoDist, int p1p3tachoDist, int p2p3tachoDist, int restpoint, String fileOrigin) {
        this.p1p2heightDiffCm = p1p2heightDiffCm;
        this.p1p3heightDiffCm = p1p3heightDiffCm;
        this.p1p2tachoDist = p1p2tachoDist;
        this.p1p3tachoDist = p1p3tachoDist;
        this.p2p3tachoDist = p2p3tachoDist;
        this.restpoint = restpoint;
        this.fileOrigin = fileOrigin;
    }

    public void saveCalib() {

        FileOutputStream out = null; // declare outside the try block
        File data = new File(default_calibFile);

        try {
            out = new FileOutputStream(data);
        } catch(IOException e) {
            System.err.println("Failed to create output stream");
            System.exit(1);
        }

        DataOutputStream dataOut = new DataOutputStream(out);
        try {
            dataOut.writeDouble(p1p2heightDiffCm);
            dataOut.writeDouble(p1p3heightDiffCm);
            dataOut.writeInt(p1p2tachoDist);
            dataOut.writeInt(p1p3tachoDist);
            dataOut.writeInt(p2p3tachoDist);
            dataOut.writeInt(restpoint);
            out.flush();
            out.close(); // flush the buffer and write the file
            this.fileOrigin = default_calibFile;
            Utils.println("Calibration data saved!" + toString());
        } catch (IOException e) {
            System.err.println("Failed to write to output stream");
        }
    }

    public static CalibValues createCalibInstance(double p1p2heightDiffCm, double p1p3heightDiffCm, int p1p2tachoDist, int p1p3tachoDist, int p2p3tachoDist, int restpointIndex) {
        instance = new CalibValues(p1p2heightDiffCm, p1p3heightDiffCm, p1p2tachoDist, p1p3tachoDist, p2p3tachoDist, restpointIndex, null);
        return getInstance();
    }

    public static CalibValues loadCalib() {
        File data = new File(default_calibFile);

        try {
            InputStream is = new FileInputStream(data);
            DataInputStream din = new DataInputStream(is);

            double p1p2heightDiffCm = din.readDouble();
            double p1p3heightDiffCm = din.readDouble();
            int p1p2tachoDist = din.readInt();
            int p1p3tachoDist = din.readInt();
            int p2p3tachoDist = din.readInt();
            int restpoint = din.readInt();

            instance = new CalibValues(p1p2heightDiffCm, p1p3heightDiffCm, p1p2tachoDist, p1p3tachoDist, p2p3tachoDist, restpoint, default_calibFile);

            din.close();
            Utils.println("Calibration data loaded!"+ instance.toString());
        } catch (IOException ioe) {
            Utils.println("Read Exception");
        }
        return getInstance();
    }

    public String toString() {
        String s="-------------------------------------------------------------\n";
        s=s+"Calib filename: '" + fileOrigin + "'\n";
        s=s+"p1p2heightDiffCm = " + p1p2heightDiffCm + " cm\n";
        s=s+"p1p3heightDiffCm = " + p1p3heightDiffCm + " cm\n";
        s=s+"p1p2tachoDist = " + p1p2tachoDist + " (" + Utils.tachoToCm(p1p2tachoDist) + " cm)\n";
        s=s+"p1p3tachoDist = " + p1p3tachoDist + " (" + Utils.tachoToCm(p1p3tachoDist) + " cm)\n";
        s=s+"p2p3tachoDist = " + p2p3tachoDist + " (" + Utils.tachoToCm(p2p3tachoDist) + " cm)\n";
        s=s+"restpoint = P" + (restpoint+1) + "\n";
        s=s+"-------------------------------------------------------------\n";
        return s;
    }

    public double getP1P2heightDiffCm() {
        return p1p2heightDiffCm;
    }

    public double getP1P3heightDiffCm() {
        return p1p3heightDiffCm;
    }

    public int getP1P2tachoDist() {
        return p1p2tachoDist;
    }

    public int getP1P3tachoDist() {
        return p1p3tachoDist;
    }

    public int getP2P3tachoDist() {
        return p2p3tachoDist;
    }

    public int getRestpoint() {
        return restpoint;
    }

}
