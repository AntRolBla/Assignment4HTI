package a2id40.thermostatapp.fragments.weekly.Models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IsabelGomes on 11/06/16.
 */
public class TimeslotModel {

    private String mHourTimeslot;
    private Boolean mDay;
    private Date mStarTime;
    private Date mEndTime;

    public TimeslotModel(Date mStarTime, Date mEndTime, Boolean mDay) {
        this.mStarTime = mStarTime;
        this.mEndTime = mEndTime;
        this.mDay = mDay;
    }

    public void setmStarTime(Date mStarTime) {
        this.mStarTime = mStarTime;
    }

    public void setmEndTime(Date mEndTime) {
        this.mEndTime = mEndTime;
    }

    public Date getmStarTime() {
        return mStarTime;
    }

    public Date getmEndTime() {
        return mEndTime;
    }

    public String getmHourTimeslot() {
        String mStartTimeString = new SimpleDateFormat("hh:mm").format(mStarTime);
        String mEndTimeString = new SimpleDateFormat("hh:mm").format(mEndTime);
        mHourTimeslot = mStartTimeString + " - " + mEndTimeString;
        return mHourTimeslot;
    }

    public Boolean getmDay() {
        return mDay;
    }
}
