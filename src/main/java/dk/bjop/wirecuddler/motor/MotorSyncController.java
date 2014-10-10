package dk.bjop.wirecuddler.motor;

import dk.bjop.wirecuddler.util.Utils;
import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.util.LogColumn;
import lejos.util.NXTDataLogger;

import java.io.IOException;

/**
 * Created by bpeterse on 13-09-2014.
 */
public class MotorSyncController {

    static NXTDataLogger logger = new NXTDataLogger();
    static LogColumn m1Tacho1 = new LogColumn("[1] M1 tacho count", LogColumn.DT_INTEGER);
    static LogColumn function1 = new LogColumn("[1] Perfect math", LogColumn.DT_INTEGER);
    static LogColumn error1 = new LogColumn("[1] Error", LogColumn.DT_INTEGER);

    static LogColumn m1Tacho2 = new LogColumn("[2] M1 tacho count", LogColumn.DT_INTEGER);
    static LogColumn function2 = new LogColumn("[2] Perfect math", LogColumn.DT_INTEGER);
    static LogColumn error2 = new LogColumn("[2] Error", LogColumn.DT_INTEGER);

    static LogColumn[] columnDefs = new LogColumn[] { m1Tacho1, function1, error1, m1Tacho2, function2, error2 };


    MotorPathController c1;
    MotorPathController c2;
    MotorPathController c3;


    public MotorSyncController(MotorPathController c1, MotorPathController c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    public void go() {

        LCD.drawString("Waiting for ", 0, 2);
        LCD.drawString("bluetooth con to", 0, 3);
        LCD.drawString("PC to log data.", 0, 4);
        LCD.drawString("Launch NXT Chart", 0, 5);
        LCD.drawString("Logger & click", 0, 6);
        LCD.drawString("the Connect btn.", 0, 7);
        NXTConnection connection = Bluetooth.waitForConnection();
        try {
            logger.startRealtimeLog(connection);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.setColumns(columnDefs);  // must be after startRealtimeLog()

        Sound.beep();

        LCD.clear();
        LCD.drawString("Press and hold", 0, 5);
        LCD.drawString("dark gray ESCAPE", 0, 6);
        LCD.drawString("button to stop.", 0, 7);

        //TODO prototyping... Improve syncing at some point later.

        c1.start();
        c2.start();

        try {
            c1.join();
            c2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.stopLogging();
    }



    static boolean written1 = false;
    static boolean written2 = false;
    static int[] test = new int[6];
    public static synchronized void log(int id, int a, int b, int c) {
        if (id == 1) {
            if (!written1) {
                test[0] = a;
                test[1] = b;
                test[2] = c;
                written1 = true;
            }
            else {
                Utils.println("Logging out of sync! (id: "+id+")");
            }
        }
        else if (id == 2) {
            if (!written2) {
                test[3] = a;
                test[4] = b;
                test[5] = c;
                written2 = true;
            }
            else {
                Utils.println("Logging out of sync! (id: "+id+")");
            }
        }

        if (written1 && written2) {
            logger.writeLog(test[0]);
            logger.writeLog(test[1]);
            logger.writeLog(test[2]);
            logger.writeLog(test[3]);
            logger.writeLog(test[4]);
            logger.writeLog(test[5]);
            logger.finishLine();

            written1 = false;
            written2 = false;
        }
    }

}
