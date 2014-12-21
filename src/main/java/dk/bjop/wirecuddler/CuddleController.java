package dk.bjop.wirecuddler;

import dk.bjop.wirecuddler.motor.MotorGroup;
import dk.bjop.wirecuddler.motor.RestPoint;
import dk.bjop.wirecuddler.util.CalibValues;
import dk.bjop.wirecuddler.util.Utils;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;

/**
 * Created by bpeterse on 26-11-2014.
 */
public class CuddleController {
    private MotorGroup mg = new MotorGroup();
    private TouchSensor ts1 = new TouchSensor(SensorPort.S1);
    private RestPoint rp = new RestPoint(ts1);

    public CuddleController() {
        // verify calib-values validity
    }

    public void moveToRestpoint() {
        rp.moveToRestPoint(mg);
    }

    public void doCuddle() {
        moveToRestpoint();

        // Set tacho-offsets as they apply when on the restpoint
        int offset = 300;
        mg.setTachoCountOffsets(offset, CalibValues.p1p2tachoDist , CalibValues.p1p3tachoDist);


        inferPosition2D_m1m3();





    }

    private void xx() {

    }

    private void inferPosition2D_m1m3() {
        int[] tachos = mg.getTachoCounts();
        int m1t = tachos[0];
        int m3t = tachos[2];

        double m1m3Dist = Utils.tachoToCm(CalibValues.p1p3tachoDist);  // b
        double m1wireLength = Utils.tachoToCm(m1t);  // c
        double m3WireLength = Utils.tachoToCm(m3t);  // a

        // Cosine relation... (Ref: http://da.wikipedia.org/wiki/Cosinusrelation) + http://www.studieportalen.dk/kompendier/matematik/formelsamling/trigonometri/begreber/hoejde-grundlinje
        double angleAtM1 = Math.acos( (m1m3Dist*m1m3Dist + m1wireLength*m1wireLength - m3WireLength*m3WireLength) / (2*m1m3Dist*m1wireLength) );  // A

        // We now consider the rightangled triangle... (sine-relation here)
        double triangleHeight = m1wireLength * Math.sin(angleAtM1); // d or hb (height from b)
        double bb = m1wireLength * Math.sin(90 - angleAtM1);

        Utils.println("2D: X-pos of point is: "+ bb + "cm");
        Utils.println("2D: Height at X-pos: " + triangleHeight + "cm");

    }

    public MotorGroup getMotorgroup() {
        return mg;
    }

    public TouchSensor getTouchSensor() {
        return ts1;
    }
}
