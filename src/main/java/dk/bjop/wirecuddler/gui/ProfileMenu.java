package dk.bjop.wirecuddler.gui;

import dk.bjop.wirecuddler.CuddleController;
import dk.bjop.wirecuddler.config.CuddleProfile;
import dk.bjop.wirecuddler.math.Utils;
import dk.bjop.wirecuddler.math.WT3Coord;
import dk.bjop.wirecuddler.math.XYZCoord;
import dk.bjop.wirecuddler.motor.MotorGroup;
import dk.bjop.wirecuddler.movement.moves.MotorPathMove;
import dk.bjop.wirecuddler.movement.moves.PointMove;
import dk.bjop.wirecuddler.movement.moves.StraightAcceleratingMove;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;

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

        String heading="   PROFILE MENU   ";
        int offset = 2;
        String[] options = new String[]{"- Switch profile", "- Create profile", "- Edit profile", "- Delete profile"};

        redraw(heading, offset, options, mainSelect);
        while (true) {

            if (Button.ENTER.isDown()) {
                while (Button.ENTER.isDown()) Thread.sleep(10);
                if (mainSelect == 0) profileSwitchMenu();
                else if (mainSelect == 1) profileCreateMenu();
                else if (mainSelect == 2) profileEditMenu();
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
            LCD.drawString(options[i], 0, lineOffset + i, select == i);
        }
    }

    private void profileDeleteMenu() {
        throw new RuntimeException("Not implemented!");
    }

    private void profileEditMenu() {
        throw new RuntimeException("Not implemented!");
    }

    private void profileSwitchMenu() {
        throw new RuntimeException("Not implemented!");
    }

    private void profileCreateMenu() throws InterruptedException {

        if (!CuddleProfile.canCreateNewProfiles()) {
            LCD.clear();
            LCD.drawString("   ERROR   ", 0, 0, true);
            LCD.drawString("No room for new profile (max is 5)", 0, 2, false);
            LCD.drawString("Delete one or more profiles.", 0, 3, false);
            Button.waitForAnyPress();
            return;
        }

        XYZCoord[] torsoPoints = null;
        XYZCoord[] legPoints = null;
        XYZCoord[] armPoints = null;


        int mainSelect = 0;
        String heading = "     SET AREAS    ";
        int offset = 2;
        String[] options = new String[]{"- Torso", "- Legs", "- Arms"};

        redraw(heading, offset, options, mainSelect);
        while (true) {

            if (Button.ENTER.isDown()) {
                while (Button.ENTER.isDown()) Thread.sleep(10);
                if (mainSelect == 0) torsoPoints = profileSetTorsoPoints();
                else if (mainSelect == 1) legPoints = profileSetLegPoints();
                else if (mainSelect == 2) armPoints = profileSetArmPoints();
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

        if (CuddleProfile.validateTorsoPoints(torsoPoints)) {
            CuddleProfile cp = CuddleProfile.createInstance(torsoPoints, legPoints, armPoints);

            Utils.println("Saving new profile...");
            try {
                cp.saveProfile(CuddleProfile.getFirstAvailableFilename(), false);
                Utils.println("New profile saved!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private XYZCoord[] profileSetTorsoPoints() throws InterruptedException {
        XYZCoord[] torsoPoints = new XYZCoord[4];


        int mainSelect = 0;
        String heading = "Set torso points";
        int offset = 2;
        String[] options = new String[]{"- Top-left", "- Top-right", "- Bottom-right", "- Bottom-left"};

        redraw(heading, offset, options, mainSelect);
        while (true) {

            if (Button.ENTER.isDown()) {
                while (Button.ENTER.isDown()) Thread.sleep(10);
                selectXYZDirectionMenu();

                if (showOkCancelMessage("Store current point?", new String[]{" - ENTER = yes", " - ESCAPE = no"}, false)) {
                    torsoPoints[mainSelect] = getCurrentPosition();
                    showOkCancelMessage("Point stored!", null, false);
                    break;
                }
                else {
                    showOkCancelMessage("Point NOT stored!", null, false);
                }
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
                boolean anyNulls = false;
                for (int i=0;i<torsoPoints.length;i++) {
                    if (torsoPoints[i] == null) anyNulls = true;
                }
                if (anyNulls) {

                    // TODO allow exit on escape

                    showOkCancelMessage("ERROR", new String[]{"Not all torso-points", "have been set.!"}, true);
                }
                else {

                    // TODO save abort and show profile filename

                    return torsoPoints;
                }
            }
        }
        return torsoPoints;
    }

    private boolean showOkCancelMessage(String heading, String[] msg, boolean dobeep) {
        LCD.clear();
        LCD.drawString(heading, 0, 0, true);
        if (msg != null) {
            for (int i = 0; i < msg.length; i++) {
                if (msg[i] == null) LCD.drawString(msg[i], 0, 2 + i, true);
            }
        }
        if (dobeep) Sound.beep();

        Button.waitForAnyPress();
        if (Button.readButtons() == Button.ID_ENTER) return true;
        else return false;
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
                motorSelect = getPrevIndex(1, 3, motorSelect);
                redrawSelectDirectionMenu(motorSelect);
                while (Button.LEFT.isDown()) Thread.sleep(10);
            }
            if (Button.RIGHT.isDown()) {
                motorSelect = getNextIndex(1, 3, motorSelect);
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

    private void directionMove(int cartesianDirection) throws InterruptedException {

        // TODO ennforce boundaries!

        cc.initialize();
        Utils.println("Moving, and waiting...");
        if (!positionChanged) {
            LCD.clear();
            LCD.drawString("Positioning...",0,3);
            LCD.drawString("Please wait...",0,4);
            cc.manualMove(new PointMove(new XYZCoord(10, 10, 10)),true);
            positionChanged = true;
        }
        Utils.println("Done!");

        LCD.clear();
        redrawDirectionMove(cartesianDirection, getCurrentPosition());
        boolean redraw = true;

        while (true) {

            if (redraw) {
                redrawDirectionMove(cartesianDirection, getCurrentPosition());
                redraw = false;
            }

            if (Button.ESCAPE.isDown()) {
                while (Button.ESCAPE.isDown()) Thread.sleep(10);
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

}
