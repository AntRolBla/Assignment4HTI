package a2id40.thermostatapp.fragments.weekly;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import a2id40.thermostatapp.R;
import a2id40.thermostatapp.fragments.vacation.VacationFragment;
import butterknife.ButterKnife;

/**
 * Created by rafaelring on 6/9/16.
 */

public class WeeklyDayFragment extends Fragment {

    private int mDay;

    //region View Components

    //endregion

    public static WeeklyDayFragment newInstance() {
        return new WeeklyDayFragment();
    }

    public WeeklyDayFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root  = inflater.inflate(R.layout.fragment_weekly_day, container, false);
        ButterKnife.bind(this, root);
        setupView();
        return root;
    }

    private void setupView() {

    }
}
