package dk.bjop.wirecuddler.config;

import dk.bjop.wirecuddler.WireCuddler;
import dk.bjop.wirecuddler.math.Utils;
import dk.bjop.wirecuddler.math.coordinates.XYZCoord;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Created by bpeterse on 24-12-2014.
 */
public class CuddleProfile {
    public static String[] allowedProfileFilenames = new String[]{"WCProfile1.cfg", "WCProfile2.cfg", "WCProfile3.cfg", "WCProfile4.cfg","WCProfile5.cfg"};

    private static final String TORSO_TOPLEFT = "torso:top-left";
    private static final String TORSO_TOPRIGHT = "torso:top-right";
    private static final String TORSO_BOTTOMRIGHT = "torso:bottom-right";
    private static final String TORSO_BOTTOMLEFT = "torso:bottom-left";

    private static final String LEGS_LEFT = "legs:left";
    private static final String LEGS_RIGHT = "legs:right";

    private static final String ARMS_LEFT = "arms:left";
    private static final String ARMS_RIGHT = "arms:right";

    private String fileOrigin = null;


    private XYZCoord[] torsoPoints = null;
    private XYZCoord[] legPoints = null;
    private XYZCoord[] armPoints = null;




    private static CuddleProfile instance = null;

    public CuddleProfile(XYZCoord[] torsoPoints, XYZCoord[] legPoints, XYZCoord[] armPoints) {
        setTorsoPoints(torsoPoints);
        setLegPoints(legPoints);
        setArmPoints(armPoints);
    }

    public static CuddleProfile createInstance(XYZCoord[] torsoPoints, XYZCoord[] legPoints, XYZCoord[] armPoints) {
        //if (instance != null) throw new RuntimeException("Cannot create new instance - an instance already exist!");
        instance = new CuddleProfile(torsoPoints, legPoints, armPoints);
        return getInstance();
    }

    private void setTorsoPoints(XYZCoord[] torsoPoints) {
        if (torsoPoints == null) throw new RuntimeException("torsoPoints is null!");
        if (torsoPoints.length != 4) throw new RuntimeException("torsoPoints length must be 4");

        for (int i = 0; i < torsoPoints.length; i++) {
            if (torsoPoints[i] == null) throw new RuntimeException("None of the torso-points can be null!");
        }
        this.torsoPoints = torsoPoints;
    }

    public void setLegPoints(XYZCoord[] legPoints) {
        if (!validateLegPoints(legPoints)) throw new RuntimeException("If leg-points array is defined none of its entries may be null");
        this.legPoints = legPoints;
    }

    public void setArmPoints(XYZCoord[] armPoints) {
        if (!validateArmPoints(armPoints)) throw new RuntimeException("If arm-points array is defined none of its entries may be null");
        this.armPoints = armPoints;
    }

    public static boolean validateTorsoPoints(XYZCoord[] tPoints) {
        if (tPoints == null) return false;
        if (tPoints.length != 4) return false;

        //TODO verify that no lines intersect i.e. that we have a valid rectangle

        for (int i = 0; i < tPoints.length; i++) {
            if (tPoints[i] == null) return false;
        }
        return true;
    }

    public static boolean validateLegPoints(XYZCoord[] lPoints) {
        if (lPoints != null) {
            if (lPoints.length != 2 || lPoints[0] == null || lPoints[1] == null) return false;
        }
        return true;
    }

    public static boolean validateArmPoints(XYZCoord[] aPoints) {
        if (aPoints != null) {
            if (aPoints.length != 2 || aPoints[0] == null || aPoints[1] == null) return false;
        }
        return true;
    }

    public XYZCoord[] getTorsoPoints() {
        return torsoPoints;
    }

    public XYZCoord[] getLegPoints() {
        return legPoints;
    }

    public XYZCoord[] getArmPoints() {
        return armPoints;
    }

    public boolean hasLegPoints() {
        return legPoints != null;
    }

    public boolean hasArmPoints() {
        return armPoints != null;
    }

