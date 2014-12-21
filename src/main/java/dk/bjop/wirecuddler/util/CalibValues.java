package dk.bjop.wirecuddler.util;

import java.io.*;

/**
 * Created by bpeterse on 05-11-2014.
 */
public class CalibValues {
    public static final String calibFile = "calib.bin";

    public static double p1p2heightDiffCm = 0;
    public static double p1p3heightDiffCm = 0;

    public static int p1p2tachoDist = -1;
    public static int p1p3tachoDist = -1;
    public static int p2p3tachoDist = -1;

    public static int restpoint = -1; // motor-index MOTORPORT.A = 0, B = 1, C = 2
    public static int[] baseline = new int[2]; // motor-indices. First index is origo in a certesian coord-system. Second index is on x-axis.

    public static int cuddlePlaneHeights[] = new int[3]; // Three point that define the plane...

    public static void saveCalib() {
        FileOutputStream out = null; // declare outside the try block
        File data = new File(calibFile);

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
            dataOut.writeInt(baseline[0]);
            dataOut.writeInt(baseline[1]);
            dataOut.writeInt(cuddlePlaneHeights[0]);
            dataOut.writeInt(cuddlePlaneHeights[1]);
            dataOut.writeInt(cuddlePlaneHeights[2]);
            out.flush();
            out.close(); // flush the buffer and write the file
            Utils.println("Calibration data saved!"+calibString());
        } catch (IOException e) {
            System.err.println("Failed to write to output stream");
        }
    }

    public static void setTestdata() {
        p1p2heightDiffCm = 0;
        p1p3heightDiffCm = 0;
        p1p2tachoDist = 13114;
        p1p3tachoDist = 13428;
        p2p3tachoDist = 10537;
        restpoint = 0;
        baseline[0] = 1;
        baseline[1] = 2;
        cuddlePlaneHeights[0] = 100;
        cuddlePlaneHeights[1] = 100;
        cuddlePlaneHeights[2] = 100;
    }

    public static void loadCalib() {
        File data = new File(calibFile);


        try {
            InputStream is = new FileInputStream(data);
            DataInputStream din = new DataInputStream(is);

            p1p2heightDiffCm = din.readDouble();
            p1p3heightDiffCm = din.readDouble();
            p1p2tachoDist = din.readInt();
            p1p3tachoDist = din.readInt();
            p2p3tachoDist = din.readInt();
            restpoint = din.readInt();
            baseline[0] = din.readInt();
            baseline[1] = din.readInt();
            cuddlePlaneHeights[0] = din.readInt();
            cuddlePlaneHeights[1] = din.readInt();
            cuddlePlaneHeights[2] = din.readInt();

            din.close();
            Utils.println("Calibration data loaded!"+ calibString());
        } catch (IOException ioe) {
            System.err.println("Read Exception");
        }
    }

    public static String calibString() {
        String s="-------------------------------------------------------------\n";
        s=s+"Calib filename: '" + calibFile + "'\n";
        s=s+"p1p2heightDiffCm = " + p1p2heightDiffCm + "\n";
        s=s+"p1p3heightDiffCm = " + p1p3heightDiffCm + "\n";
        s=s+"p1p2tachoDist = " + p1p2tachoDist + "\n";
        s=s+"p1p3tachoDist = " + p1p3tachoDist + "\n";
        s=s+"p2p3tachoDist = " + p2p3tachoDist + "\n";
        s=s+"restpoint = " + restpoint + "\n";
        s=s+"baseline[0] = " + baseline[0] + "\n";
        s=s+"baseline[1] = " + baseline[1] + "\n";
        s=s+"cuddlePlaneHeights[0] = " + cuddlePlaneHeights[0] + "\n";
        s=s+"cuddlePlaneHeights[1] = " + cuddlePlaneHeights[1] + "\n";
        s=s+"cuddlePlaneHeights[2] = " + cuddlePlaneHeights[2] + "\n";
        s=s+"-------------------------------------------------------------\n";
        return s;
    }

}
