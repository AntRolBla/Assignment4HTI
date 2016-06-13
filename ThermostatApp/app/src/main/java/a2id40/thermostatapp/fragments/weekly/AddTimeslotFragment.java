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
    private String[] weekDays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    public static final String ADD_TIMESLOT_SUN_LEFT_BUNDLE = "Add timeslot sun left";

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
        setupView();
        return root;
    }

    private void setupView() {
        setupButtons();
        mWeekDayTextView.setText(String.format("Weekly Program: %s", weekDays[mDay]));
    }

    private void setupButtons(){
        mStartTimeEditText.setOnClickListener(this);
        mEndTimeEditText.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
    }

    private void openTimePicker(final boolean isDay){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        com.wdullaer.materialdatetimepicker.time.TimePickerDialog mTimePicker = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(new com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                if (isDay){
                    mStartTimeEditText.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
                } else {
                    mEndTimeEditText.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
                }
            }
        }, hour, minute, true);
        mTimePicker.setThemeDark(true);
        mTimePicker.show(getActivity().getFragmentManager(), "TimePickerDialog");
    }

    private String saveButtonClicked(){

        return "";
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_add_timeslot_save_button:
                ((BaseActivity) getActivity()).addTimeslotToWeeklyDay(new TimeslotModel(new Date(), new Date(), true));
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
