package a2id40.thermostatapp.main;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import a2id40.thermostatapp.R;
import butterknife.ButterKnife;

/**
 * Created by rafael on 6/5/16.
 */

public class MainFragment extends android.support.v4.app.Fragment {

    //region View Components

    //endregion

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
        return root;
    }

    private void setupView() {

    }


}
