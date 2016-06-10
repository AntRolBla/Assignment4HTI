package a2id40.thermostatapp.fragments.weekly;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import a2id40.thermostatapp.R;
import a2id40.thermostatapp.fragments.vacation.VacationFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rafaelring on 6/9/16.
 */

public class WeeklyDayFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private static final int MAX_NIGHTS_AVAILABLE = 5;
    private static final int MAX_DAYS_AVAILABLE = 5;

    private int mDay;

    private String[] weekDays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    //region View Components

    @BindView(R.id.fragment_weekly_day_timeslots_recycler)
    android.support.v7.widget.RecyclerView timeslotsRecycler;

    @BindView(R.id.fragment_weekly_day_days_left_textview)
    TextView numberDaysLeftTextView;

    @BindView(R.id.fragment_weekly_day_nights_left_textview)
    TextView numberNightsLeftTextView;

    @BindView(R.id.fragment_weekly_day_week_day_textview)
    TextView dayOfWeekTextView;

    @BindView(R.id.fragment_weekly_day_add_timeslot_button)
    Button addTimeslotButton;

    //endregion

    public static WeeklyDayFragment newInstance() {return new WeeklyDayFragment(); }

    public WeeklyDayFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root  = inflater.inflate(R.layout.fragment_weekly_day, container, false);
        ButterKnife.bind(this, root);
        setupView();
        return root;
    }

    private void setupView() {
        setupButtons();
        dayOfWeekTextView.setText(String.format("Weekly Program: %s", weekDays[mDay]));
    }

    private void setupButtons() {
        addTimeslotButton.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.fragment_weekly_day_add_timeslot_button:
                break;
        }
    }
}
