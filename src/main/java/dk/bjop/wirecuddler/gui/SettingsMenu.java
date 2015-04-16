package dk.bjop.wirecuddler.gui;

import dk.bjop.wirecuddler.CuddleController;

/**
 * Created by bpeterse on 28-12-2014.
 */
public class SettingsMenu {

    private CuddleController cc;

    public SettingsMenu(CuddleController cc) {
        this.cc = cc;
    }


    public void startmenu() {
        /*while (true) {

        }*/

        // Reposition before we leave this menu
        cc.moveToRestpoint();
    }
}
