package a2id40.thermostatapp.fragments.weekly;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import java.io.IOException;
import java.sql.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import a2id40.thermostatapp.R;
import a2id40.thermostatapp.activities.base.BaseActivity;
import a2id40.thermostatapp.data.api.APIClient;
import a2id40.thermostatapp.data.models.DaysProgramModel;
import a2id40.thermostatapp.data.models.SwitchModel;
import a2id40.thermostatapp.data.models.UpdateResponse;
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

public class WeeklyDayFragment extends android.support.v4.app.Fragment implements View.OnClickListener, TimeslotAdapterInterface {

//    private static final int MAX_NIGHTS_AVAILABLE = 5;
//    private static final int MAX_DAYS_AVAILABLE = 5;

    public static final String WEEK_DAY_BUNDLE = "Week Day Random Value";
    private String[] weekDays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    private int mDay;
    private TimeslotsAdapter mTimeslotsAdapter;
    private ArrayList<TimeslotModel> mTimeslotsArray;
    private Helpers mHelper = new Helpers();
    private boolean mIsInitialSetup= true;

    private int mNumberDayLeft;
    private int mNumberNightLeft;

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
        if (mIsInitialSetup){
            setupData();
            mIsInitialSetup = false;
        } else {
            setupView();
        }
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
                    setNumberOfDaysNightsLeft(mTimeslotsArray);
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

    private void setupView() {
        setupNumberDayNightViewAndButton();
        setupAddTimeslotButton();
        setupButtons();
        setupRecycler();
        mDayOfWeekTextView.setText(String.format("Weekly Program: %s", weekDays[mDay]));
    }

    private void setupNumberDayNightViewAndButton(){
        mNumberDaysLeftTextView.setText(String.format("%d", mNumberDayLeft));
        mNumberNightsLeftTextView.setText(String.format("%d", mNumberNightLeft));
    }

    private void setupAddTimeslotButton(){
        mAddTimeslotButton.setEnabled(true);
        if (mNumberDayLeft < 1){
            mAddTimeslotButton.setEnabled(false);
        } else {
            if (mTimeslotsArray.size() == 1 && mTimeslotsArray.get(0).getmDay()){
                mAddTimeslotButton.setEnabled(false);
            }
        }
    }

