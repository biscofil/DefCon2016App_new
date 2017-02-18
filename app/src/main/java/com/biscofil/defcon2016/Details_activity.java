package com.biscofil.defcon2016;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class Details_activity extends AppCompatActivity {

    public Activity mActivity;

    LineChart graph;
    RatingBar rb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;

        setContentView(R.layout.activity_details);

        rb = (RatingBar) findViewById(R.id.ratingBar);
        graph = (LineChart) findViewById(R.id.chart);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent i = getIntent();
        int id_struttura = i.getIntExtra("id_struttura", -1);
        Struttura s = ((EcoMe) getApplication()).strutture.get(id_struttura);

        //titolo
        ((CollapsingToolbarLayout) findViewById(R.id.toolbar_layout)).setTitle(s.nome);

        new DetailsDownloadTask(this, id_struttura).execute();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setup_graph(Struttura s) {

        PointF[] points = new PointF[100];
        for (int k = 0; k < points.length; k++) {
            points[k] = new PointF((float) k, (float) (Math.sin(k * 0.5) * 20 * (Math.random() * 10 + 1)));
        }

        List<Entry> entries = new ArrayList<Entry>();

        int k = 0;
        for (PointF data : points) {
            entries.add(new Entry(data.x, data.y));
            k++;
        }

        LineDataSet dataSet = new LineDataSet(entries, getString(R.string.punteggio)); // add entries to dataset
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dataSet.setColor(getColor(R.color.colorPrimary));
            dataSet.setValueTextColor(getColor(R.color.colorPrimaryDark)); // styling, ...
        }

        LineData lineData = new LineData(dataSet);

        graph.setData(lineData);
    }

    public void setup_fab(final Struttura s) {
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

        if (((EcoMe) getApplication()).tutorialHandler.isFirstTimeHere(this.getClass())) {
            TourGuide mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                    //.setPointer(new Pointer())
                    .setToolTip(new ToolTip().setTitle(getString(R.string.tutorial_title)).setDescription(getString(R.string.tutorial_link_struttura)))
                    .setOverlay(new Overlay())
                    .playOn(fab);
        }
    }


    private class DetailsDownloadTask extends AsyncTask<Void, Void, Struttura> {
        Activity act;
        Snackbar snack;
        int _id;

        public DetailsDownloadTask(Activity act, int id) {
            this.act = act;
            _id = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // snack = Snackbar.make(act.getCurrentFocus(), R.string.scarico_dettagli, Snackbar.LENGTH_LONG);
            // snack.show();
        }

        @Override
        protected void onPostExecute(final Struttura s) {
            super.onPostExecute(s);
            //snack.dismiss();

            //immagine header
            NetworkImageView backdrop = (NetworkImageView) findViewById(R.id.backdrop);
            ImageLoader mImageLoader = CustomVolleyRequestQueue.getInstance(act)
                    .getImageLoader();
            final String url = s.url_img;
            mImageLoader.get(url, ImageLoader.getImageListener(backdrop, R.drawable.campo_sm, R.drawable.campo_sm));
            backdrop.setImageUrl(url, mImageLoader);

            //descrizione
            ((TextView) findViewById(R.id.details_text)).setText(s.descrizione);

            //bottone
            setup_fab(s);

            //punteggio
            CardView card_punteggi = (CardView) findViewById(R.id.card_punteggi);

            if (s.no_data) {
                card_punteggi.setVisibility(View.GONE);
                graph.setVisibility(View.GONE);
            } else {
                rb.setMax(5);
                rb.setRating((float) s.punteggio);
                rb.setStepSize(0.5f);
                //str punteggio

                rb.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        DetailsCalcoloDialog cp = new DetailsCalcoloDialog();
                        cp.setStruttura(s.lat_lng);
                        cp.show(getSupportFragmentManager(), "BISCO");
                        return false;
                    }
                });

                String[] splited = s.data_dati.split("\\s+");

                ((TextView) findViewById(R.id.tv_punteggio_val)).setText("" + s.punteggio);

                ((TextView) findViewById(R.id.tv_data_calcolo_val)).setText(splited[0]);
                ((TextView) findViewById(R.id.tv_ora_calcolo_val)).setText(splited[1]);

            }

            //storico
            setup_graph(s);
        }

        @Override
        protected Struttura doInBackground(Void... params) {
            Struttura out = null;
            String url = getString(R.string.web_url) + getString(R.string.xhr_controller) + getString(R.string.struttura_method) + "/" + _id;
            JSONObject object = null;
            try {
                object = new XhrInterface().getObject(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            out = new Struttura();
            out.parse_full(object);
            return out;
        }
    }
}
