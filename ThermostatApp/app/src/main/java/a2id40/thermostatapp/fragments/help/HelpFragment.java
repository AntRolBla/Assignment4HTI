package a2id40.thermostatapp.fragments.help;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import a2id40.thermostatapp.R;
import a2id40.thermostatapp.activities.base.BaseActivity;
import a2id40.thermostatapp.activities.base.util.ActivityUtils;
import a2id40.thermostatapp.fragments.main.MainFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rafael on 6/5/16.
 */

public class HelpFragment extends Fragment implements View.OnClickListener {

    //region View Components

    @BindView(R.id.fragment_help_weekly_button)
    Button mHowToWeeklyButton;

    @BindView(R.id.fragment_help_temperature_button)
    Button mHowToTemperatureButton;

    @BindView(R.id.fragment_help_vacation_button)
    Button mHowToVacationButton;

    @BindView(R.id.fragment_help_weekly_expandable)
    ExpandableRelativeLayout mWeeklyExpandable;

    @BindView(R.id.fragment_help_temperature_expandable)
    ExpandableRelativeLayout mTemperatureExpandable;

    @BindView(R.id.fragment_help_vacation_expandable)
    ExpandableRelativeLayout mVacationExpandable;

    //endregion

    public static HelpFragment newInstance() {
        return new HelpFragment();
    }

    public HelpFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root  = inflater.inflate(R.layout.fragment_help, container, false);
        ButterKnife.bind(this, root);
        setupView();
        return root;
    }

    private void setupView() {
        ((BaseActivity)getActivity()).setTitle(R.string.fragment_help_title);
        setupButtons();
        setupExpandables();
    }

    private void setupExpandables() {
        mWeeklyExpandable.post(new Runnable()
        {
            @Override
            public void run()
            {
                mWeeklyExpandable.collapse(-1, null);
            }
        });
        mTemperatureExpandable.post(new Runnable()
        {
            @Override
            public void run()
            {
                mTemperatureExpandable.collapse(-1, null);
            }
        });
        mVacationExpandable.post(new Runnable()
        {
            @Override
            public void run()
            {
                mVacationExpandable.collapse(-1, null);
            }
        });
    }

    private void setupButtons() {
        mHowToWeeklyButton.setOnClickListener(this);
        mHowToTemperatureButton.setOnClickListener(this);
        mHowToVacationButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.fragment_help_weekly_button:
                updateArrow(mHowToWeeklyButton, !mWeeklyExpandable.isExpanded());
                mWeeklyExpandable.toggle();
                break;
            case R.id.fragment_help_temperature_button:
                updateArrow(mHowToTemperatureButton, !mTemperatureExpandable.isExpanded());
                mTemperatureExpandable.toggle();
                break;

            case R.id.fragment_help_vacation_button:
                updateArrow(mHowToVacationButton, !mVacationExpandable.isExpanded());
                mVacationExpandable.toggle();
                break;
        }
    }

    private void updateArrow(Button b, boolean isExpanded) {

        int arrowId = isExpanded ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down;

        b.setCompoundDrawablesWithIntrinsicBounds(0,0, arrowId, 0);
    }
}
