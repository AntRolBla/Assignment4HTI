package a2id40.thermostatapp.fragments.main;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
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
import a2id40.thermostatapp.fragments.Utils.AnimatedColor;
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

    @BindView(R.id.fragment_main_vacation_switch)
    Switch mVacationSwitch;

    @BindView(R.id.fragment_main_temperature_circle)
    FrameLayout mTempCircle;

    @BindView(R.id.fragment_main_content_container)
    LinearLayout mContentContainer;

    @BindView(R.id.fragment_main_noconnection)
    LinearLayout mNoConnectionPlaceholder;

    @BindView(R.id.fragment_main_noconnection_retry)
    Button mNoConnectionRetryButton;

    //endregion

    // region Variables declaration

    private double mCurrentTemperature = 21.0;
    private double mTargetTemperature = 21.0;

    private boolean firstIteration = true;
    private boolean mShouldWaitToSync = true;
    private boolean mIsSyncActive = false;
    private double mCurrentTargetTemp = 0.0;

    // endregion

    public static MainFragment newInstance() { return new MainFragment(); }

    public MainFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root  = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, root);
        setupView();
        setupData();
        setupThread();
        return root;
    }

    private void setupThread(){
        // Thread for updating the temperature values
        final Activity act = this.getActivity();
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(20000);
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getCurrentTemperature(false);
                                if (!mIsSyncActive) {
                                    getTargetTemperature(false);
                                }
                                getVacationState(false);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();
    }

    private void setupData() {
        getCurrentTemperature(true); // Get what current temperature the server has
        getTargetTemperature(true); // Get what target temperature the server has
        getVacationState(true); // Get state of vacation
    }

    private void setupView() {
        ((BaseActivity)getActivity()).setTitle(R.string.fragment_main_title);
        setupButtons(); // Set up buttons listeners
    }

    // Setting the listener for the buttons
    private void setupButtons() {
        mMinus1Button.setOnClickListener(this);
        mMinus01Button.setOnClickListener(this);
        mPlus01Button.setOnClickListener(this);
        mPlus1Button.setOnClickListener(this);
        mVacationSwitch.setOnClickListener(this);
        mNoConnectionRetryButton.setOnClickListener(this);
    }

    private void updateCircleColor() {
        double value = (mTargetTemperature - MIN_TEMPERATURE) / (MAX_TEMPERATURE - MIN_TEMPERATURE);
        AnimatedColor color = new AnimatedColor(ContextCompat.getColor(getContext(), R.color.lightBlue), ContextCompat.getColor(getContext(), R.color.lightRed));
        int resultColor = color.with((float)value);
        GradientDrawable background = (GradientDrawable) mTempCircle.getBackground();
        background.setStroke(18, resultColor);
    }

    private void updateButtonsState(double temperature){
        if (mVacationSwitch.isChecked()){
            mMinus1Button.setEnabled(false);
            mMinus01Button.setEnabled(false);
            mPlus01Button.setEnabled(false);
            mPlus1Button.setEnabled(false);
        } else {
            mMinus1Button.setEnabled(true);
            mMinus01Button.setEnabled(true);
            mPlus1Button.setEnabled(true);
            mPlus01Button.setEnabled(true);
            if (temperature < MIN_TEMPERATURE + 1){
                mMinus1Button.setEnabled(false);
                if (temperature == MIN_TEMPERATURE){
                    mMinus01Button.setEnabled(false);
                }
            } else {
                if (temperature > MAX_TEMPERATURE - 1){
                    mPlus1Button.setEnabled(false);
                    if (temperature == MAX_TEMPERATURE){
                        mPlus01Button.setEnabled(false);
                    }
                }
            }
        }
    }

    private void onTargetTemperatureUpdated(Double temperature){
        mCurrentTargetTemp = mTargetTemperature;
        mTemperatureTextView.setText(String.format(getString(R.string.fragment_main_temp_format), temperature));
        updateCircleColor();
        updateButtonsState(temperature);
    }

    private void changeTemperature(double amount) {
        mTargetTemperature = mTargetTemperature + amount;
        updateButtonsState(mTargetTemperature);
        mTemperatureTextView.setText(String.format(getString(R.string.fragment_main_temp_format), mTargetTemperature));
        updateCircleColor();

        if (mIsSyncActive) {
            mShouldWaitToSync = true;
        } else {
            setUpdateTargetTimer();
            mIsSyncActive = true;
        }

    }

    private void setUpdateTargetTimer() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (mShouldWaitToSync) {
                    setUpdateTargetTimer();
                    mShouldWaitToSync = false;
                } else {
                    putTargetTemperature(mTargetTemperature, mCurrentTargetTemp); // Put target temperature
                    mIsSyncActive = false;
                }
            }
        }, 2000);
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
            case R.id.fragment_main_vacation_switch:
                putSwitchWeeklyOnOff();
                break;
            case R.id.fragment_main_noconnection_retry:
                retrySetupData();
                break;
        }
    }

    private void retrySetupData() {
        toggleNoConnectionPlaceholder(false);
        setupData();
    }

    private void toggleNoConnectionPlaceholder(boolean shouldDisplay) {
        if (shouldDisplay) {
            mNoConnectionPlaceholder.setVisibility(View.VISIBLE);
            mContentContainer.setVisibility(View.GONE);
        } else {
            mNoConnectionPlaceholder.setVisibility(View.GONE);
            mContentContainer.setVisibility(View.VISIBLE);
        }
    }

    // region server GET

    // Target temperature caller
    private void getTargetTemperature(final boolean isFirstTime){
        Call<TargetTemperatureModel> getTargetTemp = APIClient.getClient().getTargetTemperature();
        getTargetTemp.enqueue(new Callback<TargetTemperatureModel>() {

            public void onResponse(Call<TargetTemperatureModel> call, Response<TargetTemperatureModel> response) {
                if (response.isSuccessful()){
                    mTargetTemperature = response.body().getTargetTemperature();
                    onTargetTemperatureUpdated(mTargetTemperature);
                } else {
                    if (isFirstTime){
                        toggleNoConnectionPlaceholder(true);
                    }
                }
            }

            public void onFailure(Call<TargetTemperatureModel> call, Throwable t) {
                if (isFirstTime){
                    toggleNoConnectionPlaceholder(true);
                }
            }
        });
    }

    // Current temperature caller
    private void getCurrentTemperature(final boolean isFirstTime){
        Call<TemperatureModel> callTemperatureCurrent = APIClient.getClient().getCurrentTemperature();
        callTemperatureCurrent.enqueue(new Callback<TemperatureModel>() {

            public void onResponse(Call<TemperatureModel> call, Response<TemperatureModel> response) {
                //((BaseActivity) getActivity()).hideLoadingScreen();
                if (response.isSuccessful()){
                    mCurrentTemperature = response.body().getCurrentTemperature();
                    mInfoTextView.setText(String.format(getString(R.string.fragment_main_info_format), mCurrentTemperature));
                } else {
                    if (isFirstTime){
                        toggleNoConnectionPlaceholder(true);
                    }
                }
            }

            public void onFailure(Call<TemperatureModel> call, Throwable t) {
                if (isFirstTime){
                    toggleNoConnectionPlaceholder(true);
                }
            }
        });
    }

    // On vacation caller
    private void getVacationState(final boolean isFirstTime) {
        Call<WeekProgramState> callWeeklyOn = APIClient.getClient().getWeekProgramState();
        callWeeklyOn.enqueue(new Callback<WeekProgramState>() {
            public void onResponse(Call<WeekProgramState> call, Response<WeekProgramState> response) {
                if (response.isSuccessful()){
                    mVacationSwitch.setChecked(response.body().isWeekProgramOn());
                    updateButtonsState(mCurrentTemperature);
                } else {
                    if (isFirstTime){
                        toggleNoConnectionPlaceholder(true);
                    }
                }
            }

            public void onFailure(Call<WeekProgramState> call, Throwable t) {
                if (isFirstTime){
                    toggleNoConnectionPlaceholder(true);
                }
            }
        });
    }

    //endregion

    // region server PUT

    // Switch the weekly on/off PUT
    private void putSwitchWeeklyOnOff (){
        Call<UpdateResponse> setWeekProgramState = APIClient.getClient().setWeekProgramState(new WeekProgramState(mVacationSwitch.isChecked()));
        ((BaseActivity) getActivity()).showLoadingScreen();
        setWeekProgramState.enqueue(new Callback<UpdateResponse>(){
            public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                ((BaseActivity) getActivity()).hideLoadingScreen();
                if (response.isSuccessful() && response.body().isSuccess()){
                    updateButtonsState(mCurrentTemperature);
                    String message;
                    if (mVacationSwitch.isChecked()){
                        message = String.format("Vacation mode is on.\r\nTemperature will be maintained at %.1f", mTargetTemperature);
                    } else {
                        message = "Vacation mode is off. \r\nWeek program will be restored";
                    }
                    SnackBarHelper.showSuccessMessage(getView(), message);
                } else {
                    SnackBarHelper.showErrorSnackBar(getView());
                    mVacationSwitch.toggle();
                }
            }

            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                ((BaseActivity) getActivity()).hideLoadingScreen();
                SnackBarHelper.showErrorSnackBar(getView());
                mVacationSwitch.toggle();
            }
        });
    }

    // Target temperature PUT
    private void putTargetTemperature(Double temperature, final Double currentTemperature){
        TargetTemperatureModel targetTempModel = new TargetTemperatureModel(temperature);
        Call<UpdateResponse> setTargetTemperature = APIClient.getClient().setTargetTemperature(targetTempModel);
        ((BaseActivity) getActivity()).showLoadingScreen();
        setTargetTemperature.enqueue(new Callback<UpdateResponse>(){

            public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                ((BaseActivity) getActivity()).hideLoadingScreen();
                if (response.isSuccessful() && response.body().isSuccess()){
                    // If success, do nothing
                } else {
                    onTargetTemperatureUpdated(currentTemperature);
                    SnackBarHelper.showErrorSnackBar(getView());
                }
            }

            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                onTargetTemperatureUpdated(currentTemperature);
                ((BaseActivity) getActivity()).hideLoadingScreen();
                SnackBarHelper.showErrorSnackBar(getView());
            }

        });
    }

    //endregion

}