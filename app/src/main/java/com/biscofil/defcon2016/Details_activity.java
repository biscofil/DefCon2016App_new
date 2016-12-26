package com.biscofil.defcon2016;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class Details_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);

        Intent i = getIntent();
        Struttura s = (Struttura) i.getSerializableExtra("struttura");

        ((CollapsingToolbarLayout) findViewById(R.id.toolbar_layout)).setTitle(s.nome);

        TextView tv = (TextView) findViewById(R.id.details_text);
        tv.setText(s.descrizione);

    }

}
