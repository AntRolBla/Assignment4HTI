package a2id40.thermostatapp.fragments.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import a2id40.thermostatapp.data.models.SwitchModel;
import a2id40.thermostatapp.data.models.WeekProgramModel;
import a2id40.thermostatapp.fragments.weekly.Models.TimeslotModel;

/**
 * Created by IsabelGomes on 14/06/16.
 */
public class Helpers {

    public Helpers() {}

    public ArrayList<SwitchModel> getSwitchFromWeekDay(int weekDay, WeekProgramModel weekProgramModel){
        ArrayList<SwitchModel> switchModelArrayList = new ArrayList<>();
        switch (weekDay){
            case 0:
                switchModelArrayList = weekProgramModel.getWeekProgram().getDays().getMonday().getSwitches();
                break;
            case 1:
                switchModelArrayList = weekProgramModel.getWeekProgram().getDays().getTuesday().getSwitches();
                break;
            case 2:
                switchModelArrayList = weekProgramModel.getWeekProgram().getDays().getWednesday().getSwitches();
                break;
            case 3:
                switchModelArrayList = weekProgramModel.getWeekProgram().getDays().getThursday().getSwitches();
                break;
            case 4:
                switchModelArrayList = weekProgramModel.getWeekProgram().getDays().getFriday().getSwitches();
                break;
            case 5:
                switchModelArrayList = weekProgramModel.getWeekProgram().getDays().getSaturday().getSwitches();
                break;
            case 6:
                switchModelArrayList = weekProgramModel.getWeekProgram().getDays().getSunday().getSwitches();
                break;
        }
        return switchModelArrayList;
    }

    public WeekProgramModel setSwitchFromWeekDay(int day, WeekProgramModel weekProgramModel, ArrayList<SwitchModel> switchesArray){
        switch (day){
            case 0:
                weekProgramModel.getWeekProgram().getDays().getMonday().setSwitches(switchesArray);
                break;
            case 1:
                weekProgramModel.getWeekProgram().getDays().getTuesday().setSwitches(switchesArray);
                break;
            case 2:
                weekProgramModel.getWeekProgram().getDays().getWednesday().setSwitches(switchesArray);
                break;
            case 3:
                weekProgramModel.getWeekProgram().getDays().getThursday().setSwitches(switchesArray);
                break;
            case 4:
                weekProgramModel.getWeekProgram().getDays().getFriday().setSwitches(switchesArray);
                break;
            case 5:
                weekProgramModel.getWeekProgram().getDays().getSaturday().setSwitches(switchesArray);
                break;
            case 6:
                weekProgramModel.getWeekProgram().getDays().getSunday().setSwitches(switchesArray);
                break;
        }
        return weekProgramModel;
    }

    public ArrayList<TimeslotModel> convertArraySwitchesToArrayTimeslots(ArrayList<SwitchModel> switchModelArray){
        ArrayList<TimeslotModel> timeslotModelArray = new ArrayList<>();

        Calendar midNightCalendar = Calendar.getInstance();
        midNightCalendar.set(2016, 5, 5, 0, 0);
        Calendar elevenFiftyNineCalendar = Calendar.getInstance();
        elevenFiftyNineCalendar.set(2016, 5, 5, 23, 59);
        Calendar tempSwitchCalendar = Calendar.getInstance();

        Date initialTime = new Date();
        Date endTime = new Date();
        Boolean isDay;

        int controlLoop = switchModelArray.size();
        int controlInside = 0;
        for (int i = 0; i < controlLoop; i++){
            if (!switchModelArray.get(controlInside).isOn()){
                switchModelArray.remove(controlInside);
            } else {
                controlInside++;
            }
        }

        if (switchModelArray.size() == 0){ // If all switches are off
            initialTime = midNightCalendar.getTime();
            endTime = elevenFiftyNineCalendar.getTime();
            isDay = false;
            TimeslotModel timeslot = new TimeslotModel(initialTime, endTime, isDay);
            timeslotModelArray.add(timeslot);

        } else { // If there is at least one switch on
            tempSwitchCalendar.setTimeInMillis(switchModelArray.get(0).getTime().getTime()); // Get the hour of first switch

            if (tempSwitchCalendar.get(Calendar.HOUR_OF_DAY) == 0 && tempSwitchCalendar.get(Calendar.MINUTE) == 0){ // If the first switch is at midnight
                initialTime = tempSwitchCalendar.getTime();
                isDay = !switchModelArray.get(0).isNight();
                switchModelArray.remove(0);
            } else { // If doesnt start at midnight, we still need to put a night starting at midnight
                initialTime = midNightCalendar.getTime();
                isDay = false;
            }
            for (SwitchModel switchModel : switchModelArray) {
                endTime = subtractOneMinuteOnDate(switchModel.getTime());
                TimeslotModel timeslotTemp = new TimeslotModel(initialTime, endTime, isDay);
                timeslotModelArray.add(timeslotTemp);
                initialTime = switchModel.getTime();
                isDay = !switchModel.isNight();
            }
            endTime = elevenFiftyNineCalendar.getTime();
            TimeslotModel timeslot = new TimeslotModel(initialTime, endTime, isDay);
            timeslotModelArray.add(timeslot);
        }
        return timeslotModelArray;
    }

    public Date subtractOneMinuteOnDate(Date endTime){
        Date tempDate = new Date();
        long miliseconds = endTime.getTime();
        miliseconds = miliseconds - 60000;
        tempDate.setTime(miliseconds);
        return tempDate;
    }

    public Date addOneMinuteOnDate(Date endTime){
        Date tempDate = new Date();
        long miliseconds = endTime.getTime();
        miliseconds = miliseconds + 60000;
        tempDate.setTime(miliseconds);
        return tempDate;
    }

    public ArrayList<SwitchModel> convertArrayTimeslotsToArraySwitch(ArrayList<TimeslotModel> timeslotModelArray){
        ArrayList<SwitchModel> switchModelArray = new ArrayList<>();
        int daysLeft = 5;
        int nightsLeft = 5;

        Calendar midNightCalendar = Calendar.getInstance();
        midNightCalendar.set(2016, 5, 5, 0, 0);
        Date midnightDate = new Date();
        midnightDate = midNightCalendar.getTime();

        if (timeslotModelArray.size() != 1){
            int iterator = 0;
            if (!timeslotModelArray.get(0).getmDay()){
                iterator = 1;
            }
            for (int i = iterator; i < timeslotModelArray.size(); i++){
                if (timeslotModelArray.get(i).getmDay()){
                    switchModelArray.add(new SwitchModel(false, true, timeslotModelArray.get(i).getmStarTime()));
                    daysLeft--;
                } else {
                    switchModelArray.add(new SwitchModel(true, true, timeslotModelArray.get(i).getmStarTime()));
                    nightsLeft--;
                }
            }
        }
        while (daysLeft != 0){
            switchModelArray.add(new SwitchModel(false, false, midnightDate));
            daysLeft--;
        }
        while (nightsLeft != 0){
            switchModelArray.add(new SwitchModel(true, false, midnightDate));
            nightsLeft--;
        }

        return switchModelArray;
    }

}
