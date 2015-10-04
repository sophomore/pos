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

    public static String get(String addr, HashMap<String, Object> parameters) {
        return request("GET", addr, parameters);
    }

    public static String post(String addr, HashMap<String, Object> parameters) {
        return request("POST", addr, parameters);
    }

    public static String put(String addr, HashMap<String, Object> parameters) {
        return request("PUT", addr, parameters);
    }

    public static String delete(String addr, HashMap<String, Object> parameters) {
        return request("DELETE", addr, parameters);
    }

    protected static String request(String method, String addr, HashMap<String, Object> parameters) {
        /*
        * use only develop
        * */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        String result = "";
        try {
            URL url = new URL(addr);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setRequestMethod(method);

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
                result = "{\"result\":\"error\", \"error\":\""+httpCon.getResponseCode()+"\"}";
            }
            httpCon.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            result = "{\"result\":\"error\", \"error\":\"데이터를 가져오는 도중 오류가 발생했습니다.\"}";
        }

        Log.d("Http Request", "\nmethod : "+method + "\nresult : "+result);
        return result;
    }
}
