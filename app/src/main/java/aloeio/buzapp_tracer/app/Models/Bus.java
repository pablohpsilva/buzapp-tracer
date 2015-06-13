package aloeio.buzapp_tracer.app.Models;

import aloeio.buzapp_tracer.app.Interfaces.IBackendJSON;
import aloeio.buzapp_tracer.app.Utils.HttpAsync;
import aloeio.buzapp_tracer.app.Utils.HttpUtils;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;

/**
 * Created by pablohenrique on 5/26/15.
 */
public class Bus implements IBackendJSON {
    private String URL = "http://54.69.229.42:8080/busweb/receivebus";
    private String route;
    private int id;
    private Location location;
    private JSONObject jsonObject;
    private boolean speedNotNull = true;
    private HttpUtils httpUtils;

    public Bus(){
        this(null,-1);
    }

    public Bus(String route, int id){
        if(route != null && id != -1) {
            this.route = route + "";
            this.id = id + 0;
        }
        else {
            this.route = route + "T131";
            this.id = 25;
        }
        this.jsonObject = new JSONObject();
        this.location = new Location("");
        httpUtils = new HttpUtils();
    }

    public void sendJSON(Location location) throws JSONException, IOException, NullPointerException {
        JSONObject json = this.prepareJSON(location);
        if(json != null) {
            httpUtils.postRequest(URL, json);
            Log.d("Bus",json.toString());
        }
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        this.jsonObject.put("linha", this.route);
        this.jsonObject.put("id", this.id);
        this.jsonObject.put("velocity", this.location.getSpeed());
        this.jsonObject.put("latitude", this.location.getLatitude());
        this.jsonObject.put("longitude", this.location.getLongitude());
        return this.jsonObject;
    }

    public String JSONToString() throws JSONException{
        return this.toJSON().toString();
    }

    private JSONObject prepareJSON(Location location) throws JSONException{
        if(verifySameGeoPositions(location)) {
            if(!verifyNullSpeed(location)) {
                if(this.speedNotNull){
                    this.speedNotNull = false;
                    return this.toJSON();
                }
            }
        } else {
            if(!verifyNullSpeed(location)) {
                if(this.speedNotNull){
                    this.speedNotNull = false;
                    return this.toJSON();
                }
            } else {
                this.speedNotNull = true;
                this.location = location;
                return this.toJSON();
            }
        }
        return null;
    }

    private boolean verifySameGeoPositions(Location location){
        return (this.location.getLatitude() == location.getLatitude() && this.location.getLongitude() == location.getLongitude());
    }

    private boolean verifyNullSpeed(Location location){
        return (location.getSpeed() > 0.0);
    }
}
