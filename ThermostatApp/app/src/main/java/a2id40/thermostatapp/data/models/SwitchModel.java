package a2id40.thermostatapp.data.models;

import java.util.Date;

/**
 * Created by rafaelring on 6/14/16.
 */

public class SwitchModel {

    String type;
    String state;
    Date time;

    public String getType() {
        return type;
    }

    public String getState() {
        return state;
    }

    public Date getTime() {
        return time;
    }
}
