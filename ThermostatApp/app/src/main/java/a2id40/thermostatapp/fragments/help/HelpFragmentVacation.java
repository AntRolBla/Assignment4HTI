package a2id40.thermostatapp.fragments.help;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import a2id40.thermostatapp.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Antonio on 09/06/2016.
 */
public class HelpFragmentVacation extends Fragment {

    //region View Components

    @BindView(R.id.fragment_help_vacation_info_textview)
    TextView mVacationHowToTextView;

    @BindView(R.id.fragment_help_vacation_description_one_textview)
    TextView mVacationDescriptionOneTextView;

    @BindView(R.id.fragment_help_vacation_description_two_textview)
    TextView mVacationDescriptionTwoTextView;

    //endregion

    public static HelpFragmentVacation newInstance() {
        return new HelpFragmentVacation();
    }

    public HelpFragmentVacation() {}

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root  = inflater.inflate(R.layout.fragment_help_vacation, container, false);
        ButterKnife.bind(this, root);
        setupView();
        return root;
    }

    private void setupView() { }
}
