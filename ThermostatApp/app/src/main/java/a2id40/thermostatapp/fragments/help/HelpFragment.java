package a2id40.thermostatapp.fragments.help;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import a2id40.thermostatapp.R;
import a2id40.thermostatapp.activities.base.util.ActivityUtils;
import a2id40.thermostatapp.fragments.main.MainFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rafael on 6/5/16.
 */

public class HelpFragment extends Fragment implements View.OnClickListener {

    //region View Components

    @BindView(R.id.fragment_help_main_info_textview)
    TextView mHelpMainTextView;

    @BindView(R.id.fragment_help_weekly_button)
    Button mHowToWeeklyButton;

    @BindView(R.id.fragment_help_temperature_button)
    Button mHowToTemperatureButton;

    @BindView(R.id.fragment_help_vacation_button)
    Button mHowToVacationButton;

    //endregion

    public static HelpFragment newInstance() {
        return new HelpFragment();
    }

    public HelpFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root  = inflater.inflate(R.layout.fragment_help, container, false);
        ButterKnife.bind(this, root);
        setupView();
        return root;
    }

    private void setupView() {
        setupButtons();
    }

    private void setupButtons() {
        mHowToWeeklyButton.setOnClickListener(this);
        mHowToTemperatureButton.setOnClickListener(this);
        mHowToVacationButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.fragment_help_weekly_button:
                ActivityUtils.replaceFragment(getActivity().getSupportFragmentManager(), HelpFragmentWeekly.newInstance(),
                        R.id.activity_base_container);
                break;
            case R.id.fragment_help_temperature_button:
                ActivityUtils.replaceFragment(getActivity().getSupportFragmentManager(), HelpFragmentTemperature.newInstance(),
                        R.id.activity_base_container);
                break;

            case R.id.fragment_help_vacation_button:
                ActivityUtils.replaceFragment(getActivity().getSupportFragmentManager(), HelpFragmentVacation.newInstance(),
                        R.id.activity_base_container);
                break;
        }
    }
}
