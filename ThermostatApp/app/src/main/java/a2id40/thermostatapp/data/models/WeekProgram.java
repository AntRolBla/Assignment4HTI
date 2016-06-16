package a2id40.thermostatapp.data.models;

/**
 * Created by rafaelring on 6/14/16.
 */

public class WeekProgram {

    String state;

    DaysProgramModel days;

    public boolean isWeekProgramOn() {
        return state.equals("on") ? true : false;
    }

    public DaysProgramModel getDays() {
        return days;
    }

    public void setIsWeekProgramOn(boolean isWeekProgramOn) {
        this.state = isWeekProgramOn ? "on" : "off";
    }
}
