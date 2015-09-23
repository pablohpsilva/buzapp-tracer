package aloeio.buzapp_tracer.app.Utils;

import aloeio.buzapp_tracer.app.Utils.UI.MyDialog;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by eduardo on 3/15/15.
 */
public class Utils {

    private MyDialog dialog;

    public Utils(Activity activity){ dialog = new MyDialog(activity.getApplicationContext()); }

    public boolean checkGPS(final Activity activity) {
        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}
