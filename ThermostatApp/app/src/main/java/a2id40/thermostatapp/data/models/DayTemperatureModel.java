package a2id40.thermostatapp.data.models;

/**
 * Created by rafaelring on 6/14/16.
 */

public class DayTemperatureModel {

    double day_temperature;

    public DayTemperatureModel(double dayTemperature) {
        this.day_temperature = dayTemperature;
    }

    public double getDayTemperature() {
        return day_temperature;
    }

    public void setDayTemperature(double dayTemperature) {
        this.day_temperature = dayTemperature;
    }
}
