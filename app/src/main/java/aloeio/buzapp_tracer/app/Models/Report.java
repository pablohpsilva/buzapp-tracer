package aloeio.buzapp_tracer.app.Models;

import aloeio.buzapp_tracer.app.Interfaces.IBackendJSON;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pablohenrique on 3/5/15.
 */
public class Report implements IBackendJSON {

    private ReportSystem smartphoneData = new ReportSystem();
    private ReportException exception = null;

    public Report(ReportException exception){
        this.exception = exception;
    }

    public String toJSONString() throws JSONException{
        return toJSON().toString();
    }

    @Override
    public JSONObject toJSON() throws JSONException{
        JSONObject json = new JSONObject();
        json.accumulate("smartphone_data", this.smartphoneData.toJSON());
        json.accumulate("exception", this.exception.toJSON());
        return json;
    }
}
