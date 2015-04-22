package dk.bjop.wirecuddler.gui;

import dk.bjop.wirecuddler.CuddleController;
import dk.bjop.wirecuddler.math.WT3Coord;
import dk.bjop.wirecuddler.math.XYZCoord;
import dk.bjop.wirecuddler.motor.MotorGroup;
import dk.bjop.wirecuddler.movement.moves.MotorPathMove;
import dk.bjop.wirecuddler.movement.moves.StraightLineMove;
import lejos.nxt.Button;
import lejos.nxt.LCD;

/**
 * Created by bpeterse on 14-04-2015.
 */
public class CuddlePointMenu {

    final int menuWaitAfterButtonPress = 500;

    CuddleController cc;



    public CuddlePointMenu(CuddleController cc) {
        this.cc = cc;
    }

    public void startMenu() throws InterruptedException{
        boolean redraw = true;
        int mainSelect = 0;

        while (true) {

            if (redraw) {
                LCD.clear();
                LCD.drawString("Move to a point", 0, 0, mainSelect==0);
                LCD.drawString("Store current point", 0, 1, mainSelect==1);
                redraw = false;
            }

            if (Button.ENTER.isDown()) {
                if (mainSelect == 0 ) selectDirectionMenu();
                if (mainSelect == 1 ) throw new RuntimeException("Not implemented!");


                redraw = true;
                Thread.sleep(menuWaitAfterButtonPress);
            }
            if (Button.LEFT.isDown()) {
                mainSelect--;
                redraw = true;
                Thread.sleep(menuWaitAfterButtonPress);
            }
            if (Button.RIGHT.isDown()) {
                mainSelect++;
                redraw = true;
                Thread.sleep(menuWaitAfterButtonPress);
            }
            if (Button.ESCAPE.isDown()) {
                break;
            }

            if (mainSelect < 0) mainSelect = 1;
            if (mainSelect > 1) mainSelect = 0;
        }

        // TODO check we are at restpoint or move us there before exiting this menu
        cc.moveToRestpoint();
    }

    private void selectDirectionMenu() throws InterruptedException {

        //OperatedMove oMove = new OperatedMove();
        //cc.doOperatedMove(oMove);
        //oMove.initialize(new WT3Coord(MotorGroup.getInstance().getTachoCounts()).toCartesian(), System.currentTimeMillis());


        LCD.clear();
        int motorSelect = 1;
        Thread.sleep(menuWaitAfterButtonPress);
        redrawSelectDirectionMenu(motorSelect);
        while (true) {

            if (Button.ENTER.isDown()) {
                directionMove(motorSelect);
                LCD.clear();
                redrawSelectDirectionMenu(motorSelect);
                Thread.sleep(menuWaitAfterButtonPress);
            }
            if (Button.ESCAPE.isDown()) {
                break;
            }
            if (Button.LEFT.isDown()) {
                motorSelect--;
                redrawSelectDirectionMenu(motorSelect);
                Thread.sleep(menuWaitAfterButtonPress);
            }
            if (Button.RIGHT.isDown()) {
                motorSelect++;
                redrawSelectDirectionMenu(motorSelect);
                Thread.sleep(menuWaitAfterButtonPress);
            }

            if (motorSelect > 3) motorSelect = 1;
            if (motorSelect < 1) motorSelect = 3;
        }

    }

    private void directionMove(int cartesianDirection) throws InterruptedException {
        cc.initialize();
        LCD.clear();
        redrawDirectionMove(cartesianDirection, getCurrentPosition());
        boolean redraw = true;

        Thread.sleep(menuWaitAfterButtonPress);
        while (true) {

            if (redraw) {
                redrawDirectionMove(cartesianDirection, getCurrentPosition());
                redraw = false;
            }

            if (Button.ESCAPE.isDown()) {
                break;
            }
            if (Button.LEFT.isDown()) {

                MotorPathMove m = getMove(getCurrentPosition(), cartesianDirection, true);
                cc.manualMove(m);

                while (Button.LEFT.isDown()) {
                    redrawDirectionMove(cartesianDirection, getCurrentPosition());
                    Thread.sleep(25);
                }
                m.setMoveTerminate();
                redraw = true;
            }
            if (Button.RIGHT.isDown()) {

                MotorPathMove m = getMove(getCurrentPosition(), cartesianDirection, false);
                cc.manualMove(m);

                while (Button.RIGHT.isDown()) {
                    redrawDirectionMove(cartesianDirection, getCurrentPosition());
                    Thread.sleep(25);
                }
                m.setMoveTerminate();
                redraw = true;
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
        LCD.drawString(LFT_RGHT, 3, 1, motorSelect ==1);
        LCD.drawString(FWD_BCKWD, 3, 2, motorSelect ==2);
        LCD.drawString(UP_DWN, 3, 3, motorSelect ==3);
    }

    private MotorPathMove getMove(XYZCoord curPos, int cartesianDirection, boolean fwd) {
        int adder = 10;
        adder = fwd ? adder : -adder;
        MotorPathMove t = null;
        switch  (cartesianDirection) {
            case 1: t = new StraightLineMove(curPos.add(new XYZCoord(adder, 0, 0)));break;
            case 2: t = new StraightLineMove(curPos.add(new XYZCoord(0, 0, adder)));break;
            case 3: t = new StraightLineMove(curPos.add(new XYZCoord(0, adder, 0)));break;
        }
        t.initialize(curPos, System.currentTimeMillis());
        t.setSpeed(2);
        return t;
    }

    private XYZCoord getCurrentPosition() {
        return new WT3Coord(MotorGroup.getInstance().getTachoCounts()).toCartesian();
    }

    private double formatNumber(double n) {
        double precision = 100;
        return Math.round(n *precision) /precision;
    }

}
