package dk.bjop.wirecuddler.gui;

import dk.bjop.wirecuddler.CuddleController;
import dk.bjop.wirecuddler.math.WT3Coord;
import dk.bjop.wirecuddler.math.XYZCoord;
import dk.bjop.wirecuddler.motor.MotorGroup;
import dk.bjop.wirecuddler.movement.moves.OperatedMove;
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

        OperatedMove oMove = new OperatedMove();
        cc.doOperatedMove(oMove);
        oMove.initialize(new WT3Coord(MotorGroup.getInstance().getTachoCounts()).toCartesian(), System.currentTimeMillis());


        LCD.clear();
        int motorSelect = 1;
        Thread.sleep(menuWaitAfterButtonPress);
        redrawSelectDirectionMenu(motorSelect);
        while (true) {

            if (Button.ENTER.isDown()) {
                switch (motorSelect) {
                    case 1:
                    case 2:
                    case 3: directionMove(getDirection(motorSelect), oMove);break;
                }
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


        oMove.setMoveTerminate();
    }


    private void redrawSelectDirectionMenu(int motorSelect) {
        LCD.drawString("SELECT DIRECTION", 0, 0);
        LCD.drawString("Left / Right", 3, 1, motorSelect ==1);
        LCD.drawString("Fwd / backwd", 3, 2, motorSelect ==2);
        LCD.drawString("Up / Down", 3, 3, motorSelect ==3);
    }

    private OperatedMove.CoordDirection getDirection(int motorIndex) {
        switch (motorIndex) {
            case 1: return OperatedMove.CoordDirection.X;
            case 2: return OperatedMove.CoordDirection.Z;
            case 3: return OperatedMove.CoordDirection.Y;
        }
        return OperatedMove.CoordDirection.NONE;
    }


    private void directionMove(OperatedMove.CoordDirection cod, OperatedMove oMove) throws InterruptedException {
        LCD.clear();
        redrawDirectionMove(cod, oMove.getCurrentPosition());
        boolean redraw = true;

        Thread.sleep(menuWaitAfterButtonPress);
        while (true) {

            if (redraw) {
                redrawDirectionMove(cod, oMove.getCurrentPosition());
                redraw = false;
            }

            if (Button.ESCAPE.isDown()) {
                break;
            }
            if (Button.LEFT.isDown()) {
                oMove.setMoveOn(OperatedMove.MotorDirection.NEGATIVE, cod);
                while (Button.LEFT.isDown()) {
                    redrawDirectionMove(cod, oMove.getCurrentPosition());
                    Thread.sleep(50);
                }
                oMove.stopMovement();
                redraw = true;
            }
            if (Button.RIGHT.isDown()) {
                oMove.setMoveOn(OperatedMove.MotorDirection.POSITIVE, cod);
                while (Button.RIGHT.isDown()) {
                    redrawDirectionMove(cod, oMove.getCurrentPosition());
                    Thread.sleep(50);
                }
                oMove.stopMovement();
                redraw = true;
            }

        }

        oMove.stopMovement();
    }

    private void redrawDirectionMove(OperatedMove.CoordDirection dir, XYZCoord pos) {

        switch (dir) {
            case X: LCD.drawString("LEFT / RIGHT", 0, 0, true);
            case Z: LCD.drawString("FWD / BACKWD", 0, 0, true);
            case Y: LCD.drawString("UP / DOWN", 0, 0, true);
        }

        if (pos != null) {
            LCD.drawString("X: " + formatNumber(pos.x), 3, 2);
            LCD.drawString("Y: " + formatNumber(pos.y), 3, 3);
            LCD.drawString("Z: " + formatNumber(pos.z), 3, 4);
        }
    }

    private double formatNumber(double n) {
        double precision = 100;
        return Math.round(n *precision) /precision;
    }

}
