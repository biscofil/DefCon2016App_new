package com.biscofil.defcon2016.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.biscofil.defcon2016.R;
import com.biscofil.defcon2016.lib.PageFragment;

public class Credits_fragment extends PageFragment {

    public Credits_fragment() {
        super(R.layout.fragment_credits, R.string.credits_fragment_title, R.id.menu_credits);
    }

    @Override
    public void doWhatever(View rootView, Bundle sBundle) {
        Button btn_cabianca = (Button) rootView.findViewById(R.id.btn_cabianca);
        btn_cabianca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(getString(R.string.linkedin_cabianca)));
                startActivity(i);
            }
        });

        Button btn_bisconcin = (Button) rootView.findViewById(R.id.btn_bisconcin);
        btn_bisconcin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(getString(R.string.linkedin_bisconcin)));
                startActivity(i);
            }
        });


        Button btn_busolin = (Button) rootView.findViewById(R.id.btn_busolin);
        btn_busolin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(getString(R.string.linkedin_busolin)));
                startActivity(i);
            }
        });
    }

}
