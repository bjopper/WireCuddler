package dk.bjop.wirecuddler.sensors;

/**
 * Created by bpeterse on 09-10-2014.
 */
public interface EmergencyBreakListener {

    public void switchPressed(int switchID);
    public void switchReleased(int switchID);
}
