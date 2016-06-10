package a2id40.thermostatapp.fragments.main;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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

    @BindView(R.id.fragment_main_linearlayout)
    LinearLayout mMainLinearLayout;

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

    // Initial values, once finished, get data from server
    private double mCurrentTemperature = 21.0;
    private boolean mSwitchState = false;

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
        switchState();
        return root;
    }

    private void setupView() {
        mInfoTextView.setText(String.format(getString(R.string.fragment_main_info_format), mCurrentTemperature));
        mTemperatureTextView.setText(String.format(getString(R.string.fragment_main_temp_format), mCurrentTemperature));
        setupButtons();
    }

    // Setting the listener for the buttons
    private void setupButtons() {
        mMinus1Button.setOnClickListener(this);
        mMinus01Button.setOnClickListener(this);
        mPlus01Button.setOnClickListener(this);
        mPlus1Button.setOnClickListener(this);
        mVacationSwitch.setOnClickListener(this);
    }

    // Method for getting the switch state at start (ON/OFF)
    private void switchState(){
        if (mSwitchState){
            mVacationSwitch.setChecked(true);
        } else {
            mVacationSwitch.setChecked(false);
        }
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
            // Switch functionalities
            case R.id.fragment_main_vacation_switch:
                if (mVacationSwitch.isChecked()) {
                    // Override current temperature, set information to server
                    // TODO

                    changeTemperatureButtonsEnable(false);
                    mSwitchState = true;
                    // Optional: set background to grey
                    //mMainLinearLayout.setBackgroundColor(Color.GRAY);
                    Toast.makeText(getContext(), "The vacation mode is now enabled.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "All temperatures are overridden.", Toast.LENGTH_SHORT).show();

                } else {
                    // Set temperature from weekly (day or night)
                    // TODO

                    changeTemperatureButtonsEnable(true);
                    mSwitchState = false;
                    // Optional: set background back to white
                    //mMainLinearLayout.setBackgroundColor(Color.WHITE);
                    Toast.makeText(getContext(), "The vacation mode is now disabled.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "The temperature values are taken from the weekly program.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    // Auxiliary method for setting the buttons to enable or disable (depends on 'state')
    private void changeTemperatureButtonsEnable(boolean state){
        mMinus1Button.setEnabled(state);
        mMinus01Button.setEnabled(state);
        mPlus01Button.setEnabled(state);
        mPlus1Button.setEnabled(state);
    }

}
