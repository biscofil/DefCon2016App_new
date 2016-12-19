package com.biscofil.defcon2016;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.biscofil.defcon2016.map.ValuedLatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bisco on 29/11/2016.
 */

public class EcoMe extends Application {

    public boolean offlineMode = false;
    public List<ValuedLatLng> list = new ArrayList<>();
    public Map<Integer,Struttura> strutture = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public boolean isOnline() {
        ConnectivityManager cm;
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
