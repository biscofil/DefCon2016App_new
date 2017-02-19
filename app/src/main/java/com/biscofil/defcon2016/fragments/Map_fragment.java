package com.biscofil.defcon2016.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.biscofil.defcon2016.Details_activity;
import com.biscofil.defcon2016.EcoMe;
import com.biscofil.defcon2016.R;
import com.biscofil.defcon2016.Struttura;
import com.biscofil.defcon2016.gps.GPSTracker;
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

import java.util.Iterator;
import java.util.Map;

import static com.biscofil.defcon2016.EcoMe.MY_PERMISSIONS_REQUEST_FINE_LOCATION;

public class Map_fragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    MapView mMapView;
    Context mContext;
    private GoogleMap mMap;
    private boolean locationEnabled = false;
    GPSTracker gps;
    LatLng position;

    public Map_fragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        getActivity().setTitle(getString(R.string.mappa_fragment_title));
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
        return rootView;
    }
    public void initializeLocationStatus(){
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

            ((EcoMe) getActivity().getApplication()).aggiornaDati(getActivity());
            initializeLocationStatus();
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(false);

                    return;
                }
                else{
                    mMap.setMyLocationEnabled(true);
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
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
        Iterator it = ((EcoMe) getActivity().getApplication()).strutture.entrySet().iterator();
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

    public int val2col(double punteggio) {
        if (!(0 <= punteggio && punteggio <= 5)) {
            throw new RuntimeException("0 < hue < 5");
        }

        float hue = ((float) (punteggio / 5.0)) * (255 / 3);

        float sat = 75;
        float lum = 60;

        hue /= 360;
        sat /= 100;
        lum /= 100;

        return hsvToRgb(hue, sat, lum);
    }

    public int hsvToRgb(float hue, float saturation, float value) {
        int h = (int) (hue * 6);
        float f = hue * 6 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);

        switch (h) {
            case 0:
                return rgbToColor(value, t, p);
            case 1:
                return rgbToColor(q, value, p);
            case 2:
                return rgbToColor(p, value, t);
            case 3:
                return rgbToColor(p, q, value);
            case 4:
                return rgbToColor(t, p, value);
            case 5:
                return rgbToColor(value, p, q);
            default:
                throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
        }
    }

    public int rgbToColor(float r, float g, float b) {
        return Color.rgb((int) (r * 255), (int) (g * 255), (int) (b * 255));
    }

    public Bitmap convertDrawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
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