    private void setFileOrigin(String filename) {
        this.fileOrigin = filename;
    }

    public String getFileOrigin() {
        return this.fileOrigin;
    }

    public static CuddleProfile getInstance() {
        if (instance == null) throw new RuntimeException("Profile-object has not been initialized!");
        return instance;
    }

    public static CuddleProfile loadProfile(String filename) {
        if (!isValidFilename(filename)) {
            throw new RuntimeException("Invalid filename: '" + filename + "'");
        }

        File data = new File(filename);
        if (!data.exists()) throw new RuntimeException("File: '" + filename + "' could not be found!");

        Utils.println("Trying to load profile data from file: '" + filename + "'");

        Properties p = new Properties();
        try {
            FileInputStream is = new FileInputStream(data);
            p.load(is);
            is.close();
            Utils.println("Profile data loaded!");
        } catch (IOException ioe) {
            Utils.println("Read Exception");
        }


        XYZCoord[] tPoints = new XYZCoord[4];
        tPoints[0] = deserializeCoordPoint(p.getProperty(TORSO_TOPLEFT));
        tPoints[1] = deserializeCoordPoint(p.getProperty(TORSO_TOPRIGHT));
        tPoints[2] = deserializeCoordPoint(p.getProperty(TORSO_BOTTOMRIGHT));
        tPoints[3] = deserializeCoordPoint(p.getProperty(TORSO_BOTTOMLEFT));

        XYZCoord[] lPoints = null;
        if (p.getProperty(LEGS_LEFT) != null && p.getProperty(LEGS_RIGHT) != null) {
            lPoints = new XYZCoord[2];
            lPoints[0] = deserializeCoordPoint(p.getProperty(LEGS_LEFT));
            lPoints[1] = deserializeCoordPoint(p.getProperty(LEGS_RIGHT));
        }

        XYZCoord[] aPoints = null;
        if (p.getProperty(ARMS_LEFT) != null && p.getProperty(ARMS_RIGHT) != null) {
            aPoints = new XYZCoord[2];
            aPoints[0] = deserializeCoordPoint(p.getProperty(ARMS_LEFT));
            aPoints[1] = deserializeCoordPoint(p.getProperty(ARMS_RIGHT));
        }


        instance = new CuddleProfile(tPoints, lPoints, aPoints);
        instance.setFileOrigin(filename);

        if (WireCuddler.devMode) Utils.println(instance.toString());

        return getInstance();
    }

    public static void deleteProfile(String filename) {
        File f = new File(filename);
        if (f.exists()) {
            Utils.println("Deleting file: '" + filename + "'");
            f.delete();
            Utils.println("Deleted!");
        }
        else {
            Utils.println("Cannot delete '" + filename + "' - file does not exist!");
        }
    }


    public void saveProfile(String filename, boolean overrideExistingFile) {

        if (!isValidFilename(filename)) {
            throw new RuntimeException("Invalid filename: '" + filename + "'");
        }

        if (!overrideExistingFile && !isFilenameAvailable(filename)) {
            throw new RuntimeException("Filename valid, but file already exists! ('" + filename + "')!");
        }

        Properties p = new Properties();
        p.setProperty(TORSO_TOPLEFT,serializeCoordPoint(torsoPoints[0]));
        p.setProperty(TORSO_TOPRIGHT,serializeCoordPoint(torsoPoints[1]));
        p.setProperty(TORSO_BOTTOMRIGHT,serializeCoordPoint(torsoPoints[2]));
        p.setProperty(TORSO_BOTTOMLEFT,serializeCoordPoint(torsoPoints[3]));

        if (legPoints != null) {
            p.setProperty(LEGS_LEFT, serializeCoordPoint(legPoints[0]));
            p.setProperty(LEGS_RIGHT, serializeCoordPoint(legPoints[1]));
        }

        if (armPoints != null) {
            p.setProperty(ARMS_LEFT, serializeCoordPoint(armPoints[0]));
            p.setProperty(ARMS_RIGHT, serializeCoordPoint(armPoints[1]));
        }


        FileOutputStream out = null;
        File data = new File(filename);
        try {
            out = new FileOutputStream(data);
            p.store(out, "CuddleProfile");
            out.close(); // flush the buffer and write the file
            this.setFileOrigin(filename);
            Utils.println("Cuddle profile data saved to file: '" + filename + "'\n" + toString());
        } catch (IOException e) {
            Utils.println(e.getMessage());
        }
    }

