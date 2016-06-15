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
import a2id40.thermostatapp.data.models.WeekProgram;
import a2id40.thermostatapp.data.models.WeekProgramModel;
import a2id40.thermostatapp.data.models.WeekProgramState;
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
    // The current temp should be updated as it is changed in the server, as well as mInfoTextView
    private double mTargetTemperature = 21.0;
    // The vacation temp should be taken from the server
    private double mOnVacationTemperature = 15.0;
    // The weekly temp should be taken from the server
    private double mTemperatureFromWeekly = 19.9;
    private boolean mSwitchState = false;
    private String mCurrentDayString = "";

    private DayModel mDayModel;
    private TemperatureModel mTemperatureModel;
    private TargetTemperatureModel mTargetTemperatureModel;
    private WeekProgramState mWeekProgramStateModel;
    private WeekProgramModel mWeekProgramModel;

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
        // Get what target temperature the server has
        targetTemperatureCaller();
        // Get state for the switch, then update mSwitchState variable and call switchState()
        onVacationSwitchCaller();
        onVacationSwitchUpdater();
    }

    private void setupTexts(){
        // Delete texts
        mInfoTextView.setText("");
        mTemperatureTextView.setText("");
        // Put new texts
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
        // Update value of current temperature to server every time we change it
        updateTemperatureToServer(mTargetTemperature);
        // Update the current temperature
        currentTemperatureCaller();
        // Update on screen texts
        setupTexts();
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
                // Update on screen texts
                setupTexts();
                break;
            case R.id.fragment_main_minus01_button:
                changeTemperature(-0.1);
                // Update on screen texts
                setupTexts();
                break;
            case R.id.fragment_main_plus01_button:
                changeTemperature(0.1);
                // Update on screen texts
                setupTexts();
                break;
            case R.id.fragment_main_plus1_button:
                changeTemperature(1.0);
                // Update on screen texts
                setupTexts();
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
                    // Pop up messages
                    Toast.makeText(getContext(), "The vacation mode is now enabled.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "All temperatures are overridden.", Toast.LENGTH_SHORT).show();
                    // Update on screen texts
                    setupTexts();
                } else {
                    // Set temperature from weekly (day or night)
                    switchOFFVacationModeOnServer();
                    // Make available the buttons that can be used and set the switch state to non active
                    if (mTargetTemperature > MIN_TEMPERATURE){ changeTemperatureNegativeButtonsEnable(true); }
                    if (mTargetTemperature < MAX_TEMPERATURE){ changeTemperaturePositiveButtonsEnable(true); }
                    mSwitchState = false;
                    // Pop up messages
                    Toast.makeText(getContext(), "The vacation mode is now disabled.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "The temperature values are taken from the weekly program.", Toast.LENGTH_SHORT).show();
                    // Update on screen texts
                    setupTexts();
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
        // Update the server value for target temperature (POST)
        putTargetTemperature(temperature);
    }

    // Method called when the onVacation switch is changed from OFF to ON
    private void switchONVacationModeOnServer(){
        // TODO
        // Get onVacation temperature value
        // >>> needs get getOnVacationTemperature                                 <Missing from API>
        // mOnVacationTemperature = ...

        // Override target temperature (local)
        // mTargetTemperature = mOnVacationTemperature;

        // Override target temperature on sever (PUT)  [setTargetTemperature]
        putTargetTemperature(mTargetTemperature);

        // Disable weekly switches on server; false (PUT)
        putSwitchWeeklyOnOff(false);
    }

    // Method called when the onVacation switch is changed from ON to OFF
    private void switchOFFVacationModeOnServer(){

        // Set weekly back again: true (PUT)
        putSwitchWeeklyOnOff(true);

        // TODO - Automatic
        // Get temperature from weekly (GET)
            // mTemperatureFromWeekly = ...
        // Override target temperature (local)
            // mTargetTemperature = mTemperatureFromWeekly;
        // Override target temperature on server (PUT)
            //putTargetTemperature(mTargetTemperature);

        // Update local value for temperatures
        targetTemperatureCaller();
        currentTemperatureCaller();
    }

    // Callers as auxiliar methods  ---------------------------------------------------------------- [Callers]

    // Current day caller
    private void currentDayCaller(){
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
    private void currentTemperatureCaller(){
        Call<TemperatureModel> callTemperatureCurrent = APIClient.getClient().getCurrentTemperature();
        // makes the request, can have two responses from server
        callTemperatureCurrent.enqueue(new Callback<TemperatureModel>() {

            // has to validate is response is success
            public void onResponse(Call<TemperatureModel> call, Response<TemperatureModel> response) {
                if (response.isSuccessful()){
                    mTemperatureModel = response.body(); // getting the response into the model variable
                    mCurrentTemperature = mTemperatureModel.getCurrentTemperature(); // passing to String variable
                    setupTexts();
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
    private void targetTemperatureCaller(){
        Call<TargetTemperatureModel> callTargetTemperature = APIClient.getClient().getTargetTemperature();
        // makes the request, can have two responses from server
        callTargetTemperature.enqueue(new Callback<TargetTemperatureModel>() {

            // has to validate is response is success
            public void onResponse(Call<TargetTemperatureModel> call, Response<TargetTemperatureModel> response) {
                if (response.isSuccessful()){
                    mTargetTemperatureModel = response.body(); // getting the response into the model variable
                    mTargetTemperature = mTargetTemperatureModel.getTargetTemperature(); // passing to String variable
                    setupTexts();
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
    private void onVacationSwitchCaller() {
        Call<WeekProgramState> callWeeklyOn = APIClient.getClient().getWeekProgramState();
        // makes the request, can have two responses from server
        callWeeklyOn.enqueue(new Callback<WeekProgramState>() {

            // has to validate is response is success
            public void onResponse(Call<WeekProgramState> call, Response<WeekProgramState> response) {
                if (response.isSuccessful()){
                    mWeekProgramStateModel = response.body(); // getting the response into the model variable
                    mSwitchState = mWeekProgramStateModel.isWeekProgramOn(); // passing to String variable
                    setupTexts();
                    Double a = getTemperatureInRange(mTargetTemperature, 0.0);
                } else {
                    try {
                        String onResponse = response.errorBody().string();
                    } catch (IOException e){
                    };
                }
            }

            public void onFailure(Call<WeekProgramState> call, Throwable t) {
                String error = t.getMessage();
            }
        });
    }

    // On vacation switcher updater
    private void onVacationSwitchUpdater() {
        // Updates the options of the main page depending on if it is on Weelky or not
        if (mSwitchState) {
            // Disable all 4 buttons and set the switch state to active
            changeTemperaturePositiveButtonsEnable(false);
            changeTemperatureNegativeButtonsEnable(false);
            // Update on screen texts
            setupTexts();
        } else {
            // Make available the buttons that can be used and set the switch state to non active
            if (mTargetTemperature > MIN_TEMPERATURE){ changeTemperatureNegativeButtonsEnable(true); }
            if (mTargetTemperature < MAX_TEMPERATURE){ changeTemperaturePositiveButtonsEnable(true); }
            // Update on screen texts
            setupTexts();
        }
    }

    // Weekly program caller
    private void weeklyProgramCaller(){
        final Call<WeekProgramModel> weekProgramCall = APIClient.getClient().getWeekProgram();
        // makes the request, can have two responses from server
        weekProgramCall.enqueue(new Callback<WeekProgramModel>() {

            // has to validate is response is success
            public void onResponse(Call<WeekProgramModel> call, Response<WeekProgramModel> response) {
                if (response.isSuccessful()){
                    mWeekProgramModel = response.body();
                    mWeekProgramModel.getWeekProgram().setIsWeekProgramOn(false);
                } else {
                    try {
                        String onResponse = response.errorBody().string();
                    } catch (IOException e){
                    };
                }
            }

            public void onFailure(Call<WeekProgramModel> call, Throwable t) {
                String error = t.getMessage();
            }
        });
    }

    // Put as auxiliar methods  -------------------------------------------------------------------- [Putters]

    // Target temperature PUT
    private void putTargetTemperature(Double temperature){
        TargetTemperatureModel temp = new TargetTemperatureModel(temperature);
        Call<UpdateResponse> setTargetTemperature = APIClient.getClient().setTargetTemperature(temp);
        setTargetTemperature.enqueue(new Callback<UpdateResponse>(){

            public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                if (response.isSuccessful() && response.body().isSuccess()){
                    // TODO
                    // Handle success (no nothing)
                } else {
                    // TODO
                    // Show error message
                    try {
                        String onResponse = response.errorBody().string();
                    } catch (IOException e){  }
                }
            }

            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                // TODO
                // Show error message
            }

        });
    }

    // Switch the weekly on/off PUT
    private void putSwitchWeeklyOnOff (Boolean state){
        Call<UpdateResponse> setIsWeekProgramOn = APIClient.getClient().setWeekProgram(mWeekProgramModel);
        setIsWeekProgramOn.enqueue(new Callback<UpdateResponse>(){

            public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                if (response.isSuccessful() && response.body().isSuccess()){
                    // TODO
                    String s = "Successful";
                    Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                } else {
                    // TODO
                    String s = "Error";
                    Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                    try {
                        String onResponse = response.errorBody().string();
                    } catch (IOException e){  }
                }
            }

            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                // TODO
                // Error message
            }

        });
    }

}
