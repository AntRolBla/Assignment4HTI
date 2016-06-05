package a2id40.thermostatapp.daynight;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import a2id40.thermostatapp.R;
import a2id40.thermostatapp.vacation.VacationFragment;
import butterknife.ButterKnife;

/**
 * Created by rafael on 6/5/16.
 */

public class DayNightFragment extends Fragment {

    //region View Components

    //endregion

    public static DayNightFragment newInstance() {
        return new DayNightFragment();
    }

    public DayNightFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root  = inflater.inflate(R.layout.fragment_daynight, container, false);
        ButterKnife.bind(this, root);
        setupView();
        return root;
    }

    private void setupView() {

    }

}
