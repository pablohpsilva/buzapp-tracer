package aloeio.buzapp_tracer.app.Services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import aloeio.buzapp_tracer.app.Models.Bus;
import aloeio.buzapp_tracer.app.Models.BusInfo;
import aloeio.buzapp_tracer.app.Services.Overrides.MyLocationProvider;

/**
 * Created by root on 05/09/15.
 */
public class BackgroundService extends Service {

    private static final String TAG = "Location Service";
    private static Bus myBus;
    private boolean isRunning  = false;
    private static MyLocationProvider myLocProvider;

    public void onCreate(){
        Log.d(TAG, "Service Created!");

        isRunning = true;
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        Log.i(TAG, "Service onStartCommand");

        new Thread(new Runnable() {
            @Override
            public void run() {
                //Your logic that service will perform will be placed here
                //In this example we are just looping and waits for 1000 milliseconds in each loop.
                for (;;) {
                    try {
                        Thread.sleep(8000);
                    } catch (Exception e) {
                    }
                    //Log.d(TAG,"Runninf " );
                    // Esse myLocProvider é setado la na drawUserLocation() la no mapManagerService!
                    if(isRunning){
                       // BusInfo myBusInfo = BusInfo.getInstance();
                        if(myLocProvider != null){
                            //Log.d(TAG,"Runn");
                            try {
                                //Log.d(TAG,"" + myLocProvider.getLastKnownLocation());
                                myLocProvider.getBusOfLocationProvider().sendJSON(myLocProvider.getLastKnownLocation());
                            }catch(Exception e){}
                        }
                    }
                }
            }
        }).start();

        return Service.START_STICKY;
    }

    public static void setLocationProvider(MyLocationProvider mp){
        myLocProvider = mp;
    }

    public void onDestroy() {
        isRunning = false;

        Log.i(TAG, "Service onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
