package a2id40.thermostatapp.fragments.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import a2id40.thermostatapp.activities.base.BaseActivity;
import a2id40.thermostatapp.data.api.APIClient;
import a2id40.thermostatapp.data.models.NightTemperatureModel;
import a2id40.thermostatapp.data.models.TargetTemperatureModel;
import a2id40.thermostatapp.data.models.TemperatureModel;
import a2id40.thermostatapp.data.models.UpdateResponse;
import a2id40.thermostatapp.data.models.WeekProgramModel;
import a2id40.thermostatapp.data.models.WeekProgramState;
import a2id40.thermostatapp.fragments.Utils.SnackBarHelper;
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
    private boolean mSwitchState = false;
    private double mCurrentNightTemp = 10.0;
    private boolean firstIteration = true;

    private TemperatureModel mTemperatureModel;
    private TargetTemperatureModel mTargetTemperatureModel;
    private WeekProgramState mWeekProgramStateModel;
    private WeekProgramModel mWeekProgramModel;
    private NightTemperatureModel mNightTempModel;

    // endregion

    public static MainFragment newInstance() { return new MainFragment(); }

    public MainFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root  = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, root);
        setupView();

        // Thread for updating the temperature values
        final Activity act = this.getActivity();
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(250);
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Update temperatures and text
                                currentTemperatureCaller();
                                targetTemperatureCaller();
                                setupTexts();
                                // If just created, update mSwitchState and mVacationSwitch state
                                if (firstIteration){
                                    firstIteration = !firstIteration;
                                    onVacationSwitchCaller();
                                    switchState();
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();

        return root;
    }

    private void setupView() {
        setupData();    // Get data from server
        setupButtons(); // Set up buttons listeners
        switchState();  // Set the switch state
        setupTexts();   // Set the texts with the data
    }

    private void setupData(){
        // Get what current temperature the server has
        currentTemperatureCaller();
        // Get what target temperature the server has
        targetTemperatureCaller();
        // Get state for the switch, then update mSwitchState variable and call switchState()
        onVacationSwitchCaller();
        onVacationSwitchUpdater();
        setupTexts();
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
        if (mSwitchState) {
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
        putTargetTemperature(mTargetTemperature);
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
                if (mSwitchState) { changeTemperature(-1.0); }
                else { onClickButtonsOnVacation(); }
                break;
            case R.id.fragment_main_minus01_button:
                if (mSwitchState) { changeTemperature(-0.1); }
                else { onClickButtonsOnVacation(); }
                break;
            case R.id.fragment_main_plus01_button:
                if (mSwitchState) { changeTemperature(0.1); }
                else { onClickButtonsOnVacation(); }
                break;
            case R.id.fragment_main_plus1_button:
                if (mSwitchState) { changeTemperature(1.0); }
                else { onClickButtonsOnVacation(); }
                break;
            // Switch functionalities
            case R.id.fragment_main_vacation_switch:

                // Act depending on the state of the switch
                if (mVacationSwitch.isChecked()) {
                    // Override current temperature, set information to server
                    switchONVacationModeOnServer();
                    // Disable all 4 buttons and set the switch state to active
                    changeTemperaturePositiveButtonsEnable(false);
                    changeTemperatureNegativeButtonsEnable(false);
                    // Pop up messages
                    Toast.makeText(getContext(), "The vacation mode is now enabled, all temperatures are overridden.",
                            Toast.LENGTH_SHORT).show();

                } else {
                    // Set temperature from weekly (day or night)
                    switchOFFVacationModeOnServer();
                    // Make available the buttons that can be used and set the switch state to non active
                    if (mTargetTemperature > MIN_TEMPERATURE){ changeTemperatureNegativeButtonsEnable(true); }
                    if (mTargetTemperature < MAX_TEMPERATURE){ changeTemperaturePositiveButtonsEnable(true); }
                    // Pop up messages
                    Toast.makeText(getContext(), "The vacation mode is now disabled.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "The temperature values are taken from the weekly program.", Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }

    private void onClickButtonsOnVacation(){
        changeTemperatureAllButtonsEnable(false);
        Toast.makeText(getContext(), "The vacation mode is enabled.", Toast.LENGTH_SHORT).show();
    }

    // Auxiliary methods for setting the buttons to enable or disable
    private void changeTemperaturePositiveButtonsEnable(boolean state){
        mPlus01Button.setEnabled(state);
        mPlus1Button.setEnabled(state);
    }

    private void changeTemperatureNegativeButtonsEnable(boolean state){
        mMinus1Button.setEnabled(state);
        mMinus01Button.setEnabled(state);
    }

    private void changeTemperatureAllButtonsEnable (boolean state){
        changeTemperaturePositiveButtonsEnable(state);
        changeTemperatureNegativeButtonsEnable(state);
    }

    // Method called when the onVacation switch is changed from OFF to ON
    private void switchONVacationModeOnServer(){
        // Get onVacation temperature value
        mOnVacationTemperature = getVacationTemperature();

        // Override target temperature (local)
        currentTemperatureCaller();
        mTargetTemperature = mOnVacationTemperature;
        setupTexts();

        // Override target temperature on sever (PUT)
        putTargetTemperature(mTargetTemperature);

        // Disable weekly switches on server (PUT)
        weeklyProgramCallerVacationON();
        putSwitchWeeklyOnOff();
    }

    // Method called when the onVacation switch is changed from ON to OFF
    private void switchOFFVacationModeOnServer(){
        // Set weekly back again (PUT)
        weeklyProgramCallerVacationOFF();
        putSwitchWeeklyOnOff();

        // Update local value for temperatures
        targetTemperatureCaller();
        currentTemperatureCaller();
        setupTexts();
    }

    private double getVacationTemperature(){
        // Get the night temperature at mCurrentNightTemp
        getNightTemperatureFromServer();
        // Get the target temperature at mTargetTemperature
        targetTemperatureCaller();
        // Compare and set the lowest
        if (mCurrentNightTemp <= mTargetTemperature){
            return mCurrentNightTemp;
        } else {
            return mTargetTemperature;
        }
    }

    // Callers as auxiliar methods  ---------------------------------------------------------------- [Callers]

    // Current temperature caller
    private void currentTemperatureCaller(){
        Call<TemperatureModel> callTemperatureCurrent = APIClient.getClient().getCurrentTemperature();
        ((BaseActivity) getActivity()).showLoadingScreen();
        callTemperatureCurrent.enqueue(new Callback<TemperatureModel>() {

            public void onResponse(Call<TemperatureModel> call, Response<TemperatureModel> response) {
                ((BaseActivity) getActivity()).hideLoadingScreen();
                if (response.isSuccessful()){
                    mTemperatureModel = response.body();
                    mCurrentTemperature = mTemperatureModel.getCurrentTemperature();
                    setupTexts();
                } else {
                    SnackBarHelper.showErrorSnackBar(getView());
                }
            }

            public void onFailure(Call<TemperatureModel> call, Throwable t) {
                ((BaseActivity) getActivity()).hideLoadingScreen();
                SnackBarHelper.showErrorSnackBar(getView());
            }
        });
    }

    // Target temperature caller
    private void targetTemperatureCaller(){
        Call<TargetTemperatureModel> callTargetTemperature = APIClient.getClient().getTargetTemperature();
        ((BaseActivity) getActivity()).showLoadingScreen();
        callTargetTemperature.enqueue(new Callback<TargetTemperatureModel>() {

            public void onResponse(Call<TargetTemperatureModel> call, Response<TargetTemperatureModel> response) {
                ((BaseActivity) getActivity()).hideLoadingScreen();
                if (response.isSuccessful()){
                    mTargetTemperatureModel = response.body();
                    mTargetTemperature = mTargetTemperatureModel.getTargetTemperature();
                    setupTexts();
                } else {
                    SnackBarHelper.showErrorSnackBar(getView());
                }
            }

            public void onFailure(Call<TargetTemperatureModel> call, Throwable t) {
                ((BaseActivity) getActivity()).hideLoadingScreen();
                SnackBarHelper.showErrorSnackBar(getView());
            }
        });
    }

    // On vacation caller
    private void onVacationSwitchCaller() {
        Call<WeekProgramState> callWeeklyOn = APIClient.getClient().getWeekProgramState();
        ((BaseActivity) getActivity()).showLoadingScreen();
        callWeeklyOn.enqueue(new Callback<WeekProgramState>() {
            public void onResponse(Call<WeekProgramState> call, Response<WeekProgramState> response) {
                ((BaseActivity) getActivity()).hideLoadingScreen();
                if (response.isSuccessful()){
                    mWeekProgramStateModel = response.body();
                    mSwitchState = mWeekProgramStateModel.isWeekProgramOn();
                    Double a = getTemperatureInRange(mTargetTemperature, 0.0);
                } else {
                    SnackBarHelper.showErrorSnackBar(getView());
                }
            }

            public void onFailure(Call<WeekProgramState> call, Throwable t) {
                ((BaseActivity) getActivity()).hideLoadingScreen();
                SnackBarHelper.showErrorSnackBar(getView());
            }
        });
    }

    // On vacation switcher updater
    private void onVacationSwitchUpdater() {
        // Updates the options of the main page depending on if it is on Weelky or not
        if (mSwitchState) {
            // Disable all 4 buttons
            changeTemperaturePositiveButtonsEnable(false);
            changeTemperatureNegativeButtonsEnable(false);
            mVacationSwitch.setChecked(true);
            // Update on screen texts
            setupTexts();
        } else {
            // Make available the buttons that can be used and set the switch state to non active
            if (mTargetTemperature > MIN_TEMPERATURE){ changeTemperatureNegativeButtonsEnable(true); }
            if (mTargetTemperature < MAX_TEMPERATURE){ changeTemperaturePositiveButtonsEnable(true); }
            mVacationSwitch.setChecked(false);
            // Update on screen texts
            setupTexts();
        }
    }

    // Weekly program caller ON (state to true)
    private void weeklyProgramCallerVacationON(){
        final Call<WeekProgramModel> weekProgramCall = APIClient.getClient().getWeekProgram();
        // makes the request, can have two responses from server
        weekProgramCall.enqueue(new Callback<WeekProgramModel>() {

            // has to validate is response is success
            public void onResponse(Call<WeekProgramModel> call, Response<WeekProgramModel> response) {
                if (response.isSuccessful()){
                    mWeekProgramModel = response.body();
                    mWeekProgramModel.getWeekProgram().setIsWeekProgramOn(true);
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

    // Weekly program caller OFF (state to false)
    private void weeklyProgramCallerVacationOFF(){
        final Call<WeekProgramModel> weekProgramCall = APIClient.getClient().getWeekProgram();
        weekProgramCall.enqueue(new Callback<WeekProgramModel>() {

            // has to validate is response is success
            public void onResponse(Call<WeekProgramModel> call, Response<WeekProgramModel> response) {
                if (response.isSuccessful()) {
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

    //Current night temperature caller
    private void getNightTemperatureFromServer(){
        Call<NightTemperatureModel> callNightTempModel = APIClient.getClient().getNightTemperature();
        ((BaseActivity) getActivity()).showLoadingScreen();
        callNightTempModel.enqueue(new Callback<NightTemperatureModel>() {
            @Override
            public void onResponse(Call<NightTemperatureModel> call, Response<NightTemperatureModel> response) {
                ((BaseActivity) getActivity()).hideLoadingScreen();
                if (response.isSuccessful()){
                    mNightTempModel = response.body();
                    mCurrentNightTemp = mNightTempModel.getNightTemperature();
                } else {
                   SnackBarHelper.showErrorSnackBar(getView());
                }
            }

            @Override
            public void onFailure(Call<NightTemperatureModel> call, Throwable t) {
                ((BaseActivity) getActivity()).hideLoadingScreen();
                SnackBarHelper.showErrorSnackBar(getView());
            }
        });
    }

    // Put as auxiliar methods  -------------------------------------------------------------------- [Putters]

    // Target temperature PUT
    private void putTargetTemperature(Double temperature){
        TargetTemperatureModel targetTempModel = new TargetTemperatureModel(temperature);
        Call<UpdateResponse> setTargetTemperature = APIClient.getClient().setTargetTemperature(targetTempModel);
        setTargetTemperature.enqueue(new Callback<UpdateResponse>(){

            public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                if (response.isSuccessful() && response.body().isSuccess()){
                    // If success, do nothing
                } else {
                    Snackbar.make(getView(), "There has been an error while accessing the server. Please try again.",
                            Snackbar.LENGTH_LONG).show();
                    try {
                        String onResponse = response.errorBody().string();
                    } catch (IOException e){  }
                }
            }

            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                Snackbar.make(getView(), "There has been an error while accessing the server. Please try again.",
                        Snackbar.LENGTH_LONG).show();
            }

        });
    }

    // Switch the weekly on/off PUT
    private void putSwitchWeeklyOnOff (){
        Call<UpdateResponse> setIsWeekProgramOn = APIClient.getClient().setWeekProgram(mWeekProgramModel);
        setIsWeekProgramOn.enqueue(new Callback<UpdateResponse>(){

            public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                if (response.isSuccessful() && response.body().isSuccess()){
                    // If success, do nothing
                } else {
                    Snackbar.make(getView(), "There has been an error while accessing the server. Please try again.",
                            Snackbar.LENGTH_LONG).show();
                    try {
                        String onResponse = response.errorBody().string();
                    } catch (IOException e){  }
                }
            }

            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                // Thrown always when first accessing the fragment
                // The first put to the server fails
                Snackbar.make(getView(), "There has been an error while accessing the server. Please try again.",
                        Snackbar.LENGTH_LONG).show();
                // Undo changes
                mVacationSwitch.toggle();
                mSwitchState = !mSwitchState;
            }

        });
    }

}