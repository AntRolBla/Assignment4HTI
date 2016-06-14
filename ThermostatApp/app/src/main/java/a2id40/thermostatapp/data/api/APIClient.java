package a2id40.thermostatapp.data.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by rafael on 6/8/16.
 */

public class APIClient {

    private static ThermostatAPI thermostatAPIInterface;

    private static String baseUrl = "http://pcwin889.win.tue.nl/2id40-ws/41/";

    public static ThermostatAPI getClient() {

        if (thermostatAPIInterface == null) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("HH:mm")
                    .create();

            Retrofit client = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            thermostatAPIInterface = client.create(ThermostatAPI.class);
        }

        return thermostatAPIInterface;

    }

}
