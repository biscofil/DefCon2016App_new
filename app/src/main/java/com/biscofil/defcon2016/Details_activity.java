package com.biscofil.defcon2016;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.biscofil.defcon2016.lib.CustomVolleyRequestQueue;
import com.biscofil.defcon2016.lib.Struttura;
import com.biscofil.defcon2016.lib.XhrInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import im.dacer.androidcharts.LineView;
import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;

public class Details_activity extends AppCompatActivity {

    public Activity mActivity;
    //LineChart graph;
    LineView lineView;
    RatingBar rb;
    FloatingActionButton fab;

    private Animation mEnterAnimation, mExitAnimation;

    TextView tv_punteggio_val, tv_data_calcolo_val, tv_ora_calcolo_val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = this;

        setContentView(R.layout.activity_details);

        rb = (RatingBar) findViewById(R.id.ratingBar);
        //graph = (LineChart) findViewById(R.id.chart);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        lineView = (LineView) findViewById(R.id.line_view);

        tv_punteggio_val = (TextView) findViewById(R.id.tv_punteggio_val);
        tv_data_calcolo_val = (TextView) findViewById(R.id.tv_data_calcolo_val);
        tv_ora_calcolo_val = (TextView) findViewById(R.id.tv_ora_calcolo_val);

        /* setup enter and exit animation */
        mEnterAnimation = new AlphaAnimation(0f, 1f);
        mEnterAnimation.setDuration(600);
        mEnterAnimation.setFillAfter(true);

        mExitAnimation = new AlphaAnimation(1f, 0f);
        mExitAnimation.setDuration(600);
        mExitAnimation.setFillAfter(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent i = getIntent();
        int id_struttura = i.getIntExtra("id_struttura", -1);
        final Struttura s = new Struttura();

        ((EcoMe) getApplication())._xhr_interface.volleyRequestObject(
                getString(R.string.web_url) + getString(R.string.xhr_controller) + getString(R.string.struttura_method) + "/" + id_struttura,
                new XhrInterface.VolleyListener() {
                    @Override
                    public void onResponseObject(JSONObject object) {
                        s.parse_storico(object);

                        //titolo
                        ((CollapsingToolbarLayout) findViewById(R.id.toolbar_layout)).setTitle(s.nome);

                        if (s != null) {
                            //immagine header
                            NetworkImageView backdrop = (NetworkImageView) findViewById(R.id.backdrop);
                            ImageLoader mImageLoader = CustomVolleyRequestQueue.getInstance(Details_activity.this)
                                    .getImageLoader();
                            final String url = s.url_img;
                            mImageLoader.get(url, ImageLoader.getImageListener(backdrop, R.drawable.campo_sm, R.drawable.campo_sm));
                            backdrop.setImageUrl(url, mImageLoader);

                            //descrizione
                            ((TextView) findViewById(R.id.details_text)).setText(s.descrizione);

                            //bottone
                            setup_fab(s);

                            //punteggio
                            if (s.no_data) {
                                lineView.setVisibility(View.GONE);//
                                tv_punteggio_val.setText("-");
                                tv_data_calcolo_val.setText("-");
                                tv_ora_calcolo_val.setText("-");
                                rb.setRating(0);
                            } else {
                                tv_punteggio_val.setText("" + s.punteggio);
                                rb.setMax(5);
                                rb.setStepSize(0.5f);
                                rb.setRating((float) s.punteggio);

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

                                SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                SimpleDateFormat dt_data = new SimpleDateFormat("dd/MM/yyyy");
                                SimpleDateFormat dt_ora = new SimpleDateFormat("HH:mm:ss");
                                try {
                                    Date date = dt.parse(s.data_dati);
                                    tv_data_calcolo_val.setText(dt_data.format(date));
                                    tv_ora_calcolo_val.setText(dt_ora.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            //storico
                            if (!s.storico.isEmpty()) {
                                setup_graph(s);
                            } else {
                                lineView.setVisibility(View.GONE);
                            }

                            if (((EcoMe) getApplication()).tutorialHandler.isFirstTimeHere(this.getClass())) {
                                runOverlay_ContinueMethod();
                            }
                        }
                    }

                    @Override
                    public void onResponseArray(JSONArray arr) {

                    }

                    @Override
                    public void onResponseErrr(String err) {
                        Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();
                    }
                });
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
        if (!s.storico.isEmpty()) {

            ArrayList<Float> dataListF3 = new ArrayList<>();
            ArrayList<String> data_labels = new ArrayList<>();

            SimpleDateFormat dt1 = new SimpleDateFormat("HH:mm dd/MM");

            for (Map.Entry<Date, Double> entry : s.storico.entrySet()) {
                double dd = entry.getValue();
                dataListF3.add((float) (dd));
                Date dat = entry.getKey();
                data_labels.add(dt1.format(dat));
            }

            ArrayList<ArrayList<Float>> dataListFs = new ArrayList<>();
            dataListFs.add(dataListF3);

            lineView.setDrawDotLine(false); //optional
            lineView.setShowPopup(LineView.SHOW_POPUPS_MAXMIN_ONLY); //optional
            lineView.setBottomTextList(data_labels);
            int color = Color.parseColor("#248635");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                color = getColor(R.color.colorPrimary);
            }
            lineView.setColorArray(new int[]{color});
            lineView.setFloatDataList(dataListFs);
        }
    }

    public void setup_fab(final Struttura s) {
        fab.setOnClickListener(new View.OnClickListener() {
            String url = s.sito_web;

            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

    }

    private void runOverlay_ContinueMethod() {
        ChainTourGuide tourGuide2 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setTitle(getString(R.string.tutorial_title))
                        .setDescription(getString(R.string.tutorial_details_web))
                        //.setGravity(Gravity.BOTTOM | Gravity.LEFT)
                        .setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorComplement, null))
                )
                .playLater(fab);

        ChainTourGuide tourGuide3 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setTitle(getString(R.string.tutorial_title))
                        .setDescription(getString(R.string.tutorial_details_details))
                        //.setGravity(Gravity.TOP)
                        .setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorComplement, null))
                )
                .playLater(rb);

        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuide2, tourGuide3)
                .setDefaultOverlay(new Overlay()
                        .setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.tutorial_bg, null))
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                )
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.Overlay)
                .build();

        ChainTourGuide.init(this).playInSequence(sequence);
    }

}
