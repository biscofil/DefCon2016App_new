package com.biscofil.defcon2016.lib;

import android.app.Application;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.biscofil.defcon2016.EcoMe;
import com.biscofil.defcon2016.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class XhrInterface {

    public static String xhr_result = "result";
    public static String xhr_error = "error";
    public static String xhr_data = "dati";

    public static int xhr_error_result = 0;
    public static int xhr_success_result = 1;

    private Application mApp;

    public XhrInterface(Application a) {
        mApp = a;
    }

    public interface VolleyListener {
        void onResponseObject(JSONObject obj);

        void onResponseArray(JSONArray arr);

        void onResponseErrr(String err);
    }

    public void volleyRequestObject(String path, final VolleyListener vl) {
        _download(path, vl, false);
    }

    public void volleyRequestArray(String path, final VolleyListener vl) {
        _download(path, vl, true);
    }

    private void _download(final String path, final VolleyListener vl, final boolean _array) {
        commonRequestVolleyObject(path, new VolleyListener() {
            @Override
            public void onResponseObject(JSONObject obj) {
                _check_result_int(obj, vl, _array);
            }

            @Override
            public void onResponseArray(JSONArray arr) {
                //non richiesto
            }

            @Override
            public void onResponseErrr(String err) {
                Log.e("ECOME", path);
                vl.onResponseErrr(err);
            }
        });
    }

    private void _check_result_int(JSONObject obj, final VolleyListener vl, boolean _array) {
        try {
            if (obj.getInt(xhr_result) == xhr_success_result) {
                if (_array) {
                    vl.onResponseArray(obj.getJSONArray(xhr_data));
                } else {
                    vl.onResponseObject(obj.getJSONObject(xhr_data));
                }
            } else if (obj.getInt(xhr_result) == xhr_error_result) {
                vl.onResponseErrr(obj.getString(xhr_error));
            } else {
                vl.onResponseErrr("Formato non supportato");
            }
        } catch (Exception e) {
            e.printStackTrace();
            vl.onResponseErrr(e.getMessage());
        }
    }

    private void commonRequestVolleyObject(String path, final VolleyListener vl) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, path, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        vl.onResponseObject(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        vl.onResponseErrr(mApp.getString(R.string.error_connection_server));
                    }
                });
        ((EcoMe) mApp).addToRequestQueue(jsObjRequest);
    }

}
