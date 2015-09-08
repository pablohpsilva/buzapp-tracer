package aloeio.buzapp_tracer.app.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import aloeio.buzapp_tracer.app.Models.Bus;
import aloeio.buzapp_tracer.app.Models.BusInfo;
import aloeio.buzapp_tracer.app.Services.Overrides.MyLocationProvider;

/**
 * Created by root on 05/09/15.
 */
public class BackgroundService
        extends Service {

    public static final String BROADCAST_ACTION = "Service Back";

    private static String urlPostBusLocation = "http://buzapp-services.aloeio.com/busweb/tracer/receivebus";
    private static final String CODEPAGE = "UTF-8";
    private static final Integer TIMEOUT = 6500;

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    public LocationManager locationManager;
    public MyLocationListener listener;
    private static BusInfo myBusInfo;
    private static Bus myBus;
    private int i = 0;
    private static String route;
    private static String myId;
    private static MyLocationProvider myLocationProvider;
    private static Location myLocation;
    public Location previousBestLocation = null;
    static final int READ_BLOCK_SIZE = 100;

    private static int timerCounter = 0;
    private static final int TIME_UPDATE = 2000;
    private static final int TIME_UPDATE_LIMIT = 600000;

    Intent intent;
    int counter = 0;

    @Override
    public void onCreate(){
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);

        route = getDataFromFile("buzappRoute.txt");
        myId = getDataFromFile("buzappId.txt");

        Log.d("Dados","Meu id" + myId);
        Log.d("Dados","Minha rota" + route);

        if (android.os.Build.VERSION.SDK_INT > 9){
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
    public void onStart(Intent intent, int startId){
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
            // A new location is always better than no location
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
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

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
        Log.v("STOP_SERVICE", "DONE");
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

                    }
                } finally {

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


    public class MyLocationListener
            implements LocationListener {

        public void onLocationChanged(final Location loc) {
            Log.d("****", "Location changed");

            if(isBetterLocation(loc, previousBestLocation)) {
                loc.getLatitude();
                loc.getLongitude();
                //Log.d("BackgroundService", "Longitude = " + loc.getLongitude());
                //Log.d("BackgroundService", "Latitude = " + loc.getLatitude());
                intent.putExtra("Latitude", loc.getLatitude());
                intent.putExtra("Longitude", loc.getLongitude());
                intent.putExtra("Provider", loc.getProvider());
                sendBroadcast(intent);

                if(loc.getLatitude() != myLocation.getLatitude() && loc.getLongitude() != myLocation.getLongitude()) {
                    myLocation = loc;
                    if(myLocation.getSpeed() > 0.0) {
                        sendToServer();
                    }
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

    private void sendToServer() {
        try {
            //Log.d("BackgroundService", "sending To Server!");
            JSONObject jo = new JSONObject();
            InputStream inputStream = null;
            HttpClient httpclient = new DefaultHttpClient(createHttpParams());
            HttpPost httpPost = new HttpPost(urlPostBusLocation);

            if (myId == 0) {
                myId = Integer.parseInt(getDataFromFile("buzappId.txt"));
            }
            String json = "";
            jo.put("linha", route);
            jo.put("id", myId);
            jo.put("velocity", myLocation.getSpeed());
            jo.put("latitude", myLocation.getLatitude());
            jo.put("longitude", myLocation.getLongitude());


            json = jo.toString();
            Log.d("BackService", json);
            StringEntity se = new StringEntity(json, CODEPAGE);

            httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setEntity(se);

            HttpResponse httpResponse = httpclient.execute(httpPost);

            inputStream = httpResponse.getEntity().getContent();

            String result = (inputStream != null) ? convertInputStreamToString(inputStream) : "Did not work!";

            Log.d("BackService", result);

        } catch (JSONException e) {
            Log.d("BackgroundService", "sendToServer " + e);;
        } catch (IOException e) {
            Log.d("BackgroundService", "sendToServer " + e);
        }
    }

    public static HttpParams createHttpParams(){
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT);
        return httpParameters;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

}
