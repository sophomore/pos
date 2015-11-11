package org.jaram.ds.util;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by kjydiary on 15. 9. 6..
 */
public class Http {

    public static String get(String addr, HashMap<String, Object> parameters) throws IOException {
        return request("GET", addr, parameters);
    }

    public static String post(String addr, HashMap<String, Object> parameters) throws IOException {
        return request("POST", addr, parameters);
    }

    public static String put(String addr, HashMap<String, Object> parameters) throws IOException {
        return request("PUT", addr, parameters);
    }

    public static String delete(String addr, HashMap<String, Object> parameters) throws IOException {
        return request("DELETE", addr, parameters);
    }

    protected static String request(String method, String addr, HashMap<String, Object> parameters) throws IOException {
        String result = "";
        URL url = new URL(addr);
        HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setRequestMethod(method);
        httpCon.setConnectTimeout(3000);

        if (parameters != null) {
            httpCon.setDoOutput(true);
            String parameterUrl = "";
            for (String key : parameters.keySet()) {
                parameterUrl = parameterUrl + URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(parameters.get(key).toString(), "UTF-8") + "&";
            }
            DataOutputStream wr = new DataOutputStream(httpCon.getOutputStream());
            wr.writeBytes(parameterUrl);
            wr.flush();
            wr.close();
        }

        if (httpCon.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader (new InputStreamReader(httpCon.getInputStream()));
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                result += line;
            }
            br.close();
        }
        else {
            throw new IOException("response "+httpCon.getResponseCode());
        }
        httpCon.disconnect();

        return result;
    }
}
