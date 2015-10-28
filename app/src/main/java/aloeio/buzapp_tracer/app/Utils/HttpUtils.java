package aloeio.buzapp_tracer.app.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.SocketException;

/**
 * Created by pablohenrique on 2/18/15.
 */
public class HttpUtils {
    private final String CODEPAGE = "UTF-8";
    private HttpResponse response = null;
    private final Integer TIMEOUT = 6500;

    private HttpParams createHttpParams(){
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT);
        return httpParameters;
    }

    public boolean postRequest(String url, JSONObject json) throws SocketException, UnsupportedEncodingException, JSONException, IOException {
        HttpPost request = new HttpPost(url);
        HttpClient client = new DefaultHttpClient(this.createHttpParams());
        request.addHeader(HTTP.CONTENT_TYPE,"application/json");
        request.addHeader("Accept", "application/json");

        request.setEntity(new StringEntity(json.toString(), CODEPAGE));
        response = client.execute(request);

        return (response.getStatusLine().getStatusCode() == 200);
    }


}