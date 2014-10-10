package dk.bjop.wirecuddler;

import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by bpeterse on 09-10-2014.
 */
public class Switch extends Thread {
    int switchId;
    TouchSensor ts;
    ArrayList<SwitchListener> listeners = new ArrayList<SwitchListener>();

    public Switch(int swithId, SensorPort s) {
        this.switchId = swithId;
        this.ts = new TouchSensor(s);
    }

    public int getID() {
        return switchId;
    }

    public void addListener(SwitchListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SwitchListener listener) {
            listeners.remove(listener);
    }

    public void run() {
        boolean pressed = false;


        while (true) {

            if (ts.isPressed()) {
                if (!pressed) {
                    notifyListenersPressed();
                }
                pressed = true;
            }
            else {
               if (pressed) {
                   notifyListenersReleased();
               }
               pressed = false;
            }

            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyListenersPressed() {
        Iterator<SwitchListener> iter = listeners.listIterator();
        while (iter.hasNext()) {
            iter.next().switchPressed(switchId);
        }
    }

    private void notifyListenersReleased() {
        Iterator<SwitchListener> iter = listeners.listIterator();
        while (iter.hasNext()) {
            iter.next().switchReleased(switchId);
        }
    }
}
