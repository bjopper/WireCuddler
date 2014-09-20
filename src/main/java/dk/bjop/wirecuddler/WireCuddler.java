package dk.bjop.wirecuddler;

import dk.bjop.wirecuddler.motor.*;
import dk.bjop.wirecuddler.util.Utils;
import lejos.nxt.*;
import java.io.IOException;


/**
 * Created by bpeterse on 21-06-2014.
 */
public class WireCuddler {

    public static void main(String[] args) throws InterruptedException, IOException {

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


        //MovementPath path1 = new MovementPathImpl();
        //MovementPath path1 = new MovementPathSinusImpl(10); // Perfect
        //MovementPath path1 = new MovementPathSinusImpl(24); // Perfect
        //MovementPath path1 = new MovementPathSinusImpl(60); // A little behind. Set lookahead til 1100 i stedt for 1000, og så spiller det meget bedre
        //MovementPath path1 = new MovementPathSinusImpl(24);
        //MovementPath path1 = new MovementPathSinus2Impl();


        MotorPathController pr = new MotorPathController(new NXTRegulatedMotor(MotorPort.A));
        pr.addMovementPath(new MotorPathImpl());
        pr.addMovementPath(new MotorPathSinusImpl(10));
        pr.addMovementPath(new MotorPathSinus3Impl(24));
        pr.addMovementPath(new MotorPathSinusImpl(40));
        pr.addMovementPath(new MotorPathSinus3Impl(60));
        pr.addMovementPath(new MotorPathSinus2Impl());
        pr.start();
        pr.join();

        Utils.println("Main program done...");
        LCD.clear();
        LCD.drawString("DONE!!!", 0, 3);

    }

}
