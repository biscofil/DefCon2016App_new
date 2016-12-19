package com.biscofil.defcon2016;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.biscofil.defcon2016.map.ValuedLatLng;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;


public class LoadingActivity extends AppCompatActivity {

    ImageView loading_progress;
    TextView loading_info;
    Animation myFadeInAnimation;
    LoadingActivity loading_Activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loading_Activity = this;

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.activity_loading);

        loading_progress = (ImageView) findViewById(R.id.loading_progress);
        loading_info = (TextView) findViewById(R.id.loading_info);

        new MyTask(this).execute();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {

        Activity act;

        public MyTask(Activity act){
            this.act = act;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            myFadeInAnimation = AnimationUtils.loadAnimation(LoadingActivity.this, R.anim.blinkfade);
            loading_progress.startAnimation(myFadeInAnimation);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            myFadeInAnimation.cancel();

            Intent intent = new Intent(loading_Activity, MainActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                //download
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading_info.setText(getString(R.string.downloading_data));
                    }
                });

                if (((EcoMe) getApplication()).isOnline()) {

                    try {
                        String url = getString(R.string.web_url) + getString(R.string.xhr_controller) + getString(R.string.data_method);
                        JSONArray data = new XhrInterface().getArray(url);
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object = data.getJSONObject(i);
                            double lat = object.getDouble("lat");
                            double lng = object.getDouble("lng");
                            double weight = object.getDouble("weight");
                            ((EcoMe) getApplication()).list.add(new ValuedLatLng(new LatLng(lat, lng), weight));
                        }
                    } catch (Exception e) {
                        Log.e("ECOME", e.getLocalizedMessage());
                    }

                    //download
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loading_info.setText(R.string.downloading_structures_data);
                        }
                    });

                    try {
                        String url = getString(R.string.web_url) + getString(R.string.xhr_controller) + getString(R.string.strutture_method);
                        JSONArray data = new XhrInterface().getArray(url);
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object = data.getJSONObject(i);
                            Struttura s = new Struttura();
                            int id = object.getInt("id");
                            double lat = object.getDouble("lat");
                            double lng = object.getDouble("lng");
                            String nome = object.getString("nome");
                            s.id = id;
                            s.nome = nome;
                            s.lat_lng = new LatLng(lat, lng);
                            ((EcoMe) getApplication()).strutture.put(id, s);
                        }
                    } catch (Exception e) {
                        Log.e("ECOME", e.getLocalizedMessage());
                    }
                } else {
                    ((EcoMe) getApplication()).offlineMode = true;

                    AlertDialog.Builder builder = new AlertDialog.Builder(act);

                    builder.setMessage("Impossibile accedere ad internet!");


                    // Add the buttons
                    builder.setPositiveButton("Continua", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    builder.setNegativeButton("Esci", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
                    // Set other dialog properties
                    // Create the AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
