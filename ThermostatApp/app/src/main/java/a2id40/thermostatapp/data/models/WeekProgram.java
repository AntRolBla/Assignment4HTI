package a2id40.thermostatapp.data.models;

/**
 * Created by rafaelring on 6/14/16.
 */

public class WeekProgram {

    boolean state;

    DaysProgramModel days;

    public boolean isWeekProgramOn() {
        return state;
    }

    public DaysProgramModel getDays() {
        return days;
    }
}
