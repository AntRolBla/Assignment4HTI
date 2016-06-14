package a2id40.thermostatapp.data.models;

/**
 * Created by rafaelring on 6/14/16.
 */

public class WeekProgramState {

    boolean week_program_state;

    public WeekProgramState(boolean isWeekProgramOn) {
        this.week_program_state = isWeekProgramOn;
    }

    public boolean isWeekProgramOn() {
        return week_program_state;
    }

    public void setIsWeekProgramOn(boolean isWeekProgramOn) {
        this.week_program_state = isWeekProgramOn;
    }
}
