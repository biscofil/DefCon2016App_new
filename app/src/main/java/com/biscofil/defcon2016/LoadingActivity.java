package com.biscofil.defcon2016;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

public class LoadingActivity extends AppCompatActivity {

    LoadingActivity loading_Activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loading_Activity = this;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading);

        if (((EcoMe) getApplication()).isOnline()) {
            //old download
            Intent intent = new Intent(loading_Activity, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            ((EcoMe) getApplication()).offlineMode = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(LoadingActivity.this);
            builder.setMessage(R.string.no_internet);
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

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

}
