package a2id40.thermostatapp.data.api;

import a2id40.thermostatapp.data.models.DayModel;
import a2id40.thermostatapp.data.models.DayTemperatureModel;
import a2id40.thermostatapp.data.models.NightTemperatureModel;
import a2id40.thermostatapp.data.models.TargetTemperatureModel;
import a2id40.thermostatapp.data.models.TemperatureModel;
import a2id40.thermostatapp.data.models.TimeModel;
import a2id40.thermostatapp.data.models.UpdateResponse;
import a2id40.thermostatapp.data.models.WeekProgramModel;
import a2id40.thermostatapp.data.models.WeekProgramState;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by rafael on 6/8/16.
 */

public interface ThermostatAPI {
//    @GET("example/{pathParam}")
//    Call<Model> getExample(
//            @Path("pathParam") String param
//    );

//    @GET("example/list")
//    Call<Model> getExample2(
//            @Query("param") String param
//    );

//    @POST("example/new")
//    Call<Model> saveExample(
//            @Body Model data
//    );
    @Headers("Accept: application/json")
    @GET("day")
    Call<DayModel> getCurrentDay();

    @Headers("Accept: application/json")
    @GET("time")
    Call<TimeModel> getCurrentTime();

    @Headers("Accept: application/json")
    @GET("currentTemperature")
    Call<TemperatureModel> getCurrentTemperature();

    @Headers("Accept: application/json")
    @GET("targetTemperature")
    Call<TargetTemperatureModel> getTargetTemperature();

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"})
    @PUT("targetTemperature")
    Call<UpdateResponse> setTargetTemperature(
            @Body TargetTemperatureModel targetTemperatureModel
    );

    @Headers("Accept: application/json")
    @GET("dayTemperature")
    Call<DayTemperatureModel> getDayTemperature();

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"})
    @PUT("dayTemperature")
    Call<UpdateResponse> setDayTemperature(
            @Body DayTemperatureModel dayTemperatureModel
    );

    @Headers("Accept: application/json")
    @GET("nightTemperature")
    Call<NightTemperatureModel> getNightTemperature();

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"})
    @PUT("nightTemperature")
    Call<UpdateResponse> setNightTemperature(
            @Body NightTemperatureModel nightTemperatureModel
    );

    @Headers("Accept: application/json")
    @GET("weekProgramState")
    Call<WeekProgramState> getWeekProgramState();

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"})
    @PUT("weekProgramState")
    Call<UpdateResponse> setWeekProgramState(
            @Body WeekProgramState weekProgramState
    );

    @Headers("Accept: application/json")
    @GET("weekProgram")
    Call<WeekProgramModel> getWeekProgram();

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"})
    @GET("weekProgram")
    Call<UpdateResponse> setWeekProgram(
            @Body WeekProgramModel weekProgramModel
    );
}
