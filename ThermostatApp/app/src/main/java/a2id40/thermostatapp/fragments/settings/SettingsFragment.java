package a2id40.thermostatapp.fragments.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import a2id40.thermostatapp.R;
import a2id40.thermostatapp.activities.base.BaseActivity;
import a2id40.thermostatapp.data.api.APIClient;
import a2id40.thermostatapp.data.models.DayTemperatureModel;
import a2id40.thermostatapp.data.models.NightTemperatureModel;
import a2id40.thermostatapp.data.models.UpdateResponse;
import a2id40.thermostatapp.fragments.Utils.SnackBarHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by rafael on 6/5/16.
 */

public class SettingsFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {

    private static final double MIN_TEMPERATURE = 5.0;
    private static final double MAX_TEMPERATURE = 30.0;

    //region View Components

    @BindView(R.id.fragment_settings_set_day_textview)
    EditText mEditDayText;

    @BindView(R.id.fragment_settings_set_night_textview)
    EditText mEditNightText;

    @BindView(R.id.fragment_settings_save_button)
    TextView mSaveButton;

    //endregion

    // region Variables declaration

    double currentDayTemp = 22.2;
    double currentNightTemp = 20.0;

    double setDayTemp = 0.00;
    double setNightTemp = 20.0;

    private DayTemperatureModel mDayTempModel;
    private NightTemperatureModel mNightTempModel;

