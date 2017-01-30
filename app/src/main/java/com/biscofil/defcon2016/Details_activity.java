package com.biscofil.defcon2016;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.Random;

public class Details_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent i = getIntent();
        final Struttura s = (Struttura) i.getSerializableExtra("struttura");
        ((CollapsingToolbarLayout) findViewById(R.id.toolbar_layout)).setTitle(s.nome);
        TextView tv = (TextView) findViewById(R.id.details_text);
        tv.setText(s.descrizione);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            String url = s.sito_web;

            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        RatingBar rb = (RatingBar) findViewById(R.id.ratingBar);
        rb.setMax(5);
        Random r = new Random();
        rb.setRating(r.nextFloat() * 5);
    }
}
