package aloeio.buzapp_tracer.app.Utils;

/**
 * Created by pablohenrique on 5/27/15.
 */
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by pablohenrique on 2/7/15.
 */
public class HttpAsync extends AsyncTask<String, String, Object> {

    private final String CODEPAGE = "UTF-8";

    public Object sendAsyncPost(String url, String json){
        try {
            return this.execute(url, json).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected Object doInBackground(String... params) {
        HttpResponse response;
        HttpPost request = new HttpPost(params[0]);
        AndroidHttpClient client = AndroidHttpClient.newInstance("instance");

        try {
            String json = params[1];
            request.setEntity(new StringEntity(json.replaceAll("\"", "'"), CODEPAGE));
            response = client.execute(request);

//            return (response.getStatusLine().getStatusCode() == 200);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        finally {
            client.close();
        }
    }

    protected void onPostExecute(JSONObject result) {
        return;
    }

}