    //endregion

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    public SettingsFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root  = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, root);
        // Take data from server
        getInformationFromServer();
        // Setup
        setupView();
        return root;
    }

    private void setupView() {
        ((BaseActivity)getActivity()).setTitle(R.string.fragment_settings_title);
        setupEditTexts();
        setHintTexts();
        setupButtons();
    }

    private void setupEditTexts() {
        mEditDayText.setOnFocusChangeListener(this);
        mEditNightText.setOnFocusChangeListener(this);
    }

    private void setHintTexts(){
        // Set hint text with current setting for each temperature
        mEditDayText.setText(currentDayTemp + "");
        mEditNightText.setText(currentNightTemp + "");
    }

    private void clearInputValues(){
        // Set input text with empty value for each temperature
        mEditDayText.setText("");
        mEditNightText.setText("");
    }

    private void setupButtons() {
        mSaveButton.setOnClickListener(this);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch(v.getId()) {
            case R.id.fragment_settings_set_day_textview:
                if (hasFocus) {
                    mEditDayText.setText("");
                } else {
                    mEditDayText.setText(currentDayTemp + "");
                }
                break;
            case R.id.fragment_settings_set_night_textview:
                if (hasFocus) {
                    mEditNightText.setText("");
                } else {
                    mEditNightText.setText(currentNightTemp + "");
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.fragment_settings_save_button:
                double dayTemp = Double.parseDouble(mEditDayText.getText().toString());
                double nightTemp = Double.parseDouble(mEditNightText.getText().toString());

                dayTemp = roundToOneDecimal(dayTemp);
                nightTemp = roundToOneDecimal(nightTemp);

                if (!checkTemperatureInRange(dayTemp) || !checkTemperatureInRange(nightTemp)) {
                    SnackBarHelper.showErrorMessage(getView(), getString(R.string.fragment_settings_error_range));
                } else if (dayTemp == currentDayTemp && nightTemp == currentNightTemp) {
                    SnackBarHelper.showErrorMessage(getView(), getString(R.string.fragment_settings_error_nochange));
                } else {
                    putNewDayTempValue(dayTemp);
                    putNewNightTempValue(nightTemp);
                }

                break;
        }
    }

    // Simple checker for temperature values
    private boolean checkTemperatureInRange(double currentTemp) {
        if (currentTemp < MIN_TEMPERATURE || currentTemp > MAX_TEMPERATURE) {
            return false;
        } else {
            return true;
        }
    }

    // For rounding the input values
    private double roundToOneDecimal (double number){
        return Math.round ( number * 10.0) / 10.0;
    }

    private void getInformationFromServer(){
        dayTemperatureCaller();
        nightTemperatureCaller();
    }

    // Callers as auxiliar methods  ---------------------------------------------------------------- [Callers]

    //Current day temperature caller
    private void dayTemperatureCaller(){
        Call<DayTemperatureModel> callDayTempModel = APIClient.getClient().getDayTemperature();
        ((BaseActivity)getActivity()).showLoadingScreen();
        callDayTempModel.enqueue(new Callback<DayTemperatureModel>() {
            @Override
            public void onResponse(Call<DayTemperatureModel> call, Response<DayTemperatureModel> response) {
                ((BaseActivity)getActivity()).hideLoadingScreen();
                if (response.isSuccessful()){
                    mDayTempModel = response.body();
                    currentDayTemp = mDayTempModel.getDayTemperature();
                    // Update hint texts
                    setHintTexts();
                } else {
                    SnackBarHelper.showErrorSnackBar(getView());
                }
            }

            @Override
            public void onFailure(Call<DayTemperatureModel> call, Throwable t) {
                SnackBarHelper.showErrorSnackBar(getView());
            }
        });
    }

    //Current night temperature caller
    private void nightTemperatureCaller(){
        Call<NightTemperatureModel> callNightTempModel = APIClient.getClient().getNightTemperature();
        ((BaseActivity)getActivity()).showLoadingScreen();
        callNightTempModel.enqueue(new Callback<NightTemperatureModel>() {
            @Override
            public void onResponse(Call<NightTemperatureModel> call, Response<NightTemperatureModel> response) {
                ((BaseActivity)getActivity()).hideLoadingScreen();
                if (response.isSuccessful()){
                    mNightTempModel = response.body();
                    currentNightTemp = mNightTempModel.getNightTemperature();
                    // Update hint texts
                    setHintTexts();
                } else {
                    SnackBarHelper.showErrorSnackBar(getView());
                }
            }

            @Override
            public void onFailure(Call<NightTemperatureModel> call, Throwable t) {
                ((BaseActivity)getActivity()).hideLoadingScreen();
                SnackBarHelper.showErrorSnackBar(getView());
            }
        });
    }

    // Put as auxiliar methods  -------------------------------------------------------------------- [Putters]

    // Send currentDayTemp value to server (PUT)
    private void putNewDayTempValue(Double temperature){
        DayTemperatureModel dayTemp = new DayTemperatureModel(temperature);
        Call<UpdateResponse> setDayTemperature = APIClient.getClient().setDayTemperature(dayTemp);
        ((BaseActivity)getActivity()).showLoadingScreen();
        setDayTemperature.enqueue(new Callback<UpdateResponse>(){
            public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                ((BaseActivity)getActivity()).hideLoadingScreen();
                if (response.isSuccessful() && response.body().isSuccess()){
                    SnackBarHelper.showSuccessMessage(getView(), getString(R.string.fragment_settings_succes_saved));
                } else {
                    SnackBarHelper.showErrorSnackBar(getView());
                }
                getInformationFromServer();
            }

            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                ((BaseActivity)getActivity()).hideLoadingScreen();
                SnackBarHelper.showErrorSnackBar(getView());
                getInformationFromServer();
            }

        });

    }

    private void putNewNightTempValue(Double temperature){
        NightTemperatureModel nightTemp = new NightTemperatureModel(temperature);
        Call<UpdateResponse> setNightTemperature = APIClient.getClient().setNightTemperature(nightTemp);
        ((BaseActivity)getActivity()).showLoadingScreen();
        setNightTemperature.enqueue(new Callback<UpdateResponse>(){
            public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                ((BaseActivity)getActivity()).hideLoadingScreen();
                if (response.isSuccessful() && response.body().isSuccess()){
                    SnackBarHelper.showSuccessMessage(getView(), getString(R.string.fragment_settings_succes_saved));
                } else {
                    SnackBarHelper.showErrorSnackBar(getView());
                }
                getInformationFromServer();
            }

            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                ((BaseActivity)getActivity()).hideLoadingScreen();
                SnackBarHelper.showErrorSnackBar(getView());
                getInformationFromServer();
            }

        });
    }

}