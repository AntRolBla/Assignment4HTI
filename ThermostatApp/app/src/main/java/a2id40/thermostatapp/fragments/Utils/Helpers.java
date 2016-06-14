package a2id40.thermostatapp.fragments.Utils;

import java.util.ArrayList;

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

    public void setSwitchFromWeekDay(int day, WeekProgramModel weekProgramModel, ArrayList<SwitchModel> switchesArray){
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
    }

    public ArrayList<TimeslotModel> convertArraySwitchesToArrayTimeslots(ArrayList<SwitchModel> switchModel){
        ArrayList<TimeslotModel> timeslotModel = new ArrayList<>();
        return timeslotModel;
    }

    public ArrayList<SwitchModel> convertArrayTimeslotsToArraySwitch(ArrayList<TimeslotModel> timeslotModel){
        ArrayList<SwitchModel> switchModel = new ArrayList<>();
        return switchModel;
    }

}
