package com.biscofil.defcon2016.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.biscofil.defcon2016.EcoMe;
import com.biscofil.defcon2016.R;
import com.biscofil.defcon2016.lib.PageFragment;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.biscofil.defcon2016.lib.Utils.setFragmentContent;

public class Menu_fragment extends PageFragment {

    public Menu_fragment() {
        super(R.layout.fragment_menu, R.string.home_fragment_title, R.id.menu_menu);
    }

    @Override
    public void doWhatever(View rootView, Bundle b) {
        CircleImageView btn_home_meter = (CircleImageView) rootView.findViewById(R.id.home_meter);
        btn_home_meter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragmentContent(Menu_fragment.this, Meter_fragment.class, menuNav.findItem(R.id.menu_meter), false);
            }
        });

        CircleImageView btn_home_map = (CircleImageView) rootView.findViewById(R.id.home_map);
        btn_home_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragmentContent(Menu_fragment.this, Map_fragment.class, menuNav.findItem(R.id.menu_map), false);
            }
        });

        CircleImageView btn_home_licenze = (CircleImageView) rootView.findViewById(R.id.home_licenze);
        btn_home_licenze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragmentContent(Menu_fragment.this, Licenze_fragment.class, menuNav.findItem(R.id.menu_license), false);
            }
        });

        CircleImageView btn_home_guida = (CircleImageView) rootView.findViewById(R.id.home_guida);
        btn_home_guida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragmentContent(Menu_fragment.this, Guida_fragment.class, menuNav.findItem(R.id.menu_guida), false);
            }
        });

        if (((EcoMe) getActivity().getApplication()).offlineMode) {
            btn_home_meter.setEnabled(false);
            btn_home_meter.setColorFilter(Color.argb(127, 255, 255, 255));
            rootView.findViewById(R.id.home_meter_tv).setVisibility(View.INVISIBLE);

            btn_home_map.setEnabled(false);
            btn_home_map.setColorFilter(Color.argb(127, 255, 255, 255));
            rootView.findViewById(R.id.home_map_tv).setVisibility(View.INVISIBLE);

            btn_home_guida.setEnabled(false);
            btn_home_guida.setColorFilter(Color.argb(127, 255, 255, 255));
            rootView.findViewById(R.id.home_guida_tv).setVisibility(View.INVISIBLE);
        }
    }

}
