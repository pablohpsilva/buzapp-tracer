package aloeio.buzapp_tracer.app;

import aloeio.buzapp_tracer.app.R;
import aloeio.buzapp_tracer.app.Fragments.MapFragment;
import aloeio.buzapp_tracer.app.Utils.Utils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.*;

import java.io.File;


public class MainActivity extends FragmentActivity implements
        MapFragment.OnFragmentInteractionListener{
    private Utils utils = null;
    final private static String MAPZIPNAME = "Uberlandia_2015-03-06_223449.zip";

    private EditText routeEditText;
    private EditText idEditText;
    private Button startButton;
    private static String mainRoute;
    private static int mainId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        routeEditText = (EditText) findViewById(R.id.main_edt_route);
        routeEditText.setText("T131");
        idEditText = (EditText) findViewById(R.id.main_edt_id);
        idEditText.setText("25");
        startButton = (Button) findViewById(R.id.main_btn_start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String route = routeEditText.getText().toString();
                String id = idEditText.getText().toString();
                if(route.length() != 4)
                    Toast.makeText(MainActivity.this, "Problema. Escreva uma rota real. Exemplo: T131", Toast.LENGTH_SHORT).show();
                else if(!isNumeric(id))
                    Toast.makeText(MainActivity.this, "Problema. Somente numeros maiores que 25", Toast.LENGTH_SHORT).show();
                else if(Integer.parseInt(id) < 25)
                    Toast.makeText(MainActivity.this, "Problema. Somente numeros maiores que 25", Toast.LENGTH_SHORT).show();
                else{
                    mainRoute = route;
                    mainId = Integer.parseInt(id);
                    findViewById(R.id.main_controls).setVisibility(View.INVISIBLE);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragment_container, new MapFragment())
                            .commit();
                }
            }
        });

        utils = new Utils(this);
        copyMapFileIfNeeded();

//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.fragment_container, new MapFragment())
//                .commit();

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

    private void copyMapFileIfNeeded(){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/osmdroid/"+MAPZIPNAME);
        if(!file.exists()){
            // If user does not have the map, create a copy on osmdroid folder, then.
            utils.copyMapFile("file://android_asset/", MAPZIPNAME, Environment.getExternalStorageDirectory().getAbsolutePath() + "/osmdroid/", MainActivity.this);
        }
    }

    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public static String getMainRoute(){
        return mainRoute;
    }

    public static int getMainId(){
        return mainId;
    }
}
