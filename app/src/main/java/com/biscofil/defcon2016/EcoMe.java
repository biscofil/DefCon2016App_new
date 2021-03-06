package com.biscofil.defcon2016;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.biscofil.defcon2016.lib.TutorialHandler;
import com.biscofil.defcon2016.lib.XhrInterface;

public class EcoMe extends Application {

    public boolean offlineMode = false;
    public TutorialHandler tutorialHandler;
    public SharedPreferences sharedpreferences;
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
