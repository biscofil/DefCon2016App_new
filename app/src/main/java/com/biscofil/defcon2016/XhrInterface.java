package com.biscofil.defcon2016;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by bisco on 30/11/2016.
 */

public class XhrInterface {

    public static String xhr_result = "result";
    public static String xhr_error = "error";
    public static String xhr_data = "dati";

    public static int xhr_error_result = 0;
    public static int xhr_success_result = 1;

    public JSONObject getObject(String path) throws Exception {
        JSONObject obj = commonRequest(path);
        return obj.getJSONObject(xhr_data);
    }

    public JSONArray getArray(String path) throws Exception {
        JSONObject obj = commonRequest(path);
        return obj.getJSONArray(xhr_data);
    }

    private JSONObject commonRequest(String path) throws Exception {
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                InputStream it = new BufferedInputStream(conn.getInputStream());
                InputStreamReader read = new InputStreamReader(it);
                BufferedReader buff = new BufferedReader(read);
                StringBuilder dta = new StringBuilder();
                String chunks;
                while ((chunks = buff.readLine()) != null) {
                    dta.append(chunks);
                }
                JSONObject obj = new JSONObject(dta.toString());
                if (obj.getInt(xhr_result) == xhr_success_result) {
                    return obj;
                } else if (obj.getInt(xhr_result) == xhr_error_result) {
                    throw new Exception(obj.getString(xhr_error));
                } else {
                    throw new Exception("Formato non supportato");
                }
            } else {
                throw new Exception("Impossibile contattare " + path);
            }
        } catch (IOException e) {
            Log.e("ECOME", e.getLocalizedMessage());
        }
        return null;
    }
}
