package dk.bjop.wirecuddler;

import dk.bjop.wirecuddler.calibration.RestPoint;
import dk.bjop.wirecuddler.config.CalibValues;
import dk.bjop.wirecuddler.math.*;
import dk.bjop.wirecuddler.motor.MotorGroup;
import dk.bjop.wirecuddler.movement.CuddleMoveController;
import dk.bjop.wirecuddler.movement.CuddleMoveProducer;
import dk.bjop.wirecuddler.movement.CuddleMoveProducerByList;
import dk.bjop.wirecuddler.movement.moves.MotorPathMove;
import dk.bjop.wirecuddler.movement.moves.StraightToPointMove;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;

import java.util.ArrayList;

/**
 * Created by bpeterse on 26-11-2014.
 */
public class CuddleController {
    private MotorGroup mg = null;
    private TouchSensor ts1 = new TouchSensor(SensorPort.S1);
    private RestPoint rp = new RestPoint(ts1);


    CuddleMoveController cmc;



    public CuddleController() {
        // verify calib-values validity
        mg = MotorGroup.getInstance();
    }

    public void moveToRestpoint() {
        rp.moveToRestPoint(mg);
    }


    public void doCuddle()throws InterruptedException {

        int height = 70;

        cmc = new CuddleMoveController(mg);

        ArrayList<MotorPathMove> moves = new ArrayList<MotorPathMove>();
        moves.add(0, new StraightToPointMove(new XYZCoord(120,height,70)));
        moves.add(1, new StraightToPointMove(new XYZCoord(160,height,70)));
        moves.add(2, new StraightToPointMove(new XYZCoord(40,height,30)));
        moves.add(3, new StraightToPointMove(new WT3Coord(mg.getTachoCounts()).toCartesian()));

        CuddleMoveProducer cmp = new CuddleMoveProducerByList(moves);
        //CuddleMoveProducer cmp2 = new CuddleMoveProducerGenerateRandomInRectangle();

        cmc.setMoveProducer(cmp);

        cmc.start();

        //doMathtests();
        //doMoveTest();
        // Uncomment these two lines to use automatic move to restpoint
        //Triangle tri = Triangle.getInstance();
        //mg.setTachoCountOffsets(720, tri.getCalibValues().getP1P2tachoDist(), tri.getCalibValues().getP1P3tachoDist());
    }

    private void doMoveTest() throws InterruptedException {
        // Uncomment these two lines to avoid automatic move to restpoint
        Triangle tri = Triangle.getInstance();

        //TODO fix these hardcoded settings...
        mg.setTachoCountOffsets(180, tri.getCalibValues().getP1P2tachoDist(), tri.getCalibValues().getP1P3tachoDist()); // Will set position flag to known!


        if (!mg.positionKnown()) {
            RestPoint rp = new RestPoint(ts1);
            rp.moveToRestPoint(mg);
        }

        WT3Coord c = new WT3Coord(mg.getTachoCounts());
        Utils.println(c.toString());
        XYZCoord curPos = c.toCartesian();
        Utils.println(curPos.toString());

        //doGenerateMovesTest();

        //System.exit(0);

        Utils.println("Current position:\n" + curPos.toString());





        /*double height = 143;
        XYZCoord targetPos = new XYZCoord(120,height,70);
        XYZCoord tPos = new XYZCoord(160,height,70);
        XYZCoord tPos2 = new XYZCoord(150,height,50);
        XYZCoord tPos3 = new XYZCoord(40,height,30);

        Utils.println("Target pos: " + targetPos.toString());
        XYZCoord currentPos =  new WT3Coord(mg.getTachoCounts()).toCartesian();
        Utils.println("Current pos: " + currentPos.toString());

        cmc.queueMove(new StraightToPointMove(targetPos));
        cmc.queueMove(new StraightToPointMove(tPos));
        cmc.queueMove(new StraightToPointMove(tPos2));
        cmc.queueMove(new StraightToPointMove(tPos3));
        cmc.queueMove(new StraightToPointMove(currentPos));*/
    }

/*    private Collection<StraightToPointMove> doGenerateMovesTest() {

        Collection<StraightToPointMove> moves = new ArrayList<StraightToPointMove>();

        double height = 100;


        XYZCoord initPos = new XYZCoord(70,height,70);
        moves.add(new StraightToPointMove(initPos));

        int noOfMovesToGenerate = 25;

        int maxX = 130;
        int minX = 70;
        int maxZ = 60;
        int minZ = 10;


        int x = 0 ;
        int z = 0;
        int c;
        // rnd 1-4    random.nextInt(max - min + 1) + min
        Random rnd = new Random(System.currentTimeMillis()+13);

        int ones = 0;
        int twos = 0;
        int threes = 0;
        int fours = 0;

        for (int i = 0;i<noOfMovesToGenerate;i++) {

            c = (rnd.nextInt(40)+10) / 10; // Weird...  rnd.nextInt(4)+1;  only output sequence 1 2 2 1 2 2 1 2 2 1 2 2 1 for ever???

            if (c == 1) {
                x = minX;
                z = rnd.nextInt(maxZ - minZ + 1) + minZ;
                ones++;
            }
            else if (c == 2) {
                x = maxX;
                z = rnd.nextInt(maxZ - minZ + 1) + minZ;
                twos++;
            }
            else if (c == 3) {
                z = minZ;
                x = rnd.nextInt(maxX - minX + 1) + minX;
                threes++;
            }
            else if (c == 4) {
                z = maxZ;
                x = rnd.nextInt(maxX - minX + 1) + minX;
                fours++;
            }

            XYZCoord m = new XYZCoord(x, height, z);
            moves.add(new StraightToPointMove(m));
        }

        Utils.println("1: "+ ones +"   2: "+twos + "    3: "+threes + "     4: "+fours + "   sum: "+ (ones+twos+threes+fours));

        XYZCoord curPos = new WT3Coord(mg.getTachoCounts()).toCartesian();
        moves.add(new StraightToPointMove(curPos));

        return moves;
    }*/

