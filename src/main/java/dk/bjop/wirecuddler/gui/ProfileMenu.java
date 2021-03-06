package dk.bjop.wirecuddler.gui;

import dk.bjop.wirecuddler.CuddleController;
import dk.bjop.wirecuddler.config.CuddleProfile;
import dk.bjop.wirecuddler.gui.utils.Messages;
import dk.bjop.wirecuddler.gui.utils.ValueSelect;
import dk.bjop.wirecuddler.math.Utils;
import dk.bjop.wirecuddler.math.coordinates.WT3Coord;
import dk.bjop.wirecuddler.math.coordinates.XYZCoord;
import dk.bjop.wirecuddler.motor.MotorGroup;
import dk.bjop.wirecuddler.movement.moves.MotorPathMove;
import dk.bjop.wirecuddler.movement.moves.PointMove;
import dk.bjop.wirecuddler.movement.moves.StraightAcceleratingMove;
import lejos.nxt.Button;
import lejos.nxt.LCD;

/**
 * Created by bpeterse on 25-04-2015.
 */
public class ProfileMenu {

    CuddleController cc;

    boolean positionChanged = false;

    public ProfileMenu(CuddleController cc){
        this.cc = cc;
    }

    public int getNextIndex(int min, int max, int cur) {
        if (cur == max-1) return min;
        else return ++cur;
    }

    public int getPrevIndex(int min, int max, int cur) {
        if (cur == min) return max-1;
        else return --cur;
    }

