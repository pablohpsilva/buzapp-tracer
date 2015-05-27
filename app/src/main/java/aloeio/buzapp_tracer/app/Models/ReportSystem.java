package aloeio.buzapp_tracer.app.Models;

import aloeio.buzapp_tracer.app.Interfaces.IBackendJSON;
import android.os.Build;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pablohenrique on 3/10/15.
 */
public class ReportSystem implements IBackendJSON {
    private String OS = "Android";
    private String VERSION;
    private String SMARTPHONE_MODEL;
    private String SMARTPHONE_MANUFACTURER;
    private String SMARTPHONE_PRODUCT;
    private String SMARTPHONE_BRAND;
    public ReportSystem(){
        VERSION = Build.VERSION.RELEASE;
        SMARTPHONE_MANUFACTURER = Build.MANUFACTURER;
        SMARTPHONE_MODEL = Build.MODEL;
        SMARTPHONE_PRODUCT = Build.PRODUCT;
        SMARTPHONE_BRAND = Build.BRAND;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.accumulate("operational_system", OS);
        json.accumulate("version", VERSION);
        json.accumulate("model", SMARTPHONE_MODEL);
        json.accumulate("manufacturer", SMARTPHONE_MANUFACTURER);
        json.accumulate("product",SMARTPHONE_PRODUCT);
        json.accumulate("brand",SMARTPHONE_BRAND);
        return json;
    }
}
