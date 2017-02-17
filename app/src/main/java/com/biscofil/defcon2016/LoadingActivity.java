package com.biscofil.defcon2016;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


public class LoadingActivity extends AppCompatActivity {

    ImageView loading_progress;
    TextView loading_info;
    //Animation myFadeInAnimation;
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


        if (((EcoMe) getApplication()).isOnline()) {
            //new MyTask(this).execute();
            ((EcoMe) getApplication()).aggiornaDati(this);

        } else {
            ((EcoMe) getApplication()).offlineMode = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.no_internet);
            // Add the buttons
            builder.setPositiveButton(R.string.contiue_offline, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(loading_Activity, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            builder.setNegativeButton(R.string.exit_offline, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });

            // Set other dialog properties
            // Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}
