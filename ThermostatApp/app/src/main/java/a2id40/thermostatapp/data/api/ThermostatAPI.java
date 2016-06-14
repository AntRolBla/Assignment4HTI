package a2id40.thermostatapp.data.api;

import a2id40.thermostatapp.data.models.DayModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by rafael on 6/8/16.
 */

public interface ThermostatAPI {
    @Headers("Accept: application/json")
    //TODO: Implement the thermostat api

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

    @GET("day")
    Call<DayModel> getCurrentDay();
}
