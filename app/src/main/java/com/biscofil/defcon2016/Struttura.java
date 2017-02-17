package com.biscofil.defcon2016;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by bisco on 06/12/2016.
 */
public class Struttura implements Serializable {

    public boolean no_data = false;
    public int id;
    public String nome;
    public LatLng lat_lng;
    public String descrizione;
    public String sito_web;
    public String url_img;

    public double punteggio;

    public String data_dati;

    public void parse_slim(JSONObject object) {
        try {
            punteggio = object.getDouble("last_value");
        } catch (JSONException e) {
            no_data = true;
        }
        try {
            id = object.getInt("id");
            nome = object.getString("nome");
            lat_lng = new LatLng(object.getDouble("lat"), object.getDouble("lng"));
        } catch (Exception e) {
            Log.e("ECOME", e.getLocalizedMessage());
        }
    }

    public void parse_full(JSONObject object) {
        parse_slim(object);
        try {
            descrizione = object.getString("descrizione");
            sito_web = object.getString("sito_web");
            url_img = object.getString("url_img");
            data_dati = object.getString("last_value_date");
        } catch (Exception e) {
            Log.e("ECOME", e.getLocalizedMessage());
        }
    }
}
