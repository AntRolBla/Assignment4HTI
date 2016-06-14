package a2id40.thermostatapp.data.models;

import java.util.ArrayList;

/**
 * Created by rafaelring on 6/14/16.
 */

public class TuesdayModel {

    ArrayList<SwitchModel> switches;

    public ArrayList<SwitchModel> getSwitches() {
        return switches;
    }

    public void setSwitches(ArrayList<SwitchModel> switches) { this.switches = switches; }
}
