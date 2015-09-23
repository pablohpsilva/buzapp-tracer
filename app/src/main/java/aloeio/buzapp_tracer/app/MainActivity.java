package aloeio.buzapp_tracer.app;

import aloeio.buzapp_tracer.app.Fragments.MapFragment;
import aloeio.buzapp_tracer.app.Models.BusInfo;
import aloeio.buzapp_tracer.app.Models.DeviceInfo;
import aloeio.buzapp_tracer.app.Services.BackgroundService;
import aloeio.buzapp_tracer.app.Utils.GCMConstants;
import aloeio.buzapp_tracer.app.Utils.Utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class MainActivity
        extends FragmentActivity
        implements MapFragment.OnFragmentInteractionListener {
    private Utils utils = null;

    private EditText routeEditText;
    private EditText plateEditText;
    private EditText numberEditText;
    private Switch accessibilitySwitch;
    private Spinner typeSpinner;
    private Button startButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs = this.getSharedPreferences("com.example.buzapp", Context.MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        routeEditText = (EditText) findViewById(R.id.main_edt_route);
        plateEditText = (EditText) findViewById(R.id.main_edt_plate);
        numberEditText = (EditText) findViewById(R.id.main_edt_bus_number);
        accessibilitySwitch = (Switch) findViewById(R.id.main_swt_accessibility);
        typeSpinner = (Spinner) findViewById(R.id.main_spn_type);
        startButton = (Button) findViewById(R.id.main_btn_start);
        BusInfo.getInstance();

        routeEditText.setText("T131");
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String route = routeEditText.getText().toString().replaceAll(" ","").toUpperCase();
                String plate = plateEditText.getText().toString().replaceAll(" ","").toUpperCase();
                String number = numberEditText.getText().toString().replaceAll(" ","");

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

                } catch (Exception e) {
                    //
                }


                if(route.length() != 4) {
                    Toast.makeText(MainActivity.this, "Problema. Escreva uma rota real. Exemplo: T131", Toast.LENGTH_SHORT).show();
                } else if(plate.length() != 7) {
                    Toast.makeText(MainActivity.this, "Problema. Escreva uma placa real. Exemplo: ABC1234", Toast.LENGTH_SHORT).show();
                } else if(!isNumeric(number)) {
                    Toast.makeText(MainActivity.this, "Problema. O Numero do onibus deve ser somente numeros.", Toast.LENGTH_SHORT).show();
                } else {
                    BusInfo.getInstance().setAll(routeEditText, plateEditText, numberEditText, typeSpinner, accessibilitySwitch);
                    findViewById(R.id.main_controls).setVisibility(View.INVISIBLE);
                    findViewById(R.id.main_loading_spinner).setVisibility(View.VISIBLE);
                    findViewById(R.id.main_loading_text).setVisibility(View.VISIBLE);
                    findViewById(R.id.scroll_view_menu).setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragment_container, new MapFragment())
                            .commit();

                    Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
                    startService(intent);
                }
            }

        });

        utils = new Utils(this);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
        }

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

}
