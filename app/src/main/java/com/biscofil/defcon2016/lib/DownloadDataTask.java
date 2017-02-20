package com.biscofil.defcon2016.lib;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.biscofil.defcon2016.EcoMe;
import com.biscofil.defcon2016.MainActivity;
import com.biscofil.defcon2016.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DownloadDataTask extends AsyncTask<Void, Void, Void> {

    Activity act;

    public DownloadDataTask(Activity act) {
        this.act = act;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Intent intent = new Intent(act, MainActivity.class);
        act.startActivity(intent);
        act.finish();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            //TODO spostare in maps
            String url = act.getString(R.string.web_url) + act.getString(R.string.xhr_controller) + act.getString(R.string.strutture_method);
            JSONArray data = new XhrInterface().getArray(url);
            for (int i = 0; i < data.length(); i++) {
                JSONObject object = data.getJSONObject(i);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}