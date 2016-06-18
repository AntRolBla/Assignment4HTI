package a2id40.thermostatapp.fragments.weekly;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import java.sql.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import a2id40.thermostatapp.activities.base.BaseActivity;
import a2id40.thermostatapp.fragments.weekly.Models.TimeslotModel;
import butterknife.BindView;

import a2id40.thermostatapp.R;
import butterknife.ButterKnife;

/**
 * Created by IsabelGomes on 12/06/16.
 */
public class AddTimeslotFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private int mDay;
    private Timepoint[] mTimepointsInitial;
    private Timepoint[] mTimepointsEnd;
    private String[] weekDays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    private Date mStartTimeDate;
    private Date mEndTimeDate;
    private int mStartHour = 0;
    private int mStartMinute = 0;

    public static final String ADD_TIMESLOT_SUN_LEFT_BUNDLE = "Add timeslot sun left";
    public static final String ADD_TIMESLOT_TIMEPOINTS_INITIAL_BUNDLE = "Add timepoints initial array";
    public static final String ADD_TIMESLOT_TIMEPOINTS_END_BUNDLE = "Add timepoints end array";

    //region View Component
    @BindView(R.id.fragment_add_timeslot_week_day_textview)
    TextView mWeekDayTextView;

    @BindView(R.id.fragment_add_timeslot_start_time_edittext)
    EditText mStartTimeEditText;

    @BindView(R.id.fragment_add_timeslot_end_time_edittext)
    EditText mEndTimeEditText;

    @BindView(R.id.fragment_add_timeslot_sun_left_textview)
    TextView mSunLeftTextView;

    @BindView(R.id.fragment_add_timeslot_save_button)
    Button mSaveButton;
    //endregion

    public static AddTimeslotFragment newInstance() {return new AddTimeslotFragment();}

    public AddTimeslotFragment(){};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root  = inflater.inflate(R.layout.fragment_add_timeslot, container, false);
        ButterKnife.bind(this, root);
        Bundle addTimeslotBundle = this.getArguments();
        mDay = addTimeslotBundle.getInt(WeeklyDayFragment.WEEK_DAY_BUNDLE);
        mSunLeftTextView.setText(String.format("%dx", addTimeslotBundle.getInt(ADD_TIMESLOT_SUN_LEFT_BUNDLE)));
        mTimepointsEnd = (Timepoint[]) addTimeslotBundle.getParcelableArray(ADD_TIMESLOT_TIMEPOINTS_END_BUNDLE);
        mTimepointsInitial = (Timepoint[]) addTimeslotBundle.getParcelableArray(ADD_TIMESLOT_TIMEPOINTS_INITIAL_BUNDLE);
        setupView();
        return root;
    }

    private void setupView() {
        setupButtons();
        mEndTimeEditText.setEnabled(false);
        mSaveButton.setEnabled(false);
        mWeekDayTextView.setText(String.format("Weekly Program: %s", weekDays[mDay]));
    }

    private void setupButtons(){
        mStartTimeEditText.setOnClickListener(this);
        mEndTimeEditText.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
    }

    private void openTimePicker(final boolean isStart){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        com.wdullaer.materialdatetimepicker.time.TimePickerDialog mTimePicker = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(new com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                if (isStart){
                    mStartTimeEditText.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
                    startAndEndTimeToDate(true, hourOfDay, minute);
                    mStartHour = hourOfDay;
                    mStartMinute = minute;
                    enableEndTime();
                } else {
                    mEndTimeEditText.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
                    startAndEndTimeToDate(false, hourOfDay, minute);
                    enableSaveButton();
                }
            }
        }, hour, minute, true);
        mTimePicker.setThemeDark(true);
        if (!isStart){
            mTimePicker.setMinTime(new Timepoint(mStartHour, mStartMinute+1));
            mTimePicker.setSelectableTimes(getTimepointArrayAfterChosingStart(mTimepointsEnd));
        } else {
            mTimePicker.setSelectableTimes(mTimepointsInitial);
        }
        mTimePicker.show(getActivity().getFragmentManager(), "TimePickerDialog");
    }

    private void enableEndTime(){
        mEndTimeEditText.setEnabled(true);
    }

    private void enableSaveButton(){ mSaveButton.setEnabled(true); }

    private Timepoint[] getTimepointArrayAfterChosingStart(Timepoint[] timepointArray){
        ArrayList<Timepoint> initialArrayList = new ArrayList<Timepoint>(Arrays.asList(timepointArray));
        int position = 0;
        int internal = 0;
        int size = initialArrayList.size();

        for (int i = 0; i < initialArrayList.size(); i++){
            if (initialArrayList.get(i).getHour() == mStartHour && initialArrayList.get(i).getMinute() == mStartMinute){
                position = i;
            }
        }

        Timepoint control = new Timepoint(initialArrayList.get(position).getHour(), initialArrayList.get(position).getMinute());
        control = addOneMinuteToTimepoint(control);
        internal = position+1;

        for (int i = position+1; i < size; i++){
            if (control.getHour() == initialArrayList.get(internal).getHour() || control.getMinute() == initialArrayList.get(internal).getMinute()){
                internal++;
            } else {
                initialArrayList.remove(internal);
            }
            control = addOneMinuteToTimepoint(control);
        }

        Timepoint[] newTimepointArray = new Timepoint[initialArrayList.size()];
        newTimepointArray = initialArrayList.toArray(newTimepointArray);

        return newTimepointArray;
    }

    private Timepoint addOneMinuteToTimepoint(Timepoint initialTimepoint){
        Timepoint newTimepoint;
        if (initialTimepoint.getMinute() != 59){ //If it is the middle of the hour
            newTimepoint = new Timepoint(initialTimepoint.getHour(), initialTimepoint.getMinute()+1);
        } else {
            newTimepoint = new Timepoint(initialTimepoint.getHour()+1, 0);
        }
        return newTimepoint;
    }



    private void startAndEndTimeToDate(boolean isStart, int hour, int minutes){
        Calendar temp = Calendar.getInstance();
        temp.set(Calendar.HOUR_OF_DAY, hour);
        temp.set(Calendar.MINUTE, minutes);

        if (isStart){
            mStartTimeDate = temp.getTime();
        } else {
            mEndTimeDate = temp.getTime();
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_add_timeslot_save_button:
                ((BaseActivity) getActivity()).addTimeslotToWeeklyDay(new TimeslotModel(mStartTimeDate, mEndTimeDate, true));
                break;
            case R.id.fragment_add_timeslot_start_time_edittext:
                openTimePicker(true);
                break;
            case R.id.fragment_add_timeslot_end_time_edittext:
                openTimePicker(false);
                break;
        }
    }
}
