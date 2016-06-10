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
public class HelpFragmentWeekly extends Fragment {

    //region View Components

    @BindView(R.id.fragment_help_weekly_info_textview)
    TextView mWeeklyHowToTextView;

    @BindView(R.id.fragment_help_weekly_description_one_textview)
    TextView mWeeklyDescriptionOneTextView;

    @BindView(R.id.fragment_help_weekly_description_two_textview)
    TextView mWeeklyDescriptionTwoTextView;

    //endregion

    public static HelpFragmentWeekly newInstance() { return new HelpFragmentWeekly(); }

    public HelpFragmentWeekly() {}

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root  = inflater.inflate(R.layout.fragment_help_weekly, container, false);
        ButterKnife.bind(this, root);
        setupView();
        return root;
    }

    private void setupView() { }
}
