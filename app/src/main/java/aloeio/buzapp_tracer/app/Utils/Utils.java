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
import android.view.View;

/**
 * Created by eduardo on 3/15/15.
 */
public class Utils {
    private ConnectivityManager cm = null;
    private NetworkInfo netInfo = null;
    private MyDialog dialog;

    public Utils(Activity activity){ dialog = new MyDialog(activity.getApplicationContext()); }

    public void verifyConnection(final Activity activity) {
        if (!this.checkConnection(activity))
            buildAlertMessageNoConnection(activity);
    }

    public void verifyGPS(final Activity activity) {
        if (!this.checkGPS(activity))
            buildAlertMessageNoGps(activity);
    }

    public boolean checkConnection(final Activity activity) {
        cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public boolean checkGPS(final Activity activity) {
        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void buildAlertMessageNoConnection(final Activity activity) {
        dialog = dialog.setContext(activity);
        dialog.setTitle("Internet não detectada");
        dialog.setMessage("Para usar o aplicativo, conecte-se à internet: ");
        dialog.setPositiveButton("3/4G", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent().setComponent(new ComponentName(
                        "com.android.settings",
                        "com.android.settings.Settings$DataUsageSummaryActivity"));
                activity.startActivity(intent);
                dialog.close();
                System.exit(1);
            }
        }).setNeutralButton("Wifi", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                activity.startActivity(intent);
                dialog.close();
                System.exit(1);
            }
        }).setNegativeButton("Cancelar", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.close();
                System.exit(1);
            }
        }).show();
    }

    public void buildAlertMessageNoGps(final Activity activity) {
        dialog = dialog.setContext(activity);
        dialog.setTitle("GPS não detectado");
        dialog.setMessage("Seu GPS está desligado, por favor ative-o antes de prosseguir.");
        dialog.setPositiveButton("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                dialog.close();
            }
        }).setNegativeButton("Cancelar", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.close();
            }
        }).show();
    }

}