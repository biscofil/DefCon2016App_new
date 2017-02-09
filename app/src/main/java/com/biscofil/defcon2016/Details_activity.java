package com.biscofil.defcon2016;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class Details_activity extends AppCompatActivity {

    public ChainTourGuide mTourGuideHandler;
    public Activity mActivity;
    private Button mButton1, mButton2, mButton3;
    private Animation mEnterAnimation, mExitAnimation;

    public static final int OVERLAY_METHOD = 1;
    public static final int OVERLAY_LISTENER_METHOD = 2;

    public static final String CONTINUE_METHOD = "continue_method";
    private int mChosenContinueMethod = OVERLAY_METHOD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;

        setContentView(R.layout.activity_details);

        Intent i = getIntent();
        final Struttura s = (Struttura) i.getSerializableExtra("struttura");

        //immagine header
        NetworkImageView backdrop = (NetworkImageView) findViewById(R.id.backdrop);
        ImageLoader mImageLoader = CustomVolleyRequestQueue.getInstance(this.getApplicationContext())
                .getImageLoader();
        final String url = s.url_img;
        mImageLoader.get(url, ImageLoader.getImageListener(backdrop, R.drawable.campo_sm, R.drawable.campo_sm));
        backdrop.setImageUrl(url, mImageLoader);

        //titolo e descrizione
        ((CollapsingToolbarLayout) findViewById(R.id.toolbar_layout)).setTitle(s.nome);
        ((TextView) findViewById(R.id.details_text)).setText(s.descrizione);

        //bottone
        setup_fab(s);

        //punteggio
        RatingBar rb = (RatingBar) findViewById(R.id.ratingBar);
        rb.setMax(5);
        rb.setRating((float) s.punteggio);

        //storico
        setup_graph(s);
    }

    public void setup_graph(Struttura s) {
        GraphView graph = (GraphView) findViewById(R.id.graph);
        DataPoint[] points = new DataPoint[100];
        for (int k = 0; k < points.length; k++) {
            points[k] = new DataPoint(k, Math.sin(k * 0.5) * 20 * (Math.random() * 10 + 1));
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-150);
        graph.getViewport().setMaxY(150);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(4);
        graph.getViewport().setMaxX(80);
        // enable scaling and scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.addSeries(series);
        graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScrollableY(true); // enables vertical scrolling
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling
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
                    .setToolTip(new ToolTip().setTitle("Tutorial").setDescription("Premi per andare al sito della struttura"))
                    .setOverlay(new Overlay())
                    .playOn(fab);
        }
    }
}
