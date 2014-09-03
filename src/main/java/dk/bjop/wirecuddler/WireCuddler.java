package dk.bjop.wirecuddler;

import lejos.nxt.*;
import lejos.nxt.Button;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RConsole;
import lejos.util.Delay;
import lejos.util.LogColumn;
import lejos.util.NXTDataLogger;

import java.awt.*;
import java.io.IOException;

/**
 * Created by bpeterse on 21-06-2014.
 */
public class WireCuddler {
    static NXTDataLogger logger = new NXTDataLogger();
    static LogColumn m1Tacho = new LogColumn("M1 tacho count", LogColumn.DT_INTEGER);
    static LogColumn function = new LogColumn("Perfect math", LogColumn.DT_INTEGER);
    static LogColumn[] columnDefs = new LogColumn[] { m1Tacho, function };

    public static void main(String[] args) throws InterruptedException, IOException {
        LCD.drawString("Waiting for ", 0, 2);
        LCD.drawString("bluetooth con to", 0, 3);
        LCD.drawString("PC to log data.", 0, 4);
        LCD.drawString("Launch NXT Chart", 0, 5);
        LCD.drawString("Logger & click", 0, 6);
        LCD.drawString("the Connect btn.", 0, 7);

        // From a command line:
        // [leJOS Install Location]\bin\nxjchartinglogger.bat
        // Enter the NXT's name or address.  Be sure you have write permission
        // to the folder chosen by the utility.  If not, change the folder.
        // Click the Connect button

        // 20 motor revs = 34cm snor
        // 3:1 gearing
        // Omkreds af rulle = 34cm/(20 revs/3) = 51mm
        //  51mm snor-længde ændring = +- 1080 grader på motoraksel
        // Hastighed på 5 cm giver altså ca. 1080 grader pr sek.

        NXTConnection connection = Bluetooth.waitForConnection();
        logger.startRealtimeLog(connection);
        logger.setColumns(columnDefs);  // must be after startRealtimeLog()

        LCD.clear();
        Sound.beep();
        LCD.drawString("Press orange btn", 0, 4);
        LCD.drawString("to start.", 0, 5);
        Button.ENTER.waitForPressAndRelease();
        LCD.clear();

        LCD.drawString("Press and hold", 0, 5);
        LCD.drawString("dark gray ESCAPE", 0, 6);
        LCD.drawString("button to stop.", 0, 7);

        int startCount = 42;


//        RConsole.openUSB(5000);  //.open();
//        RConsole.println("Start RConsole test ");




        NXTRegulatedMotor m1 = new NXTRegulatedMotor(MotorPort.A);
        m1.setAcceleration(0);
        m1.setSpeed(0);
        m1.rotate(360*100,true);
        boolean speedIsSet = false;
        int acc = 0;
        long startTime = System.currentTimeMillis();
        float speed= 5f;
        while (!Button.ESCAPE.isDown()) {
            long loopTimeStart = System.currentTimeMillis();
            //m1.setAcceleration(acc);

            if (!speedIsSet) {
                m1.setSpeed(900);
                m1.rotate(360*100,true);
                //m1.forward();
                speedIsSet=true;
            }

            int m1Acc = m1.getAcceleration();
            int nextPos = getExpectedTachoPosAtTimeT((System.currentTimeMillis()-startTime) + 1000,speed);
            int currPos = m1.getTachoCount();
            int diff = nextPos-currPos;
            m1.setAcceleration(diff-m1Acc);
            /*if (currPos < nextPos) m1.setAcceleration(diff-m1Acc);
            else m1.setAcceleration(m1.getAcceleration()-100);*/

            /*if (m1.getTachoCount() > 360*50) {
                m1.setAcceleration(5);
                m1.setSpeed(10);
            }
            else {
                acc += 10;
            }*/

            logger.writeLog(m1.getTachoCount());
            logger.writeLog(getExpectedTachoPosAtTimeT(System.currentTimeMillis()-startTime,speed));
            logger.finishLine();
            startCount++;
            Delay.msDelay(1000 - (System.currentTimeMillis()-loopTimeStart));
        }

        logger.stopLogging();


//        RConsole.println("\n Stop RConsole test. ");
//        RConsole.close();cyg
//        Button.waitForAnyPress();

        int x = 100/0; // test remote exception dumps
    }

    public static int getExpectedTachoPosAtTimeT(long elapsedTimeMillis, float speedCmSec) {
        // a = vandret sidelængde top
        // b = lodret sidelængde nedad (konstant)
        // c = hypotenusen

        float a = (elapsedTimeMillis / 1000f) * speedCmSec;
        float b = 50;
        float c = (float) Math.sqrt(Math.pow(a,2) + Math.pow(b, 2));
        return cmToTacho(c-b); // we subtract b as we are only interested in the diff
    }

    public static int cmToTacho(float lengthCm) {
        double barrelRevs = lengthCm / GPS.wireBarrelCircumference;
        double motorRevs = barrelRevs * GPS.gearing;
        int tachoCount = (int) (motorRevs * 360f);
        return tachoCount;
    }
}