    private void setupRecycler(){
        mTimeslotsAdapter = new TimeslotsAdapter(mTimeslotsArray, this, false);
        mTimeslotsRecycler.setAdapter(mTimeslotsAdapter);
        mTimeslotsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupButtons() {
        mAddTimeslotButton.setOnClickListener(this);
    }

    // Called by BaseActivity when returning from AddTimeslotFragment
    public void getTimeslotFromAddTimeslot(TimeslotModel newTimeslotModel){
        ArrayList<TimeslotModel> updatedTimeslotArray = new ArrayList<>();
        ArrayList<SwitchModel> updatedSwitch = new ArrayList<>();

        ((BaseActivity) getActivity()).showLoadingScreen();

        updatedTimeslotArray = updateTimeslotWithAdded(newTimeslotModel); // Updated array with add timeslot
        updatedSwitch = mHelper.convertArrayTimeslotsToArraySwitch(updatedTimeslotArray); // Updated switch
        mWeekProgramModel = mHelper.setSwitchFromWeekDay(mDay, mWeekProgramModel, updatedSwitch); // Update mWeekProgramModel

        Call<UpdateResponse> callUpdateWeekProgramModel = APIClient.getClient().setWeekProgram(mWeekProgramModel);
        callUpdateWeekProgramModel.enqueue(new Callback<UpdateResponse>() {
            @Override
            public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                if (response.isSuccessful() && response.body().isSuccess()){
                    mSwitchesArray = mHelper.getSwitchFromWeekDay(mDay, mWeekProgramModel);
                    mTimeslotsArray = mHelper.convertArraySwitchesToArrayTimeslots(mSwitchesArray);
                    mTimeslotsAdapter.updateTimeslotList(mTimeslotsArray);
                    mTimeslotsAdapter.notifyDataSetChanged();
                    setNumberOfDaysNightsLeft(mTimeslotsArray);
                    setupNumberDayNightViewAndButton();
                    setupAddTimeslotButton();
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
            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                String error = t.getMessage();
                ((BaseActivity) getActivity()).hideLoadingScreen();
                //TODO: handle onFailure
            }
        });
    }

    private ArrayList<TimeslotModel> updateTimeslotWithAdded(TimeslotModel newTimeslotModel){
        ArrayList<TimeslotModel> updatedTimeslotsArray = new ArrayList<>();
        ArrayList<TimeslotModel> arrayOnlyWithDays = new ArrayList<>();
        arrayOnlyWithDays = getOnlyDayTimeslots(mTimeslotsArray);
        Date startTime;
        Date endTime;
        Boolean shouldRemoveBefore = false;
        Boolean shouldRemoveAfter = false;

        if (arrayOnlyWithDays.size() == 0){
            arrayOnlyWithDays.add(newTimeslotModel);
        } else {
            boolean foundPosition = false;

            for (int i = 0; i < arrayOnlyWithDays.size(); i++){
                boolean isBigger;
                // Check if Starting Time of TimeSlot on Array is bigger or equal the newTimeslot starting time
                isBigger = isTimeOneBiggerEqualThanTimeTwo(arrayOnlyWithDays.get(i).getmStarTime(), newTimeslotModel.getmStarTime());
                if (isBigger && !foundPosition ) {
                    foundPosition = true;

                    if (i != 0){
                        // If it starts were the before one ends
                        if (isTheSameTime(mHelper.addOneMinuteOnDate(arrayOnlyWithDays.get(i-1).getmEndTime()), newTimeslotModel.getmStarTime())){
                            startTime = arrayOnlyWithDays.get(i-1).getmStarTime();
                            shouldRemoveBefore = true;
                        } else {
                            startTime = newTimeslotModel.getmStarTime();
                        }
                    } else { //If it should be the first position
                        startTime = newTimeslotModel.getmStarTime();
                    }

                    // If it ends where the next start
                    if (isTheSameTime(mHelper.addOneMinuteOnDate(newTimeslotModel.getmEndTime()), arrayOnlyWithDays.get(i).getmStarTime())){
                        endTime = arrayOnlyWithDays.get(i).getmEndTime();
                        shouldRemoveAfter = true;
                    } else {
                        endTime = newTimeslotModel.getmEndTime();
                    }
                    arrayOnlyWithDays.add(i, new TimeslotModel(startTime,endTime, true));
                    if (shouldRemoveAfter){
                        arrayOnlyWithDays.remove(i+1);
                    }
                    if (shouldRemoveBefore){
                        arrayOnlyWithDays.remove(i-1);
                    }
                }
            }
            if (foundPosition == false){
                // Check if it should merge with the last day timeslot
                int lastPosition = arrayOnlyWithDays.size() - 1;
                if (isTheSameTime(arrayOnlyWithDays.get(lastPosition).getmEndTime(), mHelper.subtractOneMinuteOnDate(newTimeslotModel.getmStarTime()))){
                    startTime = arrayOnlyWithDays.get(lastPosition).getmStarTime();
                    endTime = newTimeslotModel.getmEndTime();
                    arrayOnlyWithDays.remove(lastPosition);
                    arrayOnlyWithDays.add(new TimeslotModel(startTime, endTime, true));
                } else {
                    arrayOnlyWithDays.add(newTimeslotModel);
                }
            }
        }

        updatedTimeslotsArray = createTimeslotsArrayFromOnlyDay(arrayOnlyWithDays);
        return updatedTimeslotsArray;
    }

    private ArrayList<TimeslotModel> updateTimeslotWithRemoved(int position){
        ArrayList<TimeslotModel> updatedTimeslotArray = new ArrayList<>();

        updatedTimeslotArray = copyTimeslotsArray(mTimeslotsArray);
        if (position != 0){
            if (position != updatedTimeslotArray.size() - 1){ // IN the middle
                updatedTimeslotArray.get(position - 1).setmEndTime(updatedTimeslotArray.get(position+1).getmEndTime());
                updatedTimeslotArray.remove(position+1);
                updatedTimeslotArray.remove(position);
            } else { // If it is in the last position
                updatedTimeslotArray.get(position-1).setmEndTime(updatedTimeslotArray.get(position).getmEndTime());
                updatedTimeslotArray.remove(position);
            }
        } else { // If it is in the first position
            if (updatedTimeslotArray.size() == 1){ // If there is only one day timeslot
                updatedTimeslotArray.get(position).setmDay(false);
            } else { //If there are more days timeslots
                updatedTimeslotArray.get(position+1).setmStarTime(updatedTimeslotArray.get(position).getmStarTime());
                updatedTimeslotArray.remove(position);
            }
        }

        return updatedTimeslotArray;
    }

    private ArrayList<TimeslotModel> copyTimeslotsArray(ArrayList<TimeslotModel> timeslotModelArray){
        ArrayList<TimeslotModel> timeslotModelCopyArray = new ArrayList<>();
        Date initialTime = new Date();
        Date endTime = new Date();
        Boolean isDay;

        for (TimeslotModel timeslotModel : timeslotModelArray) {
            initialTime = timeslotModel.getmStarTime();
            endTime = timeslotModel.getmEndTime();
            isDay = timeslotModel.getmDay();
            timeslotModelCopyArray.add(new TimeslotModel(initialTime, endTime, isDay));
        }

        return timeslotModelCopyArray;
    }

    private ArrayList<TimeslotModel> getOnlyDayTimeslots(ArrayList<TimeslotModel> arrayWithAllTimeslots){
        ArrayList<TimeslotModel> arrayOnlyWithDay = new ArrayList<>();

        for (int i = 0; i < arrayWithAllTimeslots.size(); i++){
            if (arrayWithAllTimeslots.get(i).getmDay()){
                arrayOnlyWithDay.add(new TimeslotModel(arrayWithAllTimeslots.get(i).getmStarTime(), arrayWithAllTimeslots.get(i).getmEndTime(), true));
            }
        }
        return arrayOnlyWithDay;
    }

    private ArrayList<TimeslotModel> getOnlyNightTimeslots(ArrayList<TimeslotModel> arrayWithAllTimeslots){
        ArrayList<TimeslotModel> arrayOnlyWithNight = new ArrayList<>();

        for (int i = 0; i < arrayWithAllTimeslots.size(); i++){
            if (!arrayWithAllTimeslots.get(i).getmDay()){
                arrayOnlyWithNight.add(new TimeslotModel(arrayWithAllTimeslots.get(i).getmStarTime(), arrayWithAllTimeslots.get(i).getmEndTime(), false));
            }
        }
        return arrayOnlyWithNight;
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
            completeArray.add(new TimeslotModel(midNightDate, mHelper.subtractOneMinuteOnDate(arrayWithOnlyDay.get(0).getmStarTime()), false));
        }

        int controlLoop = arrayWithOnlyDay.size();
        for (int i = 0; i < controlLoop - 1; i++){
            completeArray.add(new TimeslotModel(arrayWithOnlyDay.get(i).getmStarTime(), arrayWithOnlyDay.get(i).getmEndTime(), true));
            completeArray.add(new TimeslotModel(mHelper.addOneMinuteOnDate(arrayWithOnlyDay.get(i).getmEndTime()), mHelper.subtractOneMinuteOnDate(arrayWithOnlyDay.get(i+1).getmStarTime()), false));
        }
        completeArray.add(new TimeslotModel(arrayWithOnlyDay.get(controlLoop-1).getmStarTime(), arrayWithOnlyDay.get(controlLoop-1).getmEndTime(), true));
        if (!isTheSameTime(elevenFiftyNineDate, arrayWithOnlyDay.get(controlLoop-1).getmEndTime())){
            completeArray.add(new TimeslotModel(mHelper.addOneMinuteOnDate(arrayWithOnlyDay.get(controlLoop-1).getmEndTime()), elevenFiftyNineDate, false));
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

    private boolean isTimeOneBiggerEqualThanTimeTwo(Date timeOne, Date timeTwo){
        boolean isBiggerEqual = false;
        Calendar calendarOne = Calendar.getInstance();
        Calendar calendarTwo = Calendar.getInstance();
        calendarOne.setTime(timeOne);
        calendarTwo.setTime(timeTwo);

        if ((calendarOne.get(Calendar.HOUR_OF_DAY) > calendarTwo.get(Calendar.HOUR_OF_DAY))){
            isBiggerEqual = true;
        } else {
            if ((calendarOne.get(Calendar.HOUR_OF_DAY) == calendarTwo.get(Calendar.HOUR_OF_DAY))){
                if (calendarOne.get(Calendar.MINUTE) >= calendarTwo.get(Calendar.MINUTE)) {
                    isBiggerEqual = true;
                }
            }
        }
        return isBiggerEqual;
    }

    private void setNumberOfDaysNightsLeft(ArrayList<TimeslotModel> timeslotModelArray){
        int numberOfDaysLeft = 5;
        int numberOfNightsLeft = 5;

        if (!timeslotModelArray.get(0).getmDay()){
            numberOfNightsLeft++;
        }
        for (TimeslotModel timeslot : timeslotModelArray) {
            if (timeslot.getmDay()){
                numberOfDaysLeft--;
            } else {
                numberOfNightsLeft--;
            }
        }

        mNumberDayLeft = numberOfDaysLeft;
        mNumberNightLeft = numberOfDaysLeft;
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.fragment_weekly_day_add_timeslot_button:
                ArrayList<Timepoint[]> availableTimes = createAvailableHoursTimepointArray(mTimeslotsArray);
                Timepoint[] availableTimesStart = availableTimes.get(0);
                Timepoint[] availableTimesEnd = availableTimes.get(1);
                ((BaseActivity) getActivity()).openAddTimeslots(mDay, mNumberDayLeft, availableTimesStart, availableTimesEnd);
                break;
        }
    }

    private ArrayList<Timepoint[]> createAvailableHoursTimepointArray(ArrayList<TimeslotModel> timeslotsModelArray){
        ArrayList<TimeslotModel> arrayOnlyWithNight = new ArrayList<>();
        ArrayList<Timepoint> timePointsArrayListStart = new ArrayList<>();
        ArrayList<Timepoint> timePointsArrayListEnd = new ArrayList<>();
        Calendar temp = Calendar.getInstance();
        int initialHour;
        int initialMinute;
        int finalHour;
        int finalMinute;

        arrayOnlyWithNight = getOnlyNightTimeslots(timeslotsModelArray);

        for (TimeslotModel timeslot : arrayOnlyWithNight) {
            temp.setTime(timeslot.getmStarTime());
            initialHour = temp.get(Calendar.HOUR_OF_DAY);
            initialMinute = temp.get(Calendar.MINUTE);
            temp.setTime(timeslot.getmEndTime());
            finalHour = temp.get(Calendar.HOUR_OF_DAY);
            finalMinute = temp.get(Calendar.MINUTE);

            // If it doesnt start and end on the same hours
            if (initialHour != finalHour){

                // For the initialHour - add from initialM to 59
                for (int min = initialMinute; min < 60; min++){
                    timePointsArrayListEnd.add(new Timepoint(initialHour, min));
                    timePointsArrayListStart.add(new Timepoint(initialHour, min));
                }
                // For the hours between initial and final - add all minutes
                for (int hour = initialHour+1; hour < finalHour; hour++){
                    int min = 0;
                    while (min < 60){
                        timePointsArrayListStart.add(new Timepoint(hour, min));
                        timePointsArrayListEnd.add(new Timepoint(hour, min));
                        min++;
                    }
                }
                // For the final hour - add until final minute
                for (int min = 0; min < finalMinute; min++){
                    timePointsArrayListStart.add(new Timepoint(finalHour, min));
                    timePointsArrayListEnd.add(new Timepoint(finalHour, min));
                }
                timePointsArrayListEnd.add(new Timepoint(finalHour, finalMinute));

            } else { // Should only add the minutes between the hours
                for (int min = initialMinute; min < finalMinute; min++){
                    timePointsArrayListEnd.add(new Timepoint(finalHour, min));
                    timePointsArrayListStart.add(new Timepoint(finalHour, min));
                }
                timePointsArrayListEnd.add(new Timepoint(finalHour, finalMinute));
            }
        }

        Timepoint[] timePointArrayEnd = new Timepoint[timePointsArrayListEnd.size()];
        timePointArrayEnd = timePointsArrayListEnd.toArray(timePointArrayEnd);
        Timepoint[] timePointArrayStart = new Timepoint[timePointsArrayListStart.size()];
        timePointArrayStart = timePointsArrayListStart.toArray(timePointArrayStart);

        ArrayList<Timepoint[]> bothArrays = new ArrayList<>();
        bothArrays.add(timePointArrayStart);
        bothArrays.add(timePointArrayEnd);

        return bothArrays;
    }

    @Override
    public void removeTimeslotClicked(int position) {
        ArrayList<TimeslotModel> updatedTimeslotWithRemovedArray = new ArrayList<>();
        ArrayList<SwitchModel> updatedWithRemovedSwitch = new ArrayList<>();

        ((BaseActivity) getActivity()).showLoadingScreen();

        updatedTimeslotWithRemovedArray = updateTimeslotWithRemoved(position); // Get array without position selected
        updatedWithRemovedSwitch = mHelper.convertArrayTimeslotsToArraySwitch(updatedTimeslotWithRemovedArray); // Get array of switches updated
        mHelper.setSwitchFromWeekDay(mDay, mWeekProgramModel, updatedWithRemovedSwitch); // Update week program model

        // Put new program model
        Call<UpdateResponse> callUpdateWeekProgramModel = APIClient.getClient().setWeekProgram(mWeekProgramModel);
        callUpdateWeekProgramModel.enqueue(new Callback<UpdateResponse>() {
            @Override
            public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                // In case of success, update mTimslotArray, mSwitchesArray, mTimeslotAdapter
                if (response.isSuccessful() && response.body().isSuccess()){
                    mSwitchesArray = mHelper.getSwitchFromWeekDay(mDay, mWeekProgramModel);
                    mTimeslotsArray = mHelper.convertArraySwitchesToArrayTimeslots(mSwitchesArray);
                    mTimeslotsAdapter.updateTimeslotList(mTimeslotsArray);
                    mTimeslotsAdapter.notifyDataSetChanged();
                    setNumberOfDaysNightsLeft(mTimeslotsArray);
                    setupNumberDayNightViewAndButton();
                    setupAddTimeslotButton();
                    ((BaseActivity) getActivity()).hideLoadingScreen();
                } else { // With error: Undo modification on mWeekProgram using mTimeslotArray
                    ((BaseActivity) getActivity()).hideLoadingScreen();
                    try {
                        String onResponse = response.errorBody().string();
                        //TODO: handle notSuccessful
                    } catch (IOException e){
                        //TODO: handle exception e
                    }
                }
            }

            // With error: Undo modification on mWeekProgram using mTimeslotArray
            @Override
            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                String error = t.getMessage();
                ((BaseActivity) getActivity()).hideLoadingScreen();
                //TODO: handle onFailure
            }
        });
    }
}
