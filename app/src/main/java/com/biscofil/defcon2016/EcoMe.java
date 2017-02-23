package com.biscofil.defcon2016;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.biscofil.defcon2016.lib.Struttura;
import com.biscofil.defcon2016.lib.TutorialHandler;
import com.biscofil.defcon2016.lib.XhrInterface;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EcoMe extends Application {

    public boolean offlineMode = false;
    public TutorialHandler tutorialHandler;
    public SharedPreferences sharedpreferences;
    public Map<Integer, Struttura> strutture = new HashMap<>();
    public LatLng mapCenter = new LatLng(41.8919300, 12.5113300); //roma
    private static final String MyPREFERENCES = "MyPrefs";
    public static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 293;
    public static final String TAG = EcoMe.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static EcoMe mInstance;
    public XhrInterface _xhr_interface;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        _xhr_interface = new XhrInterface(this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        tutorialHandler = new TutorialHandler(sharedpreferences);
    }

    public boolean isOnline() {
        ConnectivityManager cm;
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void aggiornaDati(final Activity act) {
        strutture = new HashMap<>();

        _xhr_interface.volleyRequestArray(
                act.getString(R.string.web_url) + act.getString(R.string.xhr_controller) + act.getString(R.string.strutture_method),
                new XhrInterface.VolleyListener() {
                    @Override
                    public void onResponseObject(JSONObject obj) {

                    }

                    @Override
                    public void onResponseArray(JSONArray data) {
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object = null;
                            try {
                                object = data.getJSONObject(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Struttura s = new Struttura();
                            try {
                                s.punteggio = object.getDouble("last_value");
                            } catch (JSONException e) {
                                s.no_data = true;
                            }
                            try {
                                int id = object.getInt("id");
                                s.id = id;
                                s.nome = object.getString("nome");
                                s.lat_lng = new LatLng(object.getDouble("lat"), object.getDouble("lng"));
                                ((EcoMe) act.getApplication()).strutture.put(id, s);
                            } catch (Exception e) {
                                Log.e("ECOME", e.getLocalizedMessage());
                            }
                        }

                        //TODO spostare

                        Intent intent = new Intent(act, MainActivity.class);
                        act.startActivity(intent);
                        act.finish();
                    }

                    @Override
                    public void onResponseErrr(String err) {
                        Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();
                    }
                }
        );

        //new DownloadDataTask(act).execute();
    }


    public static synchronized EcoMe getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}
