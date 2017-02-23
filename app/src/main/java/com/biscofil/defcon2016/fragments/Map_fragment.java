package com.biscofil.defcon2016.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.biscofil.defcon2016.Details_activity;
import com.biscofil.defcon2016.EcoMe;
import com.biscofil.defcon2016.MainActivity;
import com.biscofil.defcon2016.R;
import com.biscofil.defcon2016.lib.Struttura;
import com.biscofil.defcon2016.lib.XhrInterface;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.biscofil.defcon2016.EcoMe.MY_PERMISSIONS_REQUEST_FINE_LOCATION;
import static com.biscofil.defcon2016.lib.Utils.convertDrawableToBitmap;
import static com.biscofil.defcon2016.lib.Utils.val2col;

public class Map_fragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    MapView mMapView;
    Context mContext;
    private GoogleMap mMap;

    private boolean locationEnabled;

    public Map<Integer, Struttura> strutture = new HashMap<>();
    public LatLng mapCenter = new LatLng(41.8919300, 12.5113300); //roma

    public Map_fragment() {
        setHasOptionsMenu(true);
    }

    public void aggiornaDati() {
        Activity act = getActivity();
        strutture = new HashMap<>();

        ((EcoMe) getActivity().getApplication())._xhr_interface.volleyRequestArray(
                act.getString(R.string.web_url) + act.getString(R.string.xhr_controller) + act.getString(R.string.strutture_method),
                new XhrInterface.VolleyListener() {
                    @Override
                    public void onResponseObject(JSONObject obj) {

                    }

                    @Override
                    public void onResponseArray(JSONArray data) {
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object = null;
                            try {
                                object = data.getJSONObject(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Struttura s = new Struttura();
                            try {
                                s.punteggio = object.getDouble("last_value");
                            } catch (JSONException e) {
                                s.no_data = true;
                            }
                            try {
                                int id = object.getInt("id");
                                s.id = id;
                                s.nome = object.getString("nome");
                                s.lat_lng = new LatLng(object.getDouble("lat"), object.getDouble("lng"));
                                strutture.put(id, s);
                            } catch (Exception e) {
                                Log.e("ECOME", e.getLocalizedMessage());
                            }
                        }
                        mMap.clear();
                        addMarkers();
                    }

                    @Override
                    public void onResponseErrr(String err) {
                        Toast.makeText(mContext, err, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        getActivity().setTitle(getString(R.string.mappa_fragment_title));

        final Menu menuNav = ((MainActivity) getActivity()).getDrawer().getMenu();
        menuNav.findItem(R.id.menu_map).setChecked(true);

        mContext = getContext();
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

        aggiornaDati();

        return rootView;
    }

    public void initializeLocationStatus() {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.locationEnabled = false;
            mMap.setMyLocationEnabled(false);
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(mContext, "This app need your location", Toast.LENGTH_LONG).show();

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

            }
        } else {
            this.locationEnabled = true;
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.map_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            Snackbar.make(getView(), R.string.sto_aggiornando, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            aggiornaDati();
           /*aggiunto*/
            initializeLocationStatus();
            return true;
        }
        return false;
    }

    /*aggiunto*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(false);
                    return;
                } else {
                    mMap.setMyLocationEnabled(true);
                }
            }
        }
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

            mMap.setOnMarkerClickListener(this);
            mMap.getUiSettings().setMapToolbarEnabled(false);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 6));

            addMarkers();
            initializeLocationStatus();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int id = (Integer) marker.getTag();
        Intent intent = new Intent(getActivity(), Details_activity.class);
        intent.putExtra("id_struttura", id);
        startActivity(intent);
        return true;
    }

    public void addMarkers() {
        Iterator it = strutture.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            int id = (Integer) pair.getKey();
            Struttura s = (Struttura) pair.getValue();
            Drawable d = getResources().getDrawable(R.drawable.struttura_32);

            Bitmap marker_icon;

            if (s.no_data) {
                marker_icon = convertDrawableToBitmap(d);
            } else {
                marker_icon = colora_marker(convertDrawableToBitmap(d), val2col(s.punteggio));
            }

            BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(marker_icon);

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(s.lat_lng)
                    .title(s.nome)
                    .snippet(s.nome)
                    .icon(bd)
            );
            marker.setTag(id);
        }
    }

    public Bitmap colora_marker(Bitmap sourceBitmap, int color) {
        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
                sourceBitmap.getWidth() - 1, sourceBitmap.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 1);
        p.setColorFilter(filter);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);
        return resultBitmap;
    }

}