    /**
     * reference menu!
     *
     * @throws InterruptedException
     */
    public void startMenu() throws InterruptedException {
        positionChanged = false;


        int mainSelect = 0;

        String heading="  PROFILE MENU   ";
        int offset = 2;
        String[] options = new String[]{"Switch profile", "Create profile", "Edit profile", "Delete profile"};

        redraw(heading, offset, options, mainSelect);
        while (true) {

            if (Button.ENTER.isDown()) {
                while (Button.ENTER.isDown()) Thread.sleep(10);
                if (mainSelect == 0) profileSwitchMenu();
                else if (mainSelect == 1) {
                    if (!CuddleProfile.canCreateNewProfiles()) {
                        Messages.showMessage("  -ERROR-", new String[]{"Too many", "profiles!", "Delete some"}, true);
                        return;
                    }

                    String profileName = null;
                    try {
                        profileName = CuddleProfile.getFirstAvailableFilename();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    profileEditCreateMenu(profileName);
                }
                else if (mainSelect == 2) profileEditCreateMenu(profileSelectMenu("EDIT PROFILE"));
                else if (mainSelect == 3) profileDeleteMenu();
                else throw new RuntimeException("Invalid choice! [" + mainSelect + "]");
            }
            if (Button.LEFT.isDown()) {
                mainSelect = getPrevIndex(0, options.length, mainSelect);
                redraw(heading, offset, options, mainSelect);
                while (Button.LEFT.isDown()) Thread.sleep(10);
            }
            if (Button.RIGHT.isDown()) {
                mainSelect = getNextIndex(0, options.length, mainSelect);
                redraw(heading, offset, options, mainSelect);
                while (Button.RIGHT.isDown()) Thread.sleep(10);
            }
            if (Button.ESCAPE.isDown()) {
                while (Button.ESCAPE.isDown()) Thread.sleep(10);
                break;
            }
        }

        // TODO check we are at restpoint or move us there before exiting this menu
        if (positionChanged) cc.moveToRestpoint();
    }

    private void redraw(String heading, int lineOffset, String[] options, int select) {
        LCD.clear();
        LCD.drawString(heading, 0, 0, false);
        for (int i = 0; i < options.length; i++) {
            if (options[i] !=null && !options[i].trim().equals("")) LCD.drawString("- " + options[i], 0, lineOffset + i, select == i);
        }
    }

    private void profileDeleteMenu() throws InterruptedException {
        String profileName = profileSelectMenu("DELETE PROFILE");
        if (profileName == null) return;
        CuddleProfile.deleteProfile(profileName);
        Messages.showOkCancelMessage("", new String[]{"Profile", "deleted!"}, false);
    }

    private void profileSwitchMenu() throws InterruptedException {
        profileSelectMenu("SWITCH PROFILE");
        throw new RuntimeException("Not implemented!");
    }

    private void profileEditCreateMenu(String profileName) throws InterruptedException {
        cc.initialize();

        CuddleProfile cp = null;
        if (CuddleProfile.profileExist(profileName)) {
            cp = CuddleProfile.loadProfile(profileName);
        }
        else {
            Messages.showOkCancelMessage("NEW PROFILE NAME:", new String[]{"",profileName}, false);
        }

        XYZCoord[] torsoPoints = null;
        XYZCoord[] legPoints = null;
        XYZCoord[] armPoints = null;
        int speed = 4;

        if (cp != null) {
            torsoPoints = cp.getTorsoPoints();
            legPoints = cp.getLegPoints();
            armPoints = cp.getArmPoints();
            speed = cp.getSpeed();
        }
        else {
            torsoPoints = new XYZCoord[4];
        }

        int mainSelect = 0;
        String heading = "    SET AREAS";
        int offset = 2;
        String[] options = new String[]{"Torso", "Legs", "Arms", "Speed", "   -SAVE-"};

        redraw(heading, offset, options, mainSelect);
        while (true) {

            if (Button.ENTER.isDown()) {
                while (Button.ENTER.isDown()) Thread.sleep(10);
                if (mainSelect == 0) torsoPoints = profileSetTorsoPoints(torsoPoints);
                else if (mainSelect == 1) legPoints = profileSetLegPoints();
                else if (mainSelect == 2) armPoints = profileSetArmPoints();
                else if (mainSelect == 3) speed = profileSetSpeed(speed);
                else if (mainSelect == 4) {
                    if (CuddleProfile.validateTorsoPoints(torsoPoints)) {
                        if (Messages.showOkCancelMessage("Save all points?", null, false)) {
                            if (cp == null) {
                                new CuddleProfile(torsoPoints, legPoints, armPoints, speed).saveProfile(profileName, false);
                            }
                            else {
                                cp.setSpeed(speed);
                                cp.saveProfile(profileName, true);
                            }

                            Utils.println(CuddleProfile.loadProfile(profileName).toString());
                            Messages.showTimedMessage("Profile saved!", null, false, 2000);
                        }
                        return;
                    }
                    else {
                        if (Messages.showOkCancelMessage("Exit?", new String[]{"Exit ","without", "save?"}, false)) {
                            return;
                        }
                    }
                }
                else throw new RuntimeException("Invalid choice! [" + mainSelect + "]");
                redraw(heading, offset, options, mainSelect);
            }
            if (Button.LEFT.isDown()) {
                mainSelect = getPrevIndex(0, options.length, mainSelect);
                redraw(heading, offset, options, mainSelect);
                while (Button.LEFT.isDown()) Thread.sleep(10);
            }
            if (Button.RIGHT.isDown()) {
                mainSelect = getNextIndex(0, options.length, mainSelect);
                redraw(heading, offset, options, mainSelect);
                while (Button.RIGHT.isDown()) Thread.sleep(10);
            }
            if (Button.ESCAPE.isDown()) {
                while (Button.ESCAPE.isDown()) Thread.sleep(10);
                break;
            }
        }
    }

    private int profileSetSpeed(int curVal) throws InterruptedException{
        int speed = new ValueSelect().selectValueMenu("SET SPEED",1,10, curVal);
        Messages.showOkCancelMessage("Speed set to "+speed, null, false);
        return speed;
    }

    private XYZCoord[] profileSetTorsoPoints(XYZCoord[] torsoPoints) throws InterruptedException {

        int mainSelect = 0;
        String heading = "Set torso points";
        int offset = 2;
        String[] options = new String[]{"Top-left", "Top-right", "Bottom-right", "Bottom-left"};

        redraw(heading, offset, options, mainSelect);
        while (true) {

            if (Button.ENTER.isDown()) {
                while (Button.ENTER.isDown()) Thread.sleep(10);
                if (torsoPoints[mainSelect] != null) {
                    doPositionMoveWithInfo(new PointMove(torsoPoints[mainSelect]));
                    positionChanged = true;
                }

                selectXYZDirectionMenu();

                torsoPoints[mainSelect] = getCurrentPosition();
                Messages.showTimedMessage("", new String[]{"", "Point stored:", options[mainSelect]}, false, 2000);

                redraw(heading, offset, options, mainSelect);
            }
            if (Button.LEFT.isDown()) {
                mainSelect = getPrevIndex(0, options.length, mainSelect);
                redraw(heading, offset, options, mainSelect);
                while (Button.LEFT.isDown()) Thread.sleep(10);
            }
            if (Button.RIGHT.isDown()) {
                mainSelect = getNextIndex(0, options.length, mainSelect);
                redraw(heading, offset, options, mainSelect);
                while (Button.RIGHT.isDown()) Thread.sleep(10);
            }
            if (Button.ESCAPE.isDown()) {
                while (Button.ESCAPE.isDown()) Thread.sleep(10);

                if (!CuddleProfile.validateTorsoPoints(torsoPoints)) {

                    // TODO allow exit on escape

                    if (!Messages.showOkCancelMessage("     Exit?", new String[]{"Point missing!", "Press Esc to exit", "or Enter", "to stay."}, true)) {
                        return null;
                    }
                }
                else {

                    // TODO save abort and show profile filename
                    Messages.showOkCancelMessage("", new String[]{"", "Torso-points", "are now set!"}, false);
                    return torsoPoints;
                }
            }
        }
    }

    private void selectXYZDirectionMenu() throws InterruptedException {

        LCD.clear();
        int motorSelect = 1;
        redrawSelectDirectionMenu(motorSelect);
        while (true) {

            if (Button.ENTER.isDown()) {
                while (Button.ENTER.isDown()) Thread.sleep(10);
                directionMove(motorSelect);
                LCD.clear();
                redrawSelectDirectionMenu(motorSelect);
            }
            if (Button.ESCAPE.isDown()) {
                while (Button.ESCAPE.isDown()) Thread.sleep(10);
                break;
            }
            if (Button.LEFT.isDown()) {
                motorSelect = getPrevIndex(1, 4, motorSelect);
                redrawSelectDirectionMenu(motorSelect);
                while (Button.LEFT.isDown()) Thread.sleep(10);
            }
            if (Button.RIGHT.isDown()) {
                motorSelect = getNextIndex(1, 4, motorSelect);
                redrawSelectDirectionMenu(motorSelect);
                while (Button.RIGHT.isDown()) Thread.sleep(10);
            }
        }
    }

    private void redrawDirectionMove(int cartesianDirection, XYZCoord pos) {
        switch (cartesianDirection) {
            case 1: LCD.drawString(LFT_RGHT, 0, 0, true);break;
            case 2: LCD.drawString(FWD_BCKWD, 0, 0, true);break;
            case 3: LCD.drawString(UP_DWN, 0, 0, true);break;
        }

        if (pos != null) {
            LCD.drawString("X: " + formatNumber(pos.x), 3, 2);
            LCD.drawString("Y: " + formatNumber(pos.y), 3, 3);
            LCD.drawString("Z: " + formatNumber(pos.z), 3, 4);
        }
    }

    private final String LFT_RGHT="Left / Right";
    private final String FWD_BCKWD="Fwd / backwd";
    private final String UP_DWN="Up / Down";


    private void redrawSelectDirectionMenu(int motorSelect) {
        LCD.drawString("SELECT DIRECTION", 0, 0);
        LCD.drawString(LFT_RGHT, 2, 2, motorSelect ==1);
        LCD.drawString(FWD_BCKWD, 2, 3, motorSelect ==2);
        LCD.drawString(UP_DWN, 2, 4, motorSelect ==3);
    }

    private MotorPathMove getMove(XYZCoord curPos, int cartesianDirection, boolean fwd) {
        int adder = 1;
        adder = fwd ? -adder : adder;

        XYZCoord target = null;
        switch  (cartesianDirection) {
            case 1: target = curPos.add(new XYZCoord(adder, 0, 0));break;
            case 2: target = curPos.add(new XYZCoord(0, 0, adder));break;
            case 3: target = curPos.add(new XYZCoord(0, adder, 0));break;
        }

        Utils.println("New target:" + target.toString());

        return new StraightAcceleratingMove(target);
    }

    private XYZCoord getCurrentPosition() {
        return new WT3Coord(MotorGroup.getInstance().getTachoCounts()).toCartesian();
    }

    private double formatNumber(double n) {
        double precision = 100;
        return Math.round(n *precision) /precision;
    }

    private void doPositionMoveWithInfo(MotorPathMove move) {
        Utils.println("Moving, and waiting...");
        LCD.clear();
        LCD.drawString("Positioning...", 0, 3);
        LCD.drawString("Please wait...", 0, 4);
        cc.manualMove(move, true);
        Utils.println("Done!");
    }

    private void directionMove(int cartesianDirection) throws InterruptedException {
        if (!positionChanged) {
            Utils.println("Moving to 10,10,10");
            doPositionMoveWithInfo(new PointMove(new XYZCoord(10, 10, 10)));
            positionChanged = true;
        }

        LCD.clear();
        redrawDirectionMove(cartesianDirection, getCurrentPosition());
        boolean redraw = true;

        while (true) {

            if (redraw) {
                redrawDirectionMove(cartesianDirection, getCurrentPosition());
                redraw = false;
            }

            if (Button.ENTER.isDown()) {
                while (Button.ENTER.isDown()) Thread.sleep(10);
               // Messages.showTimedMessage("", new String[]{"", "", "Position saved!"}, false, 2000);
                break;
            }
            if (Button.ESCAPE.isDown()) {
                while (Button.ESCAPE.isDown()) Thread.sleep(10);
               // Messages.showTimedMessage("", new String[]{"", "", "Position saved!"}, false, 1000);
                break;
            }
            if (Button.LEFT.isDown()) {

                MotorPathMove m = getMove(getCurrentPosition(), cartesianDirection, true);
                cc.manualMove(m, false);

                while (Button.LEFT.isDown()) {
                    redrawDirectionMove(cartesianDirection, getCurrentPosition());
                    Thread.sleep(25);
                }
                m.setMoveTerminate();
                redraw = true;
            }
            if (Button.RIGHT.isDown()) {

                MotorPathMove m = getMove(getCurrentPosition(), cartesianDirection, false);
                cc.manualMove(m, false);

                while (Button.RIGHT.isDown()) {
                    redrawDirectionMove(cartesianDirection, getCurrentPosition());
                    Thread.sleep(25);
                }
                m.setMoveTerminate();
                redraw = true;
            }

        }
    }

    private XYZCoord[] profileSetLegPoints() {
        throw new RuntimeException("Not implemented!");
    }

    private XYZCoord[] profileSetArmPoints() {
        throw new RuntimeException("Not implemented!");
    }

    public String profileSelectMenu(String heading) throws InterruptedException {
        String[] profileNames = CuddleProfile.listExistingProfiles();
        if (profileNames.length == 0) {
            Messages.showOkCancelMessage("No profiles found!", null, true);
            return null;
        }
        int selection = 0;
        drawProfileSelectMenu(heading, selection, profileNames);
        while (true) {
            if (Button.ENTER.isDown()) {
                while (Button.ENTER.isDown()) Thread.sleep(10);
                return profileNames[selection];
            }
            if (Button.LEFT.isDown()) {
                selection = getPrevIndex(0, profileNames.length, selection);
                drawProfileSelectMenu(heading, selection, profileNames);
                while (Button.LEFT.isDown()) Thread.sleep(10);
            }
            if (Button.RIGHT.isDown()) {
                selection = getNextIndex(0, profileNames.length, selection);
                drawProfileSelectMenu(heading, selection, profileNames);
                while (Button.RIGHT.isDown()) Thread.sleep(10);
            }
            if (Button.ESCAPE.isDown()) {
                while (Button.ESCAPE.isDown()) Thread.sleep(10);
                return null;
            }
        }
    }

    private void drawProfileSelectMenu(String heading, int selectedIndex, String[] profileNames) {
        LCD.clear();
        LCD.drawString(heading, 0, 0 , false);
        if (profileNames != null) {
            for (int i = 0; i < profileNames.length; i++) {
                if (profileNames[i] != null) LCD.drawString("-" + profileNames[i], 0, 2 + i, i==selectedIndex);
            }
        }
    }

}
