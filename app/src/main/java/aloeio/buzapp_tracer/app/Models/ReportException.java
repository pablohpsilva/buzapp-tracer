package aloeio.buzapp_tracer.app.Models;

import aloeio.buzapp_tracer.app.Interfaces.IBackendJSON;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pablohenrique on 4/9/15.
 */
public class ReportException
        implements IBackendJSON {

    private String className = "";
    private String message = "";
    private String localizedMessage = "";
    private String cause = "";
    private String stackTrace = "";
    private String rawException = "";

    public ReportException(Exception exception) {

        this.className = exception.getClass().getName();
        this.message = exception.getMessage();
        this.localizedMessage = exception.getLocalizedMessage();
        this.cause = ((exception.getCause() == null) ? "CAUSE NOT FOUND" : exception.getCause().toString());
        this.rawException = exception.toString();

        if(exception.getStackTrace().length != 0) {
            for (StackTraceElement elem : exception.getStackTrace()) {
                this.stackTrace += "class:" + elem.getClassName() + " \tfile:" + elem.getFileName() + "\tline:" + elem.getLineNumber() + "\n";
            }
        }
    }

    @Override
    public JSONObject toJSON() throws JSONException {

        JSONObject json = new JSONObject();
        json.accumulate("class_name", this.className);
        json.accumulate("message", this.message);
        json.accumulate("localized_message", this.localizedMessage);
        json.accumulate("cause", this.cause);
        json.accumulate("stack_trace", this.stackTrace);
        json.accumulate("raw_exception", this.rawException);

        return json;
    }
}
