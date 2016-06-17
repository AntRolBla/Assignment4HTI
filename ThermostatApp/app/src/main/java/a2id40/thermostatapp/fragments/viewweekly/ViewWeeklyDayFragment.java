package a2id40.thermostatapp.fragments.viewweekly;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import a2id40.thermostatapp.R;
import a2id40.thermostatapp.activities.base.BaseActivity;
import a2id40.thermostatapp.data.api.APIClient;
import a2id40.thermostatapp.data.models.SwitchModel;
import a2id40.thermostatapp.data.models.WeekProgramModel;
import a2id40.thermostatapp.fragments.Utils.Helpers;
import a2id40.thermostatapp.fragments.weekly.Models.TimeslotModel;
import a2id40.thermostatapp.fragments.weekly.TimeslotAdapterInterface;
import a2id40.thermostatapp.fragments.weekly.TimeslotsAdapter;
import a2id40.thermostatapp.fragments.weekly.WeeklyDayFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by IsabelGomes on 15/06/16.
 */
public class ViewWeeklyDayFragment extends android.support.v4.app.Fragment implements TimeslotAdapterInterface {
    private String[] weekDays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private int mDay;
    private TimeslotsAdapter mTimeslotsAdapter;
    private ArrayList<TimeslotModel> mTimeslotsArray;
    private Helpers mHelper = new Helpers();

    // Variable to store data from server
    private WeekProgramModel mWeekProgramModel;
    private ArrayList<SwitchModel> mSwitchesArray;

    //region View Components

    @BindView(R.id.fragment_viewweekly_day_timeslots_recycler)
    android.support.v7.widget.RecyclerView mTimeslotsRecycler;

    @BindView(R.id.fragment_viewweekly_day_days_left_textview)
    TextView mNumberDaysLeftTextView;

    @BindView(R.id.fragment_viewweekly_day_nights_left_textview)
    TextView mNumberNightsLeftTextView;

    @BindView(R.id.fragment_viewweekly_day_week_day_textview)
    TextView mDayOfWeekTextView;

    //endregion

    public static ViewWeeklyDayFragment newInstance() {return new ViewWeeklyDayFragment();}
    public ViewWeeklyDayFragment() {}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root  = inflater.inflate(R.layout.fragment_viewweekly_day, container, false);
        ButterKnife.bind(this, root);
        Bundle weekDayBundle = this.getArguments();
        mDay = weekDayBundle.getInt(WeeklyDayFragment.WEEK_DAY_BUNDLE);
        setupData();

        return root;
    }

    private void setupData(){
        ((BaseActivity) getActivity()).showLoadingScreen();

        Call<WeekProgramModel> callWeekProgramModel = APIClient.getClient().getWeekProgram();
        callWeekProgramModel.enqueue(new Callback<WeekProgramModel>() {
            @Override
            public void onResponse(Call<WeekProgramModel> call, Response<WeekProgramModel> response) {
                if (response.isSuccessful()){
                    mWeekProgramModel = response.body();
                    mSwitchesArray = mHelper.getSwitchFromWeekDay(mDay, mWeekProgramModel);
                    mTimeslotsArray = mHelper.convertArraySwitchesToArrayTimeslots(mSwitchesArray);
                    setupView();
                    ((BaseActivity) getActivity()).hideLoadingScreen();
                } else {
                    ((BaseActivity) getActivity()).hideLoadingScreen();
                    try {
                        String onResponse = response.errorBody().string();
                        //TODO: handle notSuccessful
                    } catch (IOException e){
                        //TODO: handle exception e
                    }
                }
            }

            @Override
            public void onFailure(Call<WeekProgramModel> call, Throwable t) {
                String error = t.getMessage();
                ((BaseActivity) getActivity()).hideLoadingScreen();
                //TODO: handle onFailure
            }
        });
    }

    private void setupView(){
        setupRecycler();
        mDayOfWeekTextView.setText(String.format("Weekly Program: %s", weekDays[mDay]));
    }

    private void setupRecycler(){
        mTimeslotsAdapter = new TimeslotsAdapter(mTimeslotsArray, this, true);
        mTimeslotsRecycler.setAdapter(mTimeslotsAdapter);
        mTimeslotsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void removeTimeslotClicked(int position) {

    }
}
