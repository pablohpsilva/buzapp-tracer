package aloeio.buzapp_tracer.app.Models;

import aloeio.buzapp_tracer.app.Utils.HttpUtils;

import android.location.Location;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by pablohenrique on 5/26/15.
 */
public class Bus {
    private String urlPostBusLocation = "http://buzapp-services.aloeio.com/busweb/tracer/receivebus";
    private String urlGetServiceID = "http://buzapp-services.aloeio.com/busweb/tracer/generatedid/";
    private String route;
    private String plate;
//    private Location location;
    private JSONObject jsonObject;
    private HttpUtils httpUtils;

    public Bus(String route, String plate){
        this.route = route;
        this.plate = plate;
        this.jsonObject = new JSONObject();
        this.httpUtils = new HttpUtils();
    }

    public void sendJSON(Location location) throws JSONException, IOException, NullPointerException {
        this.myPrepareJSON(location);
        httpUtils.postRequest(urlPostBusLocation, this.jsonObject);
        System.out.println(this.jsonObject.toString());
        Log.d("Bus", this.jsonObject.toString());
    }

    public void myPrepareJSON(Location location) throws JSONException {
        this.jsonObject.put("linha", this.route);
        this.jsonObject.put("placa", this.plate);
        this.jsonObject.put("velocity", location.getSpeed());
        this.jsonObject.put("latitude", location.getLatitude());
        this.jsonObject.put("longitude", location.getLongitude());
//        return this.jsonObject;
    }
}