    private void doMathtests() {
        CalibValues cv = Triangle.getInstance().getCalibValues();


        WT3Coord wtc;
        XYZCoord pos;
        SphericCoord spc;
        int smallDist = 1000;

        Utils.println("-------------------------- Test: equal length wires");
        wtc = new WT3Coord(new int[]{10000, 10000 , 10000});
        Utils.println(wtc.toString());
        pos = wtc.toCartesian();
        Utils.println(pos.toString());
        wtc = pos.toWiresTachoCoord();
        Utils.println(wtc.toString());
        spc = pos.toSpheric();
        Utils.println(spc.toString());
        pos = spc.toCartesian();
        Utils.println(pos.toString());

        Utils.println("-------------------------- Test: Close to P2");
        wtc = new WT3Coord(new int[]{cv.getP1P2tachoDist(), smallDist , cv.getP2P3tachoDist()}); // Close to P2
        Utils.println(wtc.toString());
        pos = wtc.toCartesian();
        Utils.println(pos.toString());
        wtc = pos.toWiresTachoCoord();
        Utils.println(wtc.toString());
        spc = pos.toSpheric();
        Utils.println(spc.toString());
        pos = spc.toCartesian();
        Utils.println(pos.toString());

        Utils.println("-------------------------- Test: Close to P1");
        wtc = new WT3Coord(new int[]{smallDist, cv.getP1P2tachoDist() , cv.getP1P3tachoDist()}); // Close to P1
        Utils.println(wtc.toString());
        pos = wtc.toCartesian();
        Utils.println(pos.toString());
        wtc = pos.toWiresTachoCoord();
        Utils.println(wtc.toString());
        spc = pos.toSpheric();
        Utils.println(spc.toString());
        pos = spc.toCartesian();
        Utils.println(pos.toString());

        Utils.println("-------------------------- Test: Close to P3");
        wtc = new WT3Coord(new int[]{cv.getP1P3tachoDist(), cv.getP2P3tachoDist() , smallDist}); // Close to P3
        Utils.println(wtc.toString());
        pos = wtc.toCartesian();
        Utils.println(pos.toString());
        wtc = pos.toWiresTachoCoord();
        Utils.println(wtc.toString());
        spc = pos.toSpheric();
        Utils.println(spc.toString());
        pos = spc.toCartesian();
        Utils.println(pos.toString());


        Utils.println("------------ SPHERIC TESTS--------------");


        XYZCoord targetPos = new XYZCoord(5, 5, 5);
        XYZCoord startPos = new XYZCoord(120, 150, 70);

        Utils.println("startPos: "+startPos.toString());
        Utils.println("targetPos: "+targetPos.toString());

        XYZCoord g1 = targetPos.subtract(startPos);
        SphericCoord sp = g1.toSpheric();
        Utils.println("targetPos minus startPos: "+g1.toString() + " (Spheric: " + sp.toString()+")");

        double newR = startPos.distanceTo(targetPos) / 2;
        sp.r = newR;
        Utils.println("New radius: " + newR);
        g1 = sp.toCartesian();
        Utils.println("new XYZ after radius change: " + g1.toString());
        XYZCoord finalPos = g1.add(startPos);
        Utils.println("Final point after adding startPos: " + finalPos);


        Utils.println("--------------------- DONE -----------------------------");
    }

    private void xx() {

    }


    /*public MotorGroup getMotorgroup() {
        return mg;
    }*/

    public TouchSensor getTouchSensor() {
        return ts1;
    }
}
