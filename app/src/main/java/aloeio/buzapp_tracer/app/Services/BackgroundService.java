package aloeio.buzapp_tracer.app.Services;

import aloeio.buzapp_tracer.app.Models.DeviceInfo;
import aloeio.buzapp_tracer.app.Utils.GCMConstants;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

import aloeio.buzapp_tracer.app.Services.Overrides.MyLocationProvider;

/**
 * Created by root on 05/09/15.
 */
public class BackgroundService
        extends Service {

    public static final String BROADCAST_ACTION = "Service Back";
    private static String urlPostBusLocation = "http://buzapp-services.aloeio.com/busweb/tracer/receivebus";
    private static final String urlReportDeviceInfo = "http://buzapp-services.aloeio.com/busweb/tracer/getbus";
    private static final String CODEPAGE = "UTF-8";
    private static final Integer TIMEOUT = 6500;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static String route;
    private static String myId;
    private static MyLocationProvider myLocationProvider;
    private static Location myLocation;
    private static int timerCounter = 0;
    private boolean hasStoped = false;
    private static String CLASS_NAME;
    private static TelephonyManager tManager;
    private static WifiManager wManager;
    private static AccountManager manager;
    private static Context context;
    static final int READ_BLOCK_SIZE = 100;

    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;

    Intent intent;
    int i = 0;

    @Override
    public void onCreate(){
        super.onCreate();
        CLASS_NAME = BackgroundService.this.getClass().getName();
        intent = new Intent(BROADCAST_ACTION);
        route = getDataFromFile("buzappRoute.txt");
        myId = getDataFromFile("buzappId.txt");

        context = this;
        manager = AccountManager.get(this);
        tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        wManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        Log.d(CLASS_NAME,"Meu id: " + myId);
        Log.d(CLASS_NAME,"Minha rota: " + route);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    public static void setLocationProvider(MyLocationProvider mp){
        myLocationProvider = mp;
    }

    public static MyLocationProvider getLocationProvider() {
        return myLocationProvider;
    }

    @Override
    public void onStart(Intent intent, int startId) {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, listener);
        i++;
    }


    @Override
    public IBinder onBind(Intent intent){
        return null;
    }


    protected boolean isBetterLocation(Location location, Location currentBestLocation) {

        if (currentBestLocation == null) {
            return true;
        }
        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }

        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {

        if (provider1 == null) {
            return provider2 == null;
        }

        return provider1.equals(provider2);
    }

    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v(CLASS_NAME, " !!! STOPPED !!! ");
        locationManager.removeUpdates(listener);
    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {

        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Log.v(CLASS_NAME, "performOnBackgroundThread Exception");
                    }
                } finally {
                    //
                }
            }
        };
        t.start();

        return t;
    }

    public String getDataFromFile(String file) {

        try {
            FileInputStream fileIn = openFileInput(file);
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer= new char[READ_BLOCK_SIZE];
            String s = "";
            int charRead;

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer,0,charRead);
                s += readstring;
            }
            InputRead.close();
            Toast.makeText(getBaseContext(), s,Toast.LENGTH_SHORT).show();
            return s;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }

    private void sendToServer() {

        try {
            //Log.d("BackgroundService", "sending To Server!");
            JSONObject jo = new JSONObject();
            HttpClient httpclient = new DefaultHttpClient(createHttpParams());
            HttpPost httpPost = new HttpPost(urlPostBusLocation);

            jo.put("linha", route);
            jo.put("placa", myId);
            jo.put("velocity", myLocation.getSpeed());
            jo.put("latitude", myLocation.getLatitude());
            jo.put("longitude", myLocation.getLongitude());


            String json = jo.toString();
            Log.d("BackService", json);
            StringEntity se = new StringEntity(json, CODEPAGE);

            httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setEntity(se);

            HttpResponse httpResponse = httpclient.execute(httpPost);

            InputStream inputStream = httpResponse.getEntity().getContent();

            String result = (inputStream != null) ? convertInputStreamToString(inputStream) : "Did not work!";

            Log.d(CLASS_NAME, result);

        } catch (JSONException e) {
            Log.d("BackgroundService", "sendToServer " + e);;
        } catch (IOException e) {
            Log.d("BackgroundService", "sendToServer " + e);
        }
    }

    public static HttpParams createHttpParams() {

        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT);

        return httpParameters;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{

        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";

        while((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        inputStream.close();

        return result;
    }

    public static JSONObject getDeviceInfo() {

        JSONObject jsonObject = new JSONObject();
        String uuid = tManager.getDeviceId();
        String serial = tManager.getDeviceId();
        WifiInfo info = wManager.getConnectionInfo();
        String macAddress = info.getMacAddress();
        String simSerialNumber = tManager.getSimSerialNumber();
        String simNumber = tManager.getLine1Number();
        Account[] accounts = manager.getAccountsByType("com.google");
        String email=accounts[0].name;
        SharedPreferences prefs = context.getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        String registrationId = prefs.getString(GCMConstants.REG_ID, "");

        try {
            jsonObject = new DeviceInfo(uuid, serial, macAddress, simSerialNumber, simNumber, email, registrationId).toJSON();
            Log.d("JSON: ",jsonObject.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        sendDeviceInfoToServer(jsonObject);

        return jsonObject;
    }

    public static void updateBusInfo(String newRoute,String newID) {
        route = newRoute;
        myId = newID;
        try {
            FileOutputStream fileout = context.openFileOutput("buzappRoute.txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(route);

            outputWriter.close();

            fileout = context.openFileOutput("buzappId.txt", MODE_PRIVATE);
            outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(myId);

            outputWriter.close();
            //display file saved message
            Toast.makeText(context, "File updated successfully!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {}
    }

    public static void launchMobizen() {

        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.rsupport.mobizen.sec");
        if(launchIntent != null) {
            context.startActivity(launchIntent);
        }
    }

    private static void sendDeviceInfoToServer(JSONObject jsonObject) {
        try {
            HttpClient httpclient = new DefaultHttpClient(createHttpParams());
            HttpPost httpPost = new HttpPost(urlReportDeviceInfo);


            String json = jsonObject.toString();
            Log.d("BackService Device Info", json);
            StringEntity se = new StringEntity(json, CODEPAGE);

            httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setEntity(se);

            HttpResponse httpResponse = httpclient.execute(httpPost);

            InputStream inputStream = httpResponse.getEntity().getContent();
            String result = (inputStream != null) ? convertInputStreamToString(inputStream) : "Did not work!";

            Log.d(CLASS_NAME, result);

        } catch (IOException e) {
            Log.d("Background Device Info", "sendToServer " + e);
        }

    }

    public class MyLocationListener
            implements LocationListener {

        public void onLocationChanged(final Location loc) {
            Log.d(CLASS_NAME, "Location changed");

            if(isBetterLocation(loc, previousBestLocation)) {
                loc.getLatitude();
                loc.getLongitude();
                intent.putExtra("Latitude", loc.getLatitude());
                intent.putExtra("Longitude", loc.getLongitude());
                intent.putExtra("Provider", loc.getProvider());
                sendBroadcast(intent);

                if(myLocation != null) {
                    if (loc.getLatitude() != myLocation.getLatitude() && loc.getLongitude() != myLocation.getLongitude()) {
                        myLocation = loc;
                        sendToServer();
                        timerCounter = 0;
                        hasStoped = false;
                    }
                } else {
                    myLocation = loc;
                }

            }
        }

        public void onProviderDisabled(String provider) {
            Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
        }

        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) { }

    }

}