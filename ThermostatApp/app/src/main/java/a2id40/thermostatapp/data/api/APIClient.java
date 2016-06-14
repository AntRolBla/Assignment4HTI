package a2id40.thermostatapp.data.api;

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
            Retrofit client = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            thermostatAPIInterface = client.create(ThermostatAPI.class);
        }

        return thermostatAPIInterface;

    }

}
