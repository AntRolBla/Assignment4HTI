package a2id40.thermostatapp.data.models;

import java.util.Date;

/**
 * Created by rafaelring on 6/14/16.
 */

public class TargetTemperatureModel {

    double target_temperature;

    public TargetTemperatureModel(double targetTemperature) {
        this.target_temperature = targetTemperature;
    }

    public double getTargetTemperature() {
        return target_temperature;
    }

    public void setTargetTemperature(double targetTemperature) {
        this.target_temperature = targetTemperature;
    }
}
