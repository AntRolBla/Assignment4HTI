package a2id40.thermostatapp.fragments.weekly;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;

import a2id40.thermostatapp.R;
import butterknife.ButterKnife;

/**
 * Created by IsabelGomes on 12/06/16.
 */
public class AddTimeslotFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private int mDay;
    private String[] weekDays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

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
        View root  = inflater.inflate(R.layout.fragment_weekly_day, container, false);
        ButterKnife.bind(this, root);
        //Bundle weekDayBundle = this.getArguments();
        //mDay = weekDayBundle.getInt(WEEK_DAY_BUNDLE);
        setupView();
        return root;
    }

    private void setupView() {
        setupButtons();
        mWeekDayTextView.setText(String.format("Weekly Program: %s", weekDays[mDay]));
    }

    private void setupButtons(){
        mSaveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_add_timeslot_save_button:
                break;
        }
    }
}
