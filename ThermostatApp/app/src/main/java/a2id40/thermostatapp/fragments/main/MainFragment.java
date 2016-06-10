package a2id40.thermostatapp.fragments.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import a2id40.thermostatapp.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rafael on 6/5/16.
 */

public class MainFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private static final double MIN_TEMPERATURE = 5.0;
    private static final double MAX_TEMPERATURE = 30.0;

    //region View Components

    @BindView(R.id.fragment_main_info_textview)
    TextView mInfoTextView;

    @BindView(R.id.fragment_main_temperature_textview)
    TextView mTemperatureTextView;

    @BindView(R.id.fragment_main_minus1_button)
    Button mMinus1Button;

    @BindView(R.id.fragment_main_minus01_button)
    Button mMinus01Button;

    @BindView(R.id.fragment_main_plus01_button)
    Button mPlus01Button;

    @BindView(R.id.fragment_main_plus1_button)
    Button mPlus1Button;

    @BindView(R.id.fragment_main_vacation_textview)
    TextView mVacationTextView;

    @BindView(R.id.fragment_main_vacation_switch)
    Switch mVacationSwitch;

    //endregion

    private double mCurrentTemperature = 21.0;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public MainFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root  = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, root);
        setupView();
        return root;
    }

    private void setupView() {
        mInfoTextView.setText(String.format(getString(R.string.fragment_main_info_format), mCurrentTemperature));
        mTemperatureTextView.setText(String.format(getString(R.string.fragment_main_temp_format), mCurrentTemperature));
        setupButtons();
    }

    private void setupButtons() {
        mMinus1Button.setOnClickListener(this);
        mMinus01Button.setOnClickListener(this);
        mPlus01Button.setOnClickListener(this);
        mPlus1Button.setOnClickListener(this);
    }

    private void changeTemperature(double amount) {
        mCurrentTemperature = getTemperatureInRange(mCurrentTemperature, amount);
        mInfoTextView.setText(String.format(getString(R.string.fragment_main_info_format), mCurrentTemperature));
        mTemperatureTextView.setText(String.format(getString(R.string.fragment_main_temp_format), mCurrentTemperature));
    }

    private double getTemperatureInRange(double currentTemp, double amount) {
        currentTemp = currentTemp + amount;
        if (currentTemp < MIN_TEMPERATURE) {
            return MIN_TEMPERATURE;
        } else if (currentTemp > MAX_TEMPERATURE) {
            return MAX_TEMPERATURE;
        } else {
            return currentTemp;
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.fragment_main_minus1_button:
                changeTemperature(-1.0);
                break;
            case R.id.fragment_main_minus01_button:
                changeTemperature(-0.1);
                break;
            case R.id.fragment_main_plus01_button:
                changeTemperature(0.1);
                break;
            case R.id.fragment_main_plus1_button:
                changeTemperature(1.0);
                break;
        }
    }
}
