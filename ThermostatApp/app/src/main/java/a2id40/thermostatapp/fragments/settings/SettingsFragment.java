package a2id40.thermostatapp.fragments.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import a2id40.thermostatapp.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rafael on 6/5/16.
 */

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private static final double MIN_TEMPERATURE = 5.0;
    private static final double MAX_TEMPERATURE = 30.0;

    //region View Components

    @BindView(R.id.fragment_settings_info_textview)
    TextView mSettingsTextView;

    @BindView(R.id.fragment_settings_day_textview)
    TextView mDayTempTextView;

    @BindView(R.id.fragment_settings_night_textview)
    TextView mNightTempTextView;

    @BindView(R.id.fragment_settings_vacation_textview)
    TextView mVacationTempTextView;

    @BindView(R.id.fragment_settings_set_day_textview)
    TextView mEditDayText;

    @BindView(R.id.fragment_settings_set_night_textview)
    TextView mEditNightText;

    @BindView(R.id.fragment_settings_set_vacation_textview)
    TextView mEditVacationText;

    @BindView(R.id.fragment_settings_save_button)
    TextView mSaveButton;

    //endregion

    double currentDayTemp = 22.2;
    double currentNightTemp = 20.0;
    double currentVacationTemp = 18.8;

    double setDayTemp = 0.00;
    double setNightTemp = 20.0;
    double setVacationTemp = 18.8;

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
        // TODO
        setupView();
        return root;
    }

    private void setupView() {
        setHintTexts();
        setupButtons();
    }

    private void setHintTexts(){
        // Set hint text with current setting for each temperature
        mEditDayText.setHint(currentDayTemp + "");
        mEditNightText.setHint(currentNightTemp + "");
        mEditVacationText.setHint(currentVacationTemp + "");
    }

    private void clearInputValues(){
        // Set input text with empty value for each temperature
        mEditDayText.setText("");
        mEditNightText.setText("");
        mEditVacationText.setText("");
    }

    private void setupButtons() {
        mSaveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.fragment_settings_save_button:

                int numberPopUps = 0;
                boolean dataChanged = false;
                boolean tempOK = false;
                boolean sameDayTemp = false, sameNightTemp = false, sameVacationTemp = false;

                // Save values and check temperatures are between 5 and 30, otherwise give a pop up
                if (mEditDayText.getText().length() > 0) {
                    setDayTemp = Double.parseDouble(mEditDayText.getText().toString());
                    // Limiting to 1 decimal (precision is in 0.1)
                    setNightTemp = roudToOneDecimal(setNightTemp);
                    sameDayTemp = (currentDayTemp == setDayTemp);
                    if (!sameDayTemp){   // Only check everything if it is different
                        tempOK = checkTemperatureInRange(setDayTemp);
                        if (!tempOK && numberPopUps == 0) {
                            Toast.makeText(getContext(), "All temperatures must be set to values between 5.0ºC and 30.0ºC.",
                                Toast.LENGTH_SHORT).show();
                            numberPopUps++;
                        } else {
                            // Otherwise, we overwrite it
                            currentDayTemp = setDayTemp;
                            dataChanged = true;
                        }
                    }
                }

                // Save values and check temperatures are between 5 and 30, otherwise give a pop up
                if (mEditNightText.getText().length() > 0) {
                    setNightTemp = Double.parseDouble(mEditNightText.getText().toString());
                    sameNightTemp = (currentNightTemp == setNightTemp);
                    // Limiting to 1 decimal (precision is in 0.1)
                    setNightTemp = roudToOneDecimal(setNightTemp);
                    if (!sameNightTemp){   // Only check everything if it is different
                        tempOK = checkTemperatureInRange(setNightTemp);
                        if (!tempOK && numberPopUps == 0) {
                            Toast.makeText(getContext(), "All temperatures must be set to values between 5.0ºC and 30.0ºC.",
                                Toast.LENGTH_SHORT).show();
                            numberPopUps++;
                        } else {
                            // Otherwise, we overwrite it
                            currentNightTemp = setNightTemp;
                            dataChanged = true;
                        }
                    }
                }

                // Save values and check temperatures are between 5 and 30, otherwise give a pop up
                if (mEditVacationText.getText().length() > 0) {
                    setVacationTemp = Double.parseDouble(mEditVacationText.getText().toString());
                    // Limiting to 1 decimal (precision is in 0.1)
                    setVacationTemp = roudToOneDecimal(setVacationTemp);
                    sameVacationTemp = (currentVacationTemp == setVacationTemp);
                    if (!sameVacationTemp) {   // Only check everything if it is different
                        tempOK = checkTemperatureInRange(setVacationTemp);
                        if (!tempOK && numberPopUps == 0) {
                            Toast.makeText(getContext(), "All temperatures must be set to values between 5.0ºC and 30.0ºC.",
                                Toast.LENGTH_SHORT).show();
                            numberPopUps++;
                        } else {
                            // Otherwise, we overwrite it
                            currentVacationTemp = setVacationTemp;
                            dataChanged = true;
                        }
                    }
                }

                // In case no changes are introduced
                if ((mEditDayText.getText().length() == 0 && mEditNightText.getText().length() == 0 &&
                        mEditVacationText.getText().length() == 0)
                        || (sameDayTemp || sameNightTemp || sameVacationTemp)) {
                    // Pop up message
                    Toast.makeText(getContext(), "No changes to be saved.", Toast.LENGTH_SHORT).show();
                    // Clear input values
                    clearInputValues();
                }

                // If everything OK, update and show pop up message for feedback to user
                if (dataChanged && numberPopUps == 0) {
                    // Send changes to sever (override with new values)
                    // TODO
                    // Pop up message
                    Toast.makeText(getContext(), "Your changes have been saved.", Toast.LENGTH_SHORT).show();
                    // Update hint texts values
                    setHintTexts();
                    // Clear input values
                    clearInputValues();
                }
                break;
        }
    }

    private boolean checkTemperatureInRange(double currentTemp) {
        if (currentTemp < MIN_TEMPERATURE || currentTemp > MAX_TEMPERATURE) {
            return false;
        } else {
            return true;
        }
    }

    private double roudToOneDecimal (double number){
        return Math.round ( number * 10.0) / 10.0;
    }
}
