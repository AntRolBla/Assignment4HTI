package a2id40.thermostatapp.fragments.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import a2id40.thermostatapp.R;
import a2id40.thermostatapp.data.api.APIClient;
import a2id40.thermostatapp.data.models.DayModel;
import a2id40.thermostatapp.data.models.TargetTemperatureModel;
import a2id40.thermostatapp.data.models.TemperatureModel;
import a2id40.thermostatapp.data.models.UpdateResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    // region Variables declaration
    private double mCurrentTemperature = 21.0;
    // mCurrentTemperature should be updated as it is changed in the server, as well as mInfoTextView
    private double mTargetTemperature = 21.0;
    private boolean mSwitchState = false;
    private String mCurrentDayString = "";

    private DayModel mDayModel;
    private TemperatureModel mTemperatureModel;
    private TargetTemperatureModel mTargetTemperatureModel;

    // endregion

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
        setupData();    // Get data from server
        setupTexts();   // Put data on screen texts
        setupButtons(); // Set up buttons listeners
    }

    private void setupData(){
        // Get current day
        currentDayCaller();
        // Get what current temperature the server has
        currentTemperatureCaller();
        // Get what target temperature is set
        targetTemperatureCaller();
        // Get state for the switch, then update mSwitchState variable and call switchState()
        // TODO
        onVacationSwitchCaller();
        onVacationSwitchUpdater();
    }

    private void setupTexts(){
        mInfoTextView.setText(String.format(getString(R.string.fragment_main_info_format), mCurrentTemperature));
        mTemperatureTextView.setText(String.format(getString(R.string.fragment_main_temp_format), mTargetTemperature));
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
        mTargetTemperature = getTemperatureInRange(mTargetTemperature, amount);
        // Update on screen texts
        setupTexts();
        // Update value of current temperature to server every time we change it
        updateTemperatureToServer(mTargetTemperature);
    }

    private double getTemperatureInRange(double currentTemp, double amount) {
        currentTemp = currentTemp + amount;
        if (currentTemp <= MIN_TEMPERATURE) {
            // Disable negative buttons to prevent user from clicking
            changeTemperatureNegativeButtonsEnable(false);
            return MIN_TEMPERATURE;
        } else if (currentTemp >= MAX_TEMPERATURE) {
            // Disable positive buttons to prevent user from clicking
            changeTemperaturePositiveButtonsEnable(false);
            return MAX_TEMPERATURE;
        } else {
            changeTemperatureNegativeButtonsEnable(true);
            changeTemperaturePositiveButtonsEnable(true);
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
                    switchONVacationModeOnServer();
                    // Disable all 4 buttons and set the switch state to active
                    changeTemperaturePositiveButtonsEnable(false);
                    changeTemperatureNegativeButtonsEnable(false);
                    mSwitchState = true;
                    // Update on screen texts
                    setupTexts();
                    // Pop up messages
                    Toast.makeText(getContext(), "The vacation mode is now enabled.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "All temperatures are overridden.", Toast.LENGTH_SHORT).show();

                } else {
                    // Set temperature from weekly (day or night)
                    switchOFFVacationModeOnServer();
                    // Make available the buttons that can be used and set the switch state to non active
                    if (mTargetTemperature > MIN_TEMPERATURE){ changeTemperatureNegativeButtonsEnable(true); }
                    if (mTargetTemperature < MAX_TEMPERATURE){ changeTemperaturePositiveButtonsEnable(true); }
                    mSwitchState = false;
                    // Update on screen texts
                    setupTexts();
                    // Pop up messages
                    Toast.makeText(getContext(), "The vacation mode is now disabled.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "The temperature values are taken from the weekly program.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    // Auxiliary methods for setting the buttons to enable or disable (depends on 'state')
    private void changeTemperaturePositiveButtonsEnable(boolean state){
        mPlus01Button.setEnabled(state);
        mPlus1Button.setEnabled(state);
    }

    private void changeTemperatureNegativeButtonsEnable(boolean state){
        mMinus1Button.setEnabled(state);
        mMinus01Button.setEnabled(state);
    }

    // Method called everytime we change the temperature value through the buttons
    private void updateTemperatureToServer(double temperature){
        // TODO
        // Update the server value for target temperature (POST)
        //TargetTemperatureModel model = new TargetTemperatureModel (temperature);
        //UpdateResponse updateResponse = (UpdateResponse) APIClient.getClient().setTargetTemperature(model);
        // App crashes (probably the POST is not well defined)
    }

    // Method called when the onVacation switch is changed from OFF to ON
    private void switchONVacationModeOnServer(){
        // TODO
        // Override current temperature (local)
        // Override target temperature (local)
        // Override target temperature on sever (POST)  [setTargetTemperature]
        // >>> needs get onVacationTemperature                                    <Missing from API>
        // Disable weekly switches on server (POST)     [setWeekProgramState(off)]
    }

    // Method called when the onVacation switch is changed from ON to OFF
    private void switchOFFVacationModeOnServer(){
        // TODO
        // Set weekly back again (POST)                 [setWeekProgramState(on)]
        // Get temperature from weekly (GET)
        // Override current temperature (local)
        // Override target temperature (local)
        // Override target temperature on server (POST)
    }

    // Callers as auxiliar methods  ----------------------------------------------------------------

    // Current day caller
    public void currentDayCaller(){
        Call<DayModel> callDayModel = APIClient.getClient().getCurrentDay(); //create the request
        // makes the request, can have two responses from server
        callDayModel.enqueue(new Callback<DayModel>() {

            // has to validate is response is success
            public void onResponse(Call<DayModel> call, Response<DayModel> response) {
                if (response.isSuccessful()){
                    mDayModel = response.body(); // getting the response into the model variable
                    mCurrentDayString = mDayModel.getCurrentDay(); // passing to String variable
                } else {
                    try {
                        String onResponse = response.errorBody().string();
                    } catch (IOException e){
                    };
                }
            }

            public void onFailure(Call<DayModel> call, Throwable t) {
                String error = t.getMessage();
            }
        });
    }

    // Current temperature caller
    public void currentTemperatureCaller(){
        Call<TemperatureModel> callTemperatureCurrent = APIClient.getClient().getCurrentTemperature();
        // makes the request, can have two responses from server
        callTemperatureCurrent.enqueue(new Callback<TemperatureModel>() {

            // has to validate is response is success
            public void onResponse(Call<TemperatureModel> call, Response<TemperatureModel> response) {
                if (response.isSuccessful()){
                    mTemperatureModel = response.body(); // getting the response into the model variable
                    mCurrentTemperature = mTemperatureModel.getCurrentTemperature(); // passing to String variable
                } else {
                    try {
                        String onResponse = response.errorBody().string();
                    } catch (IOException e){
                    };
                }
            }

            public void onFailure(Call<TemperatureModel> call, Throwable t) {
                String error = t.getMessage();
            }
        });
    }

    // Target temperature caller
    public void targetTemperatureCaller(){
        Call<TargetTemperatureModel> callTargetTemperature = APIClient.getClient().getTargetTemperature();
        // makes the request, can have two responses from server
        callTargetTemperature.enqueue(new Callback<TargetTemperatureModel>() {

            // has to validate is response is success
            public void onResponse(Call<TargetTemperatureModel> call, Response<TargetTemperatureModel> response) {
                if (response.isSuccessful()){
                    mTargetTemperatureModel = response.body(); // getting the response into the model variable
                    mTargetTemperature = mTargetTemperatureModel.getTargetTemperature(); // passing to String variable
                } else {
                    try {
                        String onResponse = response.errorBody().string();
                    } catch (IOException e){
                    };
                }
            }

            public void onFailure(Call<TargetTemperatureModel> call, Throwable t) {
                String error = t.getMessage();
            }
        });
    }

    // On vacation caller
    public void onVacationSwitchCaller() {
        // TODO
    }

    // On vacation switcher updater
    public void onVacationSwitchUpdater() {
        // TODO
    }
}
