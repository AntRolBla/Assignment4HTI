package a2id40.thermostatapp.fragments.weekly.Models;

/**
 * Created by IsabelGomes on 11/06/16.
 */
public class TimeslotModel {

    private String mHourTimeslot;
    private Boolean mDay;

    public TimeslotModel(String mHourTimeslot, Boolean mDay) {
        this.mHourTimeslot = mHourTimeslot;
        this.mDay = mDay;
    }

    public String getmHourTimeslot() {
        return mHourTimeslot;
    }

    public Boolean getmDay() {
        return mDay;
    }
}
