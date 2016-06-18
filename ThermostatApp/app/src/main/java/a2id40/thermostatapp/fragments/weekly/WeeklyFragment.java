package a2id40.thermostatapp.fragments.weekly;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import a2id40.thermostatapp.R;
import a2id40.thermostatapp.activities.base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rafael on 6/5/16.
 */

public class WeeklyFragment extends Fragment implements View.OnClickListener {

    //region View Components

    @BindView(R.id.fragment_weekly_main_monday_button)
    Button mMondayButton;

    @BindView(R.id.fragment_weekly_main_tuesday_button)
    Button mTuesdayButton;

    @BindView(R.id.fragment_weekly_main_wednesday_button)
    Button mWednesdayButton;

    @BindView(R.id.fragment_weekly_main_thursday_button)
    Button mThursdayButton;

    @BindView(R.id.fragment_weekly_main_friday_button)
    Button mFridayButton;

    @BindView(R.id.fragment_weekly_main_saturday_button)
    Button mSaturdayButton;

    @BindView(R.id.fragment_weekly_main_sunday_button)
    Button mSundayButton;

    //endregion

    public static WeeklyFragment newInstance() {
        return new WeeklyFragment();
    }

    public WeeklyFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root  = inflater.inflate(R.layout.fragment_weekly, container, false);
        ButterKnife.bind(this, root);
        setupView();
        return root;
    }

    private void setupView() {
        ((BaseActivity)getActivity()).setTitle(R.string.fragment_weekly_main_title);
        setupButtons();
    }

    private void setupButtons() {
        mMondayButton.setOnClickListener(this);
        mTuesdayButton.setOnClickListener(this);
        mWednesdayButton.setOnClickListener(this);
        mThursdayButton.setOnClickListener(this);
        mFridayButton.setOnClickListener(this);
        mSaturdayButton.setOnClickListener(this);
        mSundayButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_weekly_main_monday_button:
                ((BaseActivity) getActivity()).openWeeklyDay(0);
                break;
            case R.id.fragment_weekly_main_tuesday_button:
                ((BaseActivity) getActivity()).openWeeklyDay(1);
                break;
            case R.id.fragment_weekly_main_wednesday_button:
                ((BaseActivity) getActivity()).openWeeklyDay(2);
                break;
            case R.id.fragment_weekly_main_thursday_button:
                ((BaseActivity) getActivity()).openWeeklyDay(3);
                break;
            case R.id.fragment_weekly_main_friday_button:
                ((BaseActivity) getActivity()).openWeeklyDay(4);
                break;
            case R.id.fragment_weekly_main_saturday_button:
                ((BaseActivity) getActivity()).openWeeklyDay(5);
                break;
            case R.id.fragment_weekly_main_sunday_button:
                ((BaseActivity) getActivity()).openWeeklyDay(6);
                break;
        }
    }

}
