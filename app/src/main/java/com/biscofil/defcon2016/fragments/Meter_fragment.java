package com.biscofil.defcon2016.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.biscofil.defcon2016.R;
import com.biscofil.defcon2016.gps.GPSTracker;

import static com.biscofil.defcon2016.EcoMe.MY_PERMISSIONS_REQUEST_FINE_LOCATION;

public class Meter_fragment extends Fragment {

    // GPSTracker class
    GPSTracker gps;
    private Button btn;
    private Context mContext;

    public Meter_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_meter, container, false);
        getActivity().setTitle(getString(R.string.meter_fragment_title));
        this.mContext = getContext();
        btn = (Button) rootView.findViewById(R.id.btn_update_meter);
        if(this.mContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            this.btn.setEnabled(false);
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)){
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            }
            else{
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

            }
        }
        else{
            gps = new GPSTracker(getActivity(), mContext);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // check if GPS enabled
                if (gps != null && gps.canGetLocation()) {

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    // \n is for new line
                    Toast.makeText(mContext, "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    if(gps != null) gps.showSettingsAlert();
                    else {/*show permissions denied alert;*/}
                }
            }
        });


        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    btn.setEnabled(true);
                    if(gps == null) {
                        gps = new GPSTracker(getActivity(), mContext);
                    }

                } else {
                    btn.setEnabled(false);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
