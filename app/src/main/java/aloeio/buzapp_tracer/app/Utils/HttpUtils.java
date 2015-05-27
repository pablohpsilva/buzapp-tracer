package aloeio.buzapp_tracer.app.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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

    public String getRequest(String url) throws HttpException, SocketException, URISyntaxException, ClientProtocolException, IOException{
        HttpGet request = new HttpGet();
        HttpClient client = new DefaultHttpClient(this.createHttpParams());

        request.addHeader("Accept-Encoding", "gzip");

        request.setURI(new URI(url));
        response = client.execute(request);

        return convertStreamToString(response.getEntity().getContent());
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

    public String getGZippedRequest(String url) throws HttpException, SocketException, URISyntaxException, ClientProtocolException, IOException{
        HttpGet request = new HttpGet(url);
        HttpClient client = new DefaultHttpClient(this.createHttpParams());

        request.addHeader("Accept-Encoding", "gzip");
//        request.setURI(new URI(url));

        response = client.execute(request);
        HttpEntity entity = response.getEntity();
        InputStream instream = entity.getContent();
        String result = "";

        if (entity.getContentEncoding() != null && "gzip".equalsIgnoreCase(entity.getContentEncoding().getValue())){
            result = uncompressInputStream(instream);
        } else {
            result = convertStreamToString(instream);
        }
        return result;
    }

    public boolean postGZippedRequest(String url, JSONObject json) throws SocketException, UnsupportedEncodingException, JSONException, IOException {
        HttpPost request = new HttpPost(url);
        HttpClient client = new DefaultHttpClient(this.createHttpParams());

        request.addHeader("Content-Encoding", "gzip");
        request.setEntity( new ByteArrayEntity(compressJson(json)) );

        response = client.execute(request);

        return (response.getStatusLine().getStatusCode() == 200);
    }

    private String convertStreamToString(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(inputStream, CODEPAGE),1024);
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                inputStream.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

    private byte[] compressJson(JSONObject json) throws IOException, JSONException {
        ByteArrayOutputStream baos = null;
        GZIPOutputStream gzos = null;
        byte[] jsonGZippedBytes;
        try {
            baos = new ByteArrayOutputStream();
            gzos = new GZIPOutputStream(baos);
            gzos.write(json.toString().getBytes(CODEPAGE));
            jsonGZippedBytes = baos.toByteArray();
        } finally {
            if(gzos != null)
                gzos.close();
            if(baos != null)
                baos.close();
        }
        return jsonGZippedBytes;
    }

    private String uncompressInputStream(InputStream inputStream) throws IOException {
        StringBuilder value = new StringBuilder();

        GZIPInputStream gzipIn = null;
        InputStreamReader inputReader = null;
        BufferedReader reader = null;

        try {
            gzipIn = new GZIPInputStream(inputStream);
            inputReader = new InputStreamReader(gzipIn, CODEPAGE);
            reader = new BufferedReader(inputReader);

            String line = "";
            while ((line = reader.readLine()) != null) {
                value.append(line);
            }
        } finally {
            try {
                if (gzipIn != null) {
                    gzipIn.close();
                }

                if (inputReader != null) {
                    inputReader.close();
                }

                if (reader != null) {
                    reader.close();
                }

            } catch (IOException io) {

                io.printStackTrace();
            }

        }

        return value.toString();
    }


}
