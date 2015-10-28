package aloeio.buzapp_tracer.app.Services.Overrides;

import aloeio.buzapp_tracer.app.Services.LocationDataSender;

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
    private IMyLocationConsumer mMyLocationConsumer;
    private Fragment activity;
    private LocationDataSender locationDataSender;
    private Timer senderTimer;
    private int TIMER_VAR = 2000;

    public MyLocationProvider(Fragment activity, String route, String plate) {

        this.activity = activity;
        mLocationManager = (LocationManager) this.activity.getActivity().getSystemService(Context.LOCATION_SERVICE);
        Log.i(this.getClass().getName(), "criada");
        locationDataSender = new LocationDataSender(route, plate);
        startBusTracking();
    }

    @Override
    public boolean startLocationProvider(IMyLocationConsumer iMyLocationConsumer) {

        boolean result = false;
        mMyLocationConsumer = iMyLocationConsumer;

        for(final String provider : mLocationManager.getProviders(true)) {
            if (LocationManager.GPS_PROVIDER.equals(provider) || LocationManager.NETWORK_PROVIDER.equals(provider)) {
                result = true;
                float mLocationUpdateMinDistance = 0.0f;
                long mLocationUpdateMinTime = 0;
                mLocationManager.requestLocationUpdates(provider, mLocationUpdateMinTime, mLocationUpdateMinDistance, this);
            }
        }

        Log.i(this.getClass().getName(), "started");

        return result;
    }

    @Override
    public void stopLocationProvider() {
        mLocationManager.removeUpdates(this);
    }

    @Override
    public Location getLastKnownLocation() {
        return mLocation;
    }

    @Override
    public void onLocationChanged(final Location location) {
        if(mIgnorer.shouldIgnore(location.getProvider(), System.currentTimeMillis())) {
            return;
        }

        mLocation = location;

        startBusTracking();

        if(mMyLocationConsumer != null) {
            mMyLocationConsumer.onLocationChanged(mLocation, this);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }

    public void startBusTracking(){

        if(senderTimer == null) {
            senderTimer = new Timer();
            senderTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {

                        if(mLocation != null){
                            locationDataSender.sendJSON(mLocation);
                        }

                    } catch (JSONException e) {
                        Log.d("MyLocationProviderJ", e.getMessage());
                    } catch (IOException e) {
                        Log.d("MyLocationProviderI", e.getMessage());
                    } catch (NullPointerException e) {
                        Log.d("MyLocationProviderN", e.getMessage());
                    } catch (Exception e) {
                        Log.d("MyLocationProviderE", e.getMessage());
                    }
                }
            }, 0, TIMER_VAR);
        }
    }
}
