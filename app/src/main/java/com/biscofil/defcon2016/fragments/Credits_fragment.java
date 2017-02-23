package com.biscofil.defcon2016.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.biscofil.defcon2016.MainActivity;
import com.biscofil.defcon2016.R;

public class Credits_fragment extends Fragment {

    public Credits_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_credits, container, false);
        getActivity().setTitle(getString(R.string.credits_fragment_title));

        final Menu menuNav = ((MainActivity) getActivity()).getDrawer().getMenu();
        menuNav.findItem(R.id.menu_credits).setChecked(true);

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


        return rootView;
    }

}
