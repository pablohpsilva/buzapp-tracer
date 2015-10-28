package aloeio.buzapp_tracer.app.Services;

import aloeio.buzapp_tracer.app.Models.Report;
import aloeio.buzapp_tracer.app.Models.ReportException;
import android.util.Log;
import org.json.JSONException;

/**
 * Created by pablohenrique on 4/8/15.
 */
public class ExceptionService {

    public final static ExceptionService instance = new ExceptionService();
    private ExceptionService(){}
    public static ExceptionService getInstance(){
        return instance;
    }

    private void sendErrorReport(Class mclass, Exception exception) throws JSONException {
        Log.i("ExceptionControllerS", new Report(new ReportException(exception)).toJSONString());
    }

    private void dispatchEvent(String tag){
    }

    private void processException(Class mclass, Exception exception, String tag){

        try {
            sendErrorReport(mclass, exception);
        } catch(Exception e){
            processException(ExceptionService.class, e, "exception");
        }
    }

    public void catchException(Class mclass, Exception exception){
        processException(mclass, exception, "exception");
    }

    public void catchException(Class mclass, Exception exception, String tag){
        processException(mclass, exception, tag);
    }
}

