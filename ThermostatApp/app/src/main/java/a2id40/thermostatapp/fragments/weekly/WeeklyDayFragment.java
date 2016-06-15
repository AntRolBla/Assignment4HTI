package a2id40.thermostatapp.fragments.weekly;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import a2id40.thermostatapp.R;
import a2id40.thermostatapp.activities.base.BaseActivity;
import a2id40.thermostatapp.data.api.APIClient;
import a2id40.thermostatapp.data.models.DaysProgramModel;
import a2id40.thermostatapp.data.models.SwitchModel;
import a2id40.thermostatapp.data.models.WeekProgram;
import a2id40.thermostatapp.data.models.WeekProgramModel;
import a2id40.thermostatapp.fragments.Utils.Helpers;
import a2id40.thermostatapp.fragments.weekly.Models.TimeslotModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by rafaelring on 6/9/16.
 */

public class WeeklyDayFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

//    private static final int MAX_NIGHTS_AVAILABLE = 5;
//    private static final int MAX_DAYS_AVAILABLE = 5;

    public static final String WEEK_DAY_BUNDLE = "Week Day Random Value";
    private String[] weekDays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    private int mDay;
    private TimeslotsAdapter mTimeslotsAdapter;
    private int mNumberSunLeft = 5;
    private ArrayList<TimeslotModel> mTimeslotsArray;
    private Helpers helper = new Helpers();

    // Variable to store data from server
    private WeekProgramModel mWeekProgramModel;
    private ArrayList<SwitchModel> mSwitchesArray;

    //region View Components

    @BindView(R.id.fragment_weekly_day_timeslots_recycler)
    android.support.v7.widget.RecyclerView mTimeslotsRecycler;

    @BindView(R.id.fragment_weekly_day_days_left_textview)
    TextView mNumberDaysLeftTextView;

    @BindView(R.id.fragment_weekly_day_nights_left_textview)
    TextView mNumberNightsLeftTextView;

    @BindView(R.id.fragment_weekly_day_week_day_textview)
    TextView mDayOfWeekTextView;

    @BindView(R.id.fragment_weekly_day_add_timeslot_button)
    Button mAddTimeslotButton;

    //endregion

    public static WeeklyDayFragment newInstance() {return new WeeklyDayFragment(); }

    public WeeklyDayFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root  = inflater.inflate(R.layout.fragment_weekly_day, container, false);
        ButterKnife.bind(this, root);
        Bundle weekDayBundle = this.getArguments();
        mDay = weekDayBundle.getInt(WEEK_DAY_BUNDLE);
        setupData();

        return root;
    }

    private void setupData(){
        Call<WeekProgramModel> callWeekProgramModel = APIClient.getClient().getWeekProgram();
        callWeekProgramModel.enqueue(new Callback<WeekProgramModel>() {
            @Override
            public void onResponse(Call<WeekProgramModel> call, Response<WeekProgramModel> response) {
                if (response.isSuccessful()){
                    mWeekProgramModel = response.body();
                    mSwitchesArray = helper.getSwitchFromWeekDay(mDay, mWeekProgramModel);
                    mTimeslotsArray = helper.convertArraySwitchesToArrayTimeslots(mSwitchesArray);
                    setupView();
                } else {
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
                //TODO: handle onFailure
            }
        });
    }

    private void setupView() {
        setupButtons();
        setupRecycler();
        mDayOfWeekTextView.setText(String.format("Weekly Program: %s", weekDays[mDay]));
    }

    private void setupRecycler(){
        mTimeslotsAdapter = new TimeslotsAdapter(mTimeslotsArray);
        mTimeslotsRecycler.setAdapter(mTimeslotsAdapter);
        mTimeslotsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupButtons() {
        mAddTimeslotButton.setOnClickListener(this);
    }

    public void getTimeslotFromAddTimeslot(TimeslotModel newTimeslotModel){
        Calendar tempCalendar = Calendar.getInstance();
        Calendar newTimeslotCalendar = Calendar.getInstance();
        newTimeslotCalendar.setTime(newTimeslotModel.getmStarTime());

        // TESTs
        Calendar elevenFiftyNineCalendar = Calendar.getInstance();
        elevenFiftyNineCalendar.set(2016, 5, 5, 23, 59);
        Date elevenFiftyNineDate = elevenFiftyNineCalendar.getTime();

        boolean isTheSame = isTheSameTime(newTimeslotModel.getmEndTime(), elevenFiftyNineDate);
        boolean isTheSame2 = isTheSameTime(newTimeslotModel.getmStarTime(), elevenFiftyNineDate);



        // END TESTs

        boolean foundPosition = false;
        for (int i = 0; i < mTimeslotsArray.size(); i++){
            tempCalendar.setTime(mTimeslotsArray.get(i).getmStarTime());
            if ((tempCalendar.get(Calendar.HOUR_OF_DAY) >= newTimeslotCalendar.get(Calendar.HOUR_OF_DAY)) && (tempCalendar.get(Calendar.MINUTE) >= newTimeslotCalendar.get(Calendar.MINUTE)) && !foundPosition ) {
                foundPosition = true;

            }
        }
        if (foundPosition == false){

        }
    }

    private ArrayList<TimeslotModel> getOnlyDayTimeslots(ArrayList<TimeslotModel> arrayWithAllTimeslots){
        ArrayList<TimeslotModel> arrayOnlyWithDay;
        arrayOnlyWithDay = arrayWithAllTimeslots;

        int controlLoop = arrayWithAllTimeslots.size();
        int controlInside = 0;
        for (int i = 0; i < controlLoop; i++){
            if (!arrayOnlyWithDay.get(controlInside).getmDay()){
                arrayOnlyWithDay.remove(controlInside);
            } else {
                controlInside++;
            }
        }
        return arrayOnlyWithDay;
    }

    private ArrayList<TimeslotModel> createTimeslotsArrayFromOnlyDay(ArrayList<TimeslotModel> arrayWithOnlyDay){
        ArrayList<TimeslotModel> completeArray = new ArrayList<>();

        Calendar midNightCalendar = Calendar.getInstance();
        midNightCalendar.set(2016, 5, 5, 0, 0);
        Date midNightDate = midNightCalendar.getTime();

        Calendar elevenFiftyNineCalendar = Calendar.getInstance();
        elevenFiftyNineCalendar.set(2016, 5, 5, 23, 59);
        Date elevenFiftyNineDate = elevenFiftyNineCalendar.getTime();

        if (!isTheSameTime(arrayWithOnlyDay.get(0).getmStarTime(), midNightDate)){
            completeArray.add(new TimeslotModel(midNightDate, arrayWithOnlyDay.get(0).getmStarTime(), false));
        }

        int controlLoop = arrayWithOnlyDay.size();
        for (int i = 0; i < controlLoop - 1; i++){
            completeArray.add(new TimeslotModel(arrayWithOnlyDay.get(i).getmStarTime(), arrayWithOnlyDay.get(i).getmEndTime(), true));
            completeArray.add(new TimeslotModel(arrayWithOnlyDay.get(i).getmEndTime(), arrayWithOnlyDay.get(i+1).getmStarTime(), false));
        }
        completeArray.add(new TimeslotModel(arrayWithOnlyDay.get(controlLoop-1).getmStarTime(), arrayWithOnlyDay.get(controlLoop-1).getmEndTime(), true));
        if (!isTheSameTime(elevenFiftyNineDate, arrayWithOnlyDay.get(controlLoop-1).getmEndTime())){
            completeArray.add(new TimeslotModel(arrayWithOnlyDay.get(controlLoop-1).getmEndTime(), elevenFiftyNineDate, false));
        }

        return completeArray;
    }

    private boolean isTheSameTime(Date timeOne, Date timeTwo){
        boolean isTheSame;
        Calendar calendarOne = Calendar.getInstance();
        Calendar calendarTwo = Calendar.getInstance();
        calendarOne.setTime(timeOne);
        calendarTwo.setTime(timeTwo);

        if ((calendarOne.get(Calendar.HOUR_OF_DAY) == calendarTwo.get(Calendar.HOUR_OF_DAY)) && (calendarOne.get(Calendar.MINUTE) == calendarTwo.get(Calendar.MINUTE))){
            isTheSame = true;
        } else {
            isTheSame = false;
        }
        return isTheSame;
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.fragment_weekly_day_add_timeslot_button:
                ((BaseActivity) getActivity()).openAddTimeslots(mDay, mNumberSunLeft);
                break;
        }
    }

//    public ArrayList<TimeslotModel> createFalseData(){
//        ArrayList<TimeslotModel> data = new ArrayList<>();
//        data.add(new TimeslotModel("07:00 - 09:00", true));
//        data.add(new TimeslotModel("09:00 - 11:00", false));
//        data.add(new TimeslotModel("11:00 - 15:00", true));
//        data.add(new TimeslotModel("15:00 - 17:00", false));
//        return data;
//    }
}
