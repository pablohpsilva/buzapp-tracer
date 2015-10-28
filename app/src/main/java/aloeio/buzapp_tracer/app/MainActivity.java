package aloeio.buzapp_tracer.app;

import aloeio.buzapp_tracer.app.Fragments.MapFragment;
import aloeio.buzapp_tracer.app.Models.BusInfoSingleton;
import aloeio.buzapp_tracer.app.Services.BackgroundService;
import aloeio.buzapp_tracer.app.Utils.GCMConstants;
import aloeio.buzapp_tracer.app.Utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class MainActivity extends FragmentActivity implements
        MapFragment.OnFragmentInteractionListener{
    private Utils utils = null;
    final private static String MAPZIPNAME = "Uberlandia_2015-03-06_223449.zip";

    private EditText routeEditText;
    private EditText plateEditText;
    private EditText numberEditText;
    private Switch accessibilitySwitch;
    private Spinner typeSpinner;
    private Button startButton;
    private String regId = "";
    private GoogleCloudMessaging gcmObj;
    private Context context =this;

    private static String mainRoute;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = this.getSharedPreferences(
                "com.example.buzapp", Context.MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        if(isGooglePlayAvailable())
            registerInBackground();
        else
            Toast.makeText(context,"Google play services não disponível",Toast.LENGTH_LONG);


//        if(!f.exists()) {
//            try {
//                f.createNewFile();
//            } catch (IOException e) {}
//        }
//        if(!f2.exists()){
//            try {
//                f2.createNewFile();
//            } catch (IOException e) {}
//        }
//

        routeEditText = (EditText) findViewById(R.id.main_edt_route);
        plateEditText = (EditText) findViewById(R.id.main_edt_plate);
        numberEditText = (EditText) findViewById(R.id.main_edt_bus_number);
        accessibilitySwitch = (Switch) findViewById(R.id.main_swt_accessibility);
        typeSpinner = (Spinner) findViewById(R.id.main_spn_type);
        startButton = (Button) findViewById(R.id.main_btn_start);
        BusInfoSingleton.getInstance();

        routeEditText.setText("T131");
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String route = routeEditText.getText().toString();
                String plate = plateEditText.getText().toString();
                String number = numberEditText.getText().toString();

                try {
                    FileOutputStream fileout = openFileOutput("buzappRoute.txt", MODE_PRIVATE);
                    OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                    outputWriter.write(route);

                    outputWriter.close();

                    fileout = openFileOutput("buzappId.txt", MODE_PRIVATE);
                    outputWriter = new OutputStreamWriter(fileout);
                    outputWriter.write(plate);

                    outputWriter.close();
                    //display file saved message
                    Toast.makeText(getBaseContext(), "File saved successfully!",
                            Toast.LENGTH_SHORT).show();

                } catch (Exception e) {}


                if(route.length() != 4) {
                    Toast.makeText(MainActivity.this, "Problema. Escreva uma rota real. Exemplo: T131", Toast.LENGTH_SHORT).show();
                } else if(plate.length() != 7) {
                    Toast.makeText(MainActivity.this, "Problema. Escreva uma placa real. Exemplo: ABC1234", Toast.LENGTH_SHORT).show();
                } else if(!isNumeric(number)) {
                    Toast.makeText(MainActivity.this, "Problema. O Numero do onibus deve ser somente numeros.", Toast.LENGTH_SHORT).show();
                } else {
//                    mainRoute = route;
                    BusInfoSingleton.getInstance().setAll(routeEditText, plateEditText, numberEditText, typeSpinner, accessibilitySwitch);
                    findViewById(R.id.main_controls).setVisibility(View.INVISIBLE);
//                    findViewById(R.id.buzapp_logo).setVisibility(View.GONE);
                    findViewById(R.id.main_loading_spinner).setVisibility(View.VISIBLE);
                    findViewById(R.id.main_loading_text).setVisibility(View.VISIBLE);
                    findViewById(R.id.scroll_view_menu).setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragment_container, new MapFragment())
                            .commit();

                    Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
                    startService(intent);

//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            for(;;) {
//                                Log.d("Thread", "Trying save id");
//                                if (setIdOnFile) {
//                                    saveId();
//                                    Log.d("Thread", "Saved");
//                                    return;
//                                } else {
//                                    try {
//                                        Thread.sleep(2000);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        }
//                    }).start();

                }
            }

        });

        utils = new Utils(this);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
//            configFragment.setArguments(getIntent().getExtras());
//            this.callFragment(0, null);
//            this.setMainActivityDefaults();
        }

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public static String getMainRoute(){
        return mainRoute;
    }

//    public static int getMainId(){
//        return mainId;
//    }


    // AsyncTask to register Device in GCM Server
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcmObj == null) {
                        gcmObj = GoogleCloudMessaging
                                .getInstance(context);
                    }
                    regId = gcmObj
                            .register(GCMConstants.GOOGLE_PROJ_ID);
                    msg = "Registration ID :" + regId;

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (!TextUtils.isEmpty(regId)) {
                    // Store RegId created by GCM Server in SharedPref
                    storeRegIdinSharedPref(context, regId);
                    Log.d("Registered", " with GCM Server successfully." + msg);
                    Toast.makeText(context,"Registered with GCM Server successfully." + msg,Toast.LENGTH_SHORT);
                } else {
                    Log.d("Reg ID Creation Failed.","Either you haven't enabled Internet or GCM server is busy right now. Make sure you enabled Internet and try registering again after some time." + msg);
                    Toast.makeText(context,"Reg ID Creation Failed. Either you haven't enabled Internet or GCM server is busy right now. Make sure you enabled Internet and try registering again after some time." + msg, Toast.LENGTH_LONG);
                }
            }
        }.execute(null, null, null);
    }

    // Store  RegId and UUID entered by User in SharedPref
    private void storeRegIdinSharedPref(Context context, String regId) {
        SharedPreferences prefs = getSharedPreferences("userDetails",
                Context.MODE_PRIVATE);
        TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String uuid = tManager.getDeviceId();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(GCMConstants.REG_ID, regId);
        editor.putString(GCMConstants.UUID, uuid);
        editor.putString(GCMConstants.EMAIL,"email");
        Toast.makeText(context, "Gravado" + regId + "   " + uuid, Toast.LENGTH_SHORT);
        Log.d("Registered", "Gravado" + regId + "   " + uuid);
        editor.commit();

    }

    public boolean isGooglePlayAvailable() {
        boolean googlePlayStoreInstalled;
        int val= GooglePlayServicesUtil.isGooglePlayServicesAvailable(MainActivity.this);
        googlePlayStoreInstalled = val == ConnectionResult.SUCCESS;
        return googlePlayStoreInstalled;
    }


}