    private static String serializeCoordPoint(XYZCoord p) {
        return ""+p.x+"|"+p.y+"|"+p.z;
    }

    private static XYZCoord deserializeCoordPoint(String serializedP) {
        String[] tokens = splitString(serializedP, "|");
        if (tokens.length != 3) throw new RuntimeException("Bad input for deserializeCoordPoint");
        return new XYZCoord(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]));
    }

    private static String[] splitString(String s, String delim) {
        StringTokenizer strTkn = new StringTokenizer(s, delim);
        ArrayList<String> arrLis = new ArrayList<String>(s.length());
        int count = 0;
        while (strTkn.hasMoreTokens()) {
            arrLis.add(strTkn.nextToken());
            count++;
        }
        return arrLis.toArray(new String[count]);
    }


    public static String[] listExistingProfiles() {
        ArrayList<String> names = new ArrayList<String>();
        int count = 0;
        for (int i=0;i<allowedProfileFilenames.length;i++) {
            File f = new File(allowedProfileFilenames[i]);
            if (f.exists()) {
                names.add(allowedProfileFilenames[i]);
                count++;
            }
        }
        return names.toArray(new String[count]);
    }

    public static void deleteAllProfiles() {
        String[] profiles = listExistingProfiles();
        for (int i=0;i<profiles.length;i++) {
            deleteProfile(profiles[i]);
        }
    }

    public static boolean canCreateNewProfiles() {
        try {
            getFirstAvailableFilename();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static void dumpProfileFilenames() {
        Utils.println("Profiles on brick:\n");
        String[] filenames = listExistingProfiles();
        for (int i=0;i<filenames.length;i++) {
            Utils.println(" - "+filenames[i]);
        }
    }

    public static String getFirstAvailableFilename() throws Exception {
        for (int i=0;i<allowedProfileFilenames.length;i++) {
            File f = new File(allowedProfileFilenames[i]);
            if (!f.exists()) return allowedProfileFilenames[i];
        }
        throw new Exception("No available profile-filenames left!");
    }

    public boolean isFilenameAvailable(String filename) {
        return isValidFilename(filename) && !new File(filename).exists();
    }

    public static boolean isValidFilename(String filename) {
        for (int i=0;i<allowedProfileFilenames.length;i++) {
            File f = new File(allowedProfileFilenames[i]);
            if (allowedProfileFilenames[i].equalsIgnoreCase(filename)) return true;
        }
        return false;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("\n---------------- Cuddle-profile --------------------\n");
        sb.append("Filename: "+fileOrigin+"\n");
        sb.append("Torso-points:\n");
        for (int i = 0; i < torsoPoints.length; i++) {
            sb.append(" - Point "+ (i+1) + ": "+torsoPoints[i].toString()+"\n");
        }
        sb.append("\n");
        if (legPoints != null) {
            sb.append("Leg-points:\n");
            for (int i = 0; i < legPoints.length; i++) {
                sb.append(" Point "+ (i+1) + ": "+legPoints[i].toString());
            }
        }
        else {
            sb.append("No leg-points defined!\n");
        }
        sb.append("\n");
        if (armPoints != null) {
            sb.append("Arm-points:\n");
            for (int i = 0; i < armPoints.length; i++) {
                sb.append(" Point "+ (i+1) + ": "+armPoints[i].toString());
            }
        }
        else {
            sb.append("No arm-points defined!\n");
        }
        sb.append("\n-----------------------------------------------------\n");
        return sb.toString();
    }
}
