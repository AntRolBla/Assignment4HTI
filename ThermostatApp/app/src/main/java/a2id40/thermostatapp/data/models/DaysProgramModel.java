package a2id40.thermostatapp.data.models;

/**
 * Created by rafaelring on 6/14/16.
 */

public class DaysProgramModel {

    MondayModel Monday;
    TuesdayModel Tuesday;
    WednesdayModel Wednesday;
    ThursdayModel Thursday;
    FridayModel Friday;
    SaturdayModel Saturday;
    SundayModel Sunday;

    public MondayModel getMonday() {
        return Monday;
    }

    public TuesdayModel getTuesday() {
        return Tuesday;
    }

    public WednesdayModel getWednesday() {
        return Wednesday;
    }

    public ThursdayModel getThursday() {
        return Thursday;
    }

    public FridayModel getFriday() {
        return Friday;
    }

    public SaturdayModel getSaturday() {
        return Saturday;
    }

    public SundayModel getSunday() {
        return Sunday;
    }
}
