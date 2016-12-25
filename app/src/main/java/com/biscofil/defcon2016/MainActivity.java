package com.biscofil.defcon2016;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.biscofil.defcon2016.map.MyGradient;
import com.biscofil.defcon2016.map.MyHeatmapTileProvider;
import com.biscofil.defcon2016.map.ValuedLatLng;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (((EcoMe) getApplication()).offlineMode) {
            setContentView(R.layout.activity_main);
        } else {
            setContentView(R.layout.activity_main);

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (!((EcoMe) getApplication()).offlineMode) {
            mMap = googleMap;
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.style_json));

                if (!success) {
                    Log.e("ECOME", "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e("ECOME", "Can't find style. Error: ", e);
            }

            mMap.setOnMarkerClickListener(this);
            mMap.getUiSettings().setMapToolbarEnabled(false);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(((EcoMe) getApplication()).mapCenter, 6));
            addHeatMap(((EcoMe) getApplication()).list);
            addMarkers();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            Snackbar.make(getCurrentFocus(), "Aggiorno", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.guida) {
            Intent intent = new Intent(this, Guida_Activity.class);
            startActivity(intent);
        } else if (id == R.id.licenze_versioni) {
            Intent intent = new Intent(this, Licenze_Activity.class);
            startActivity(intent);
        } else if (id == R.id.nav_web) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(getString(R.string.web_url)));
            startActivity(i);
        } else if (id == R.id.nav_tec) {
            Intent intent = new Intent(this, GuidaCalcoloIndice.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int id = (Integer) marker.getTag();
        new DetailsDownloadTask(this, id).execute();
        return true;
    }

    public void addMarkers() {
        Iterator it = ((EcoMe) getApplication()).strutture.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            int id = (Integer) pair.getKey();
            Struttura s = (Struttura) pair.getValue();
            Marker marker = mMap.addMarker(new MarkerOptions().position(s.lat_lng).title(s.nome).icon(getMarkerIcon("#004A0D")));
            marker.setTag(id);
        }
    }

    private void addHeatMap(List<ValuedLatLng> list) {

        // Create the gradient.
        int[] colors = {
                Color.rgb(0, 255, 0), // green
                Color.rgb(255, 0, 0)    // red
        };

        float[] startPoints = {
                0.2f, 1f
        };

        MyGradient gradient = new MyGradient(colors, startPoints);


        // Create a heat map tile provider, passing it the latlngs of the police stations.
        MyHeatmapTileProvider mProvider = new MyHeatmapTileProvider.Builder()
                .valuedData(list)
                .opacity(1)
                .radius(35)
                .gradient(gradient)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    // method definition
    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
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
            snack = Snackbar.make(act.getCurrentFocus(), "Scarico i dettagli...", Snackbar.LENGTH_LONG);
            snack.show();
        }

        @Override
        protected void onPostExecute(Struttura out) {
            super.onPostExecute(out);
            snack.dismiss();
            Intent intent = new Intent(act, DetailsActivity.class);
            intent.putExtra("struttura", out);
            startActivity(intent);
        }

        @Override
        protected Struttura doInBackground(Void... params) {
            Struttura out = null;
            try {
                String url = getString(R.string.web_url) + getString(R.string.xhr_controller) + getString(R.string.struttura_method) + "/" + _id;
                JSONObject object = new XhrInterface().getObject(url);
                out = new Struttura();
                out.id = object.getInt("id");
                out.nome = object.getString("nome");
                out.descrizione = object.getString("descrizione");
                return out;
            } catch (Exception e) {
                Log.e("ECOME", e.getLocalizedMessage());
            }
            return null;
        }
    }

}
