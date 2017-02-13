package com.biscofil.defcon2016.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.biscofil.defcon2016.Details_activity;
import com.biscofil.defcon2016.EcoMe;
import com.biscofil.defcon2016.R;
import com.biscofil.defcon2016.Struttura;
import com.biscofil.defcon2016.XhrInterface;
import com.biscofil.defcon2016.map.MyGradient;
import com.biscofil.defcon2016.map.MyHeatmapTileProvider;
import com.biscofil.defcon2016.map.ValuedLatLng;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Map_fragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    MapView mMapView;
    private GoogleMap mMap;

    public Map_fragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        getActivity().setTitle(getString(R.string.mappa_fragment_title));

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.map_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            Snackbar.make(getView(), R.string.sto_aggiornando, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            ((EcoMe) getActivity().getApplication()).aggiornaDati(getActivity());

            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    //mappa

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (!((EcoMe) getActivity().getApplication()).offlineMode) {
            this.mMap = googleMap;

            //googleMap.setMyLocationEnabled(true);
/*
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                getActivity(), R.raw.style_json));

                if (!success) {
                    Log.e("ECOME", "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e("ECOME", "Can't find style. Error: ", e);
            }*/

            mMap.setOnMarkerClickListener(this);
            mMap.getUiSettings().setMapToolbarEnabled(false);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(((EcoMe) getActivity().getApplication()).mapCenter, 6));

            if (((EcoMe) getActivity().getApplication()).list.size() > 0) {
                addHeatMap(((EcoMe) getActivity().getApplication()).list);
            }
            addMarkers();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int id = (Integer) marker.getTag();
        new DetailsDownloadTask(getActivity(), id).execute();
        return true;
    }

    public void addMarkers() {
        Iterator it = ((EcoMe) getActivity().getApplication()).strutture.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            int id = (Integer) pair.getKey();
            Struttura s = (Struttura) pair.getValue();
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(s.lat_lng)
                    .title(s.nome)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.struttura_32))
            );
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
            Intent intent = new Intent(act, Details_activity.class);
            intent.putExtra("struttura", out);
            startActivity(intent);
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

            try {
                out.punteggio = object.getDouble("last_value");
            } catch (JSONException e) {
                out.no_data = true;
            }

            try {
                out.id = object.getInt("id");
                out.nome = object.getString("nome");
                out.descrizione = object.getString("descrizione");
                out.sito_web = object.getString("sito_web");
                out.url_img = object.getString("url_img");
                out.data_dati = object.getString("last_value_date");
                return out;
            } catch (Exception e) {
                Log.e("ECOME", e.getLocalizedMessage());
            }
            return null;
        }
    }
}
