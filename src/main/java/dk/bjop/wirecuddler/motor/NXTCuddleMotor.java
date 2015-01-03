package dk.bjop.wirecuddler.motor;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;

/**
 * Created by bpeterse on 05-11-2014.
 */
public class NXTCuddleMotor extends NXTRegulatedMotor {

    boolean directionForward = true;
    int tachoOffset = 0;
    boolean enabled = true;

    public NXTCuddleMotor(MotorPort port) {
        super(port);
    }

    public void setDirectionForward(boolean directionForward) {
        this.directionForward = directionForward;
    }

    public boolean getDirectionForward() {
        return directionForward;
    }

    @Override
    public void forward() {
        if (directionForward) super.forward();
        else super.backward();
    }

    @Override
    public void backward() {
        if (directionForward) super.backward();
        else super.forward();
    }

    @Override
    public void rotate(int angle) {
        this.rotate(angle, false);
    }

    @Override
    public void rotate(int angle, boolean immediateReturn) {
        super.rotate(directionForward ? angle : angle*-1, immediateReturn);
    }

    @Override
    public void rotateTo(int limitAngle) {
        this.rotateTo(limitAngle, false);
    }

    @Override
    public void rotateTo(int limitAngle, boolean immediateReturn) {
        super.rotateTo(directionForward ? limitAngle : limitAngle*-1, immediateReturn);
    }

    @Override
    public int getTachoCount() {
        return super.getTachoCount() + tachoOffset;
    }

    public int getTachoOffset() {
        return tachoOffset;
    }

    public void setTachoOffset(int offset) {
        this.tachoOffset = offset;
    }

    public void setEnabled(boolean flag) {
        this.enabled = flag;
    }

    public boolean isEnabled() {
        return enabled;
    }

}
