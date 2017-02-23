package com.biscofil.defcon2016.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Toast;

import com.biscofil.defcon2016.DetailsCalcoloDialog;
import com.biscofil.defcon2016.EcoMe;
import com.biscofil.defcon2016.R;
import com.biscofil.defcon2016.gps.GPSTracker;
import com.biscofil.defcon2016.lib.XhrInterface;
import com.biscofil.defcon2016.views.MyArcProgress;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;

import static com.biscofil.defcon2016.EcoMe.MY_PERMISSIONS_REQUEST_FINE_LOCATION;

public class Meter_fragment extends Fragment {

    private Animation mEnterAnimation, mExitAnimation;
    GPSTracker gps;
    LatLng position;
    private Button btn, btn_info;
    public MyArcProgress arcProgress;
    private Context mContext;

    public Meter_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_meter, container, false);
        getActivity().setTitle(getString(R.string.meter_fragment_title));
        this.mContext = getContext();

        btn = (Button) rootView.findViewById(R.id.btn_meter_update);
        btn_info = (Button) rootView.findViewById(R.id.btn_meter_details);

        arcProgress = (MyArcProgress) rootView.findViewById(R.id.arc_progress);

        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.btn.setEnabled(false);
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(mContext, R.string.app_require_gps_toast, Toast.LENGTH_LONG).show();

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

            }
        } else {
            this.btn.setEnabled(true);
            gps = new GPSTracker(getActivity(), mContext);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if GPS enabled
                if (gps != null && gps.canGetLocation()) {

                    position = new LatLng(gps.getLatitude(), gps.getLongitude());

                    if (position.latitude == 0 && position.longitude == 0) {
                        Toast.makeText(mContext, R.string.try_later_gps, Toast.LENGTH_LONG).show();
                    } else {
                        richiesta_sito();
                    }
                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    if (gps != null) {
                        gps.showSettingsAlert();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle(R.string.gps_alert_title);
                        builder.setMessage(R.string.gps_alert_body);
                        builder.show();
                    }
                }
            }
        });

        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailsCalcoloDialog cp = new DetailsCalcoloDialog();
                cp.setStruttura(position);
                cp.show(getChildFragmentManager(), "BISCO");
            }
        });

          /* setup enter and exit animation */
        mEnterAnimation = new AlphaAnimation(0f, 1f);
        mEnterAnimation.setDuration(600);
        mEnterAnimation.setFillAfter(true);

        mExitAnimation = new AlphaAnimation(1f, 0f);
        mExitAnimation.setDuration(600);
        mExitAnimation.setFillAfter(true);

        if (((EcoMe) getActivity().getApplication()).tutorialHandler.isFirstTimeHere(this.getClass())) {
            runOverlay_ContinueMethod();
        }

        return rootView;
    }

    public void richiesta_sito() {
        ((EcoMe) getActivity().getApplication())._xhr_interface.volleyRequestObject(
                getString(R.string.web_url) + getString(R.string.xhr_controller) + getString(R.string.gps_to_value_method) + gps.getLatitude() + "/" + gps.getLongitude(),
                new XhrInterface.VolleyListener() {
                    @Override
                    public void onResponseObject(JSONObject result) {

                        //post
                        double value = 0;
                        try {
                            value = result.getDouble("val");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        btn_info.setEnabled(true);
                        arcProgress.setValue(value);
                    }

                    @Override
                    public void onResponseArray(JSONArray arr) {

                    }

                    @Override
                    public void onResponseErrr(String err) {
                        Toast.makeText(mContext, err, Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    btn.setEnabled(true);
                    if (gps == null) {
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

    private void runOverlay_ContinueMethod() {
        ChainTourGuide tourGuide1 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                        .setTitle(getString(R.string.tutorial_title))
                        .setDescription(getString(R.string.tuorial_refresh_meter))
                        //.setGravity(Gravity.BOTTOM)
                        .setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorComplement, null))
                )
                .playLater(btn);

        ChainTourGuide tourGuide3 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                        .setTitle(getString(R.string.tutorial_title))
                        .setDescription(getString(R.string.tutorial_meter_details))
                        //.setGravity(Gravity.BOTTOM)
                        .setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorComplement, null))
                )
                .playLater(btn_info);

        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuide1, tourGuide3)
                .setDefaultOverlay(new Overlay()
                        .setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.tutorial_bg, null))
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                )
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.Overlay)
                .build();

        ChainTourGuide.init(getActivity()).playInSequence(sequence);
    }

}
