package dk.bjop.wirecuddler;

/**
 * Created by bpeterse on 09-10-2014.
 */
public interface SwitchListener {

    public void switchPressed(int switchID);
    public void switchReleased(int switchID);
}