package aloeio.buzapp_tracer.app.Services.Overrides;

import aloeio.buzapp_tracer.app.Models.Bus;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import org.json.JSONException;
import org.osmdroid.util.NetworkLocationIgnorer;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by pablohenrique on 4/22/15.
 */
public class MyLocationProvider implements IMyLocationProvider, LocationListener {

    private final LocationManager mLocationManager;
    private final NetworkLocationIgnorer mIgnorer = new NetworkLocationIgnorer();

    private Location mLocation;
    private Location tempLocation;
    private Location oldMLocation;
    private IMyLocationConsumer mMyLocationConsumer;
    private Fragment activity;
    private Bus bus;
    private Timer senderTimer;
    private int TIMER_VAR = 2000;


    /**
     * Params - User navigation though bus service
     */
    private int routeStepCounter = 0;
    private boolean alreadySpoken = false;

    /**
     * Params - User navigation to nearest busStop
     */
    private int pathStepCounter = 0;
    private String lastInstruction = "";

    public MyLocationProvider(Fragment activity, String route){
        this.activity = activity;
        mLocationManager = (LocationManager) this.activity.getActivity().getSystemService(Context.LOCATION_SERVICE);
        Log.i(this.getClass().getName(), "criada");
        bus = new Bus(route);
        startBusTracking();
    }

    @Override
    public boolean startLocationProvider(IMyLocationConsumer iMyLocationConsumer) {
        boolean result = false;
        mMyLocationConsumer = iMyLocationConsumer;

        for(final String provider : mLocationManager.getProviders(true))
            if(LocationManager.GPS_PROVIDER.equals(provider) || LocationManager.NETWORK_PROVIDER.equals(provider)){
                result = true;
                float mLocationUpdateMinDistance = 0.0f;
                long mLocationUpdateMinTime = 0;
                mLocationManager.requestLocationUpdates(provider, mLocationUpdateMinTime, mLocationUpdateMinDistance, this);
            }

        Log.i(this.getClass().getName(), "started");

        return result;
    }

    public boolean startLocationProvider(){
        if(this.mMyLocationConsumer != null)
            return this.startLocationProvider(this.mMyLocationConsumer);
        return false;
    }

    @Override
    public void stopLocationProvider() {
//        mMyLocationConsumer = null;
        mLocationManager.removeUpdates(this);
    }

    @Override
    public Location getLastKnownLocation() {
        return mLocation;
    }

    @Override
    public void onLocationChanged(final Location location) {
        if(mIgnorer.shouldIgnore(location.getProvider(), System.currentTimeMillis()))
            return;

        tempLocation = location;

//        decideContinuity();
        startBusTracking();

        mLocation = location;

        if(mMyLocationConsumer != null)
            mMyLocationConsumer.onLocationChanged(mLocation, this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public boolean verifyDifferentPosition(Location location){
        if(mLocation != null && location != null)
            return (this.mLocation.getLatitude() != location.getLatitude() && this.mLocation.getLongitude() != location.getLongitude());
        return false;
    }

    public boolean verifyDifferentPosition(Location location1, Location location2){
        if(location1 != null && location2 != null)
            return (location1.getLatitude() != location2.getLatitude() && location1.getLongitude() != location2.getLongitude());
        return false;
    }

    public void stopBusTracking(){
        if(senderTimer != null)
            senderTimer.cancel();
        senderTimer = null;
    }

    public void restartBusTracking(){
        if(senderTimer == null){
            startBusTracking();
        }
    }

    public Bus getBusOfLocationProvider(){
        return bus;
    }

    public void startBusTracking(){

        if(senderTimer == null) {
            senderTimer = new Timer();
            senderTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if(verifyDifferentPosition(oldMLocation, tempLocation)) {
                            bus.sendJSON(tempLocation);
                        }
                        oldMLocation = tempLocation;
                    } catch (JSONException e) {
//                    exceptionControllerSingleton.catchException(NewBusService.class, e, "bad backend");
                        Log.d("MyLocationProviderJ", e.getMessage());
                    } catch (IOException e) {
//                    exceptionControllerSingleton.catchException(NewBusService.class, e, "bad backend");
                        Log.d("MyLocationProviderI", e.getMessage());
                    } catch (NullPointerException e) {
//                    exceptionControllerSingleton.catchException(NewBusService.class, e, "bad backend");
                        Log.d("MyLocationProviderN", e.getMessage());
                    } catch (Exception e) {
//                    exceptionControllerSingleton.catchException(NewBusService.class, e);
                        Log.d("MyLocationProviderE", e.getMessage());
                    }
                }
            }, 0, TIMER_VAR);
        }
    }
}
