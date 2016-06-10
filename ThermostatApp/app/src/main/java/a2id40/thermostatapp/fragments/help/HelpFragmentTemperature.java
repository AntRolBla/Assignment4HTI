package a2id40.thermostatapp.fragments.help;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import a2id40.thermostatapp.R;
import a2id40.thermostatapp.activities.base.BaseActivity;
import a2id40.thermostatapp.activities.base.util.ActivityUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Antonio on 09/06/2016.
 */
public class HelpFragmentTemperature extends Fragment {

    //region View Components

    @BindView(R.id.fragment_help_temperature_info_textview)
    TextView mTemperatureHowToTextView;

    @BindView(R.id.fragment_help_temperature_description_one_textview)
    TextView mTemperatureDescriptionOneTextView;

    @BindView(R.id.fragment_help_temperature_description_two_textview)
    TextView mTemperatureDescriptionTwoTextView;

    //endregion

    public static HelpFragmentTemperature newInstance() {
        return new HelpFragmentTemperature();
    }

    public HelpFragmentTemperature() {}

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root  = inflater.inflate(R.layout.fragment_help_temperature, container, false);
        ButterKnife.bind(this, root);
        setupView();
        return root;
    }

    private void setupView() { }

    // Implementing back button for going to main page of Help
    public void doBack() {
        ActivityUtils.replaceFragment(getActivity().getSupportFragmentManager(), HelpFragment.newInstance(),
                R.id.activity_base_container);
    }
}
