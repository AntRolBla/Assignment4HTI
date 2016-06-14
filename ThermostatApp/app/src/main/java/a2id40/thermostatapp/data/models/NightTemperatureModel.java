package a2id40.thermostatapp.data.models;

/**
 * Created by rafaelring on 6/14/16.
 */

public class NightTemperatureModel {

    double night_temperature;

    public NightTemperatureModel(double nightTemperature) {
        this.night_temperature = nightTemperature;
    }

    public double getNightTemperature() {
        return night_temperature;
    }

    public void setNightTemperature(double nightTemperature) {
        this.night_temperature = nightTemperature;
    }
}
