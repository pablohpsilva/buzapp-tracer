package aloeio.buzapp_tracer.app.Models;

import aloeio.buzapp_tracer.app.Interfaces.IBackendJSON;
import aloeio.buzapp_tracer.app.MainActivity;
import aloeio.buzapp_tracer.app.Services.BackgroundService;
import aloeio.buzapp_tracer.app.Utils.HttpUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;

/**
 * Created by pablohenrique on 5/26/15.
 */
public class Bus implements IBackendJSON {
    private String urlPostBusLocation = "http://buzapp-services.aloeio.com/busweb/tracer/receivebus";
    private String urlGetServiceID = "http://buzapp-services.aloeio.com/busweb/tracer/generatedid/";
    private String route;
    private int id = -1;
    private Location location;
    private JSONObject jsonObject;
    private boolean speedNotNull = true;
    private HttpUtils httpUtils;

    public Location getLocation(){
        return location;
    }
    public Bus(String route){
//        if(route != null) {
//            this.route = route + "";
//            this.id = id + 0;
//        }
//        else {
//            this.route = route + "T131";
//            this.id = 25;
//        }
        if(route.equals("")) {
            throw new NullPointerException();
        } else {
            this.route = route;
            this.jsonObject = new JSONObject();
            this.location = new Location("");
            httpUtils = new HttpUtils();
            this.setServiceId();
        }
    }

    public void sendJSON(Location location) throws JSONException, IOException, NullPointerException {
        JSONObject json = this.prepareJSON(location);
        if(json != null && this.id != -1) {
            httpUtils.postRequest(urlPostBusLocation, json);
            System.out.println(json.toString());
//            httpUtils.postGZippedRequest(urlPostBusLocation, json);
            Log.d("Bus",json.toString());
        }
    }


    public int getId(){
        return id;
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
                if(this.speedNotNull) {
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



    private void setServiceId(){
        new Runnable() {
            public void run() {
                try {
                    String result = httpUtils.getGZippedRequest(urlGetServiceID + route);
                    id = Integer.parseInt(result);

                    MainActivity.setIdOnFile(id);
//                    File f1 = new File("id.txt");
//                    FileWriter fr = new FileWriter(f1);
//                    BufferedWriter bw = new BufferedWriter(fr);
//
//                    bw.write(id);
//
//                    bw.close();
//
//                    Log.d("Bus","Salvei o ID");

                    this.finalize();
                } catch (HttpException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Throwable throwable) {
                    System.out.println();
                    System.out.println();
                    System.out.println("Thread failed");
                    System.out.println();
                    System.out.println();
                    throwable.printStackTrace();
                }
            }
        }.run();
    }
}
