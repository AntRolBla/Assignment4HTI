package a2id40.thermostatapp.data.models;

/**
 * Created by rafaelring on 6/14/16.
 */

public class UpdateResponse {

    String response;

    public boolean isSuccess() {
        if (response.equals("OK")) {
            return true;
        }
        return false;
    }

}
