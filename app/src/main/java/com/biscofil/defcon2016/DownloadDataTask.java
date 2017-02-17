package com.biscofil.defcon2016;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bisco on 11/02/2017.
 */

public class DownloadDataTask extends AsyncTask<Void, Void, Void> {

    Activity act;

    public DownloadDataTask(Activity act) {
        this.act = act;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //myFadeInAnimation = AnimationUtils.loadAnimation(LoadingActivity.this, R.anim.blinkfade);
        //loading_progress.startAnimation(myFadeInAnimation);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //myFadeInAnimation.cancel();
        Intent intent = new Intent(act, MainActivity.class);
        act.startActivity(intent);
        act.finish();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            /*act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loading_info.setText(act.getString(R.string.downloading_data));
                }
            });*/

            /*
            try {
                String url = act.getString(R.string.web_url) + act.getString(R.string.xhr_controller) + act.getString(R.string.data_method);
                JSONArray data = new XhrInterface().getArray(url);
                for (int i = 0; i < data.length(); i++) {
                    JSONObject object = data.getJSONObject(i);
                    double lat = object.getDouble("lat");
                    double lng = object.getDouble("lng");
                    double weight = object.getDouble("weight");
                    ((EcoMe) act.getApplication()).list.add(new ValuedLatLng(new LatLng(lat, lng), weight));
                }
            } catch (Exception e) {
                Log.e("ECOME", e.getLocalizedMessage());
            }*/

            /*act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loading_info.setText(R.string.downloading_structures_data);
                }
            });*/


            String url = act.getString(R.string.web_url) + act.getString(R.string.xhr_controller) + act.getString(R.string.strutture_method);
            JSONArray data = new XhrInterface().getArray(url);
            for (int i = 0; i < data.length(); i++) {
                JSONObject object = data.getJSONObject(i);
                Struttura s = new Struttura();

                int id;

                try {
                    s.punteggio = object.getDouble("last_value");
                } catch (JSONException e) {
                    s.no_data = true;
                }

                try {
                    id = object.getInt("id");
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