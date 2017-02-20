package com.biscofil.defcon2016.lib;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Struttura {

    public boolean no_data = false;
    public int id;
    public String nome;
    public LatLng lat_lng;
    public String descrizione;
    public String sito_web;
    public String url_img;

    public Map<Date, Double> storico;

    public double punteggio;

    public String data_dati;

    public void parse_slim(JSONObject object) {
        try {
            punteggio = object.getDouble("last_value");
        } catch (Exception e) {
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

    public void parse_storico(JSONObject object) {
        parse_full(object);

        storico = new HashMap<>();

        try {
            JSONArray arr = object.getJSONArray("storico");

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Date date = parseTimestamp(obj.getString("last_value_date"));
                Double value = obj.getDouble("last_value");
                storico.put(date, value);
            }

        } catch (Exception e) {
        }
    }

    private Date parseTimestamp(String dtStart) {
        //2017-02-18 20:14:49
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(dtStart);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }
}
