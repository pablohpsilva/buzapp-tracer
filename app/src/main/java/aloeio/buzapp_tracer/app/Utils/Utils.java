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
    private String message = null;
    private ConnectivityManager cm = null;
    private NetworkInfo netInfo = null;
    private boolean onBackgroundFlag = true;
    private ArrayList<String> muralMessage;
    private MyDialog dialog;

    public Utils(Activity activity){ dialog = new MyDialog(activity.getApplicationContext()); }

    public Utils(Context context){ dialog = new MyDialog(context); }

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

    public void setOnBackgroundFlag(boolean bool){
        onBackgroundFlag = bool;
    }

    public void copyMapFile(String inputPath, String inputFile, String outputPath, Activity activity) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


//            in = new FileInputStream(inputPath + inputFile);
            in =  activity.getAssets().open(inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    public void moveMapFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(inputPath + inputFile).delete();


        }

        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

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

    private ArrayList<String> loadMuralMessage(){
       // gets message from server here by Buzapp mural priority
        muralMessage = new ArrayList<String>();
        muralMessage.add("Olá, este é o mural do Buzapp, onde você receberá mensagens importantes da equipe do Buzapp e de nossos ");
        muralMessage.add("http://www.aloeio.com/partners");
        muralMessage.add("parceiros.");
        muralMessage.add("\nBem vindo a uma nova experiência ao andar de ônibus!");
        return muralMessage;
    }

}
