package a2id40.thermostatapp.data.models;

import java.util.Date;

/**
 * Created by rafaelring on 6/14/16.
 */

public class SwitchModel {

    String type;
    String state;
    Date time;

    public SwitchModel(boolean isNight, boolean isOn, Date time) {
        this.type = isNight ? "night" : "day";
        this.state = isOn ? "on" : "off";
        this.time = time;
    }

    public boolean isNight() {
        return type.equals("night");
    }

    public boolean isOn() {
        return state.equals("on");
    }

    public Date getTime() {
        return time;
    }

    public void setIsNight(boolean isNight) {
        this.type = isNight ? "night" : "day";
    }

    public void setIsOn(boolean isOn) {
        this.state = isOn ? "on" : "off";
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
