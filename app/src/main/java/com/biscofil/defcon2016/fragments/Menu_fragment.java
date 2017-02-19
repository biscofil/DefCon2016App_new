package com.biscofil.defcon2016.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biscofil.defcon2016.EcoMe;
import com.biscofil.defcon2016.R;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.biscofil.defcon2016.Utils.setFragmentContent;

public class Menu_fragment extends Fragment {

    public Menu_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        getActivity().setTitle("Home");

        CircleImageView btn_home_meter = (CircleImageView) rootView.findViewById(R.id.home_meter);
        btn_home_meter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragmentContent(Menu_fragment.this, Meter_fragment.class, false);
            }
        });

        CircleImageView btn_home_map = (CircleImageView) rootView.findViewById(R.id.home_map);
        btn_home_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragmentContent(Menu_fragment.this, Map_fragment.class, false);
            }
        });

        if (((EcoMe) getActivity().getApplication()).offlineMode) {
            btn_home_meter.setEnabled(false);
            //btn_home_meter.setPaintFlags(btn_home_map.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            btn_home_map.setEnabled(false);
            //btn_home_map.setPaintFlags(btn_home_map.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        CircleImageView btn_home_licenze = (CircleImageView) rootView.findViewById(R.id.home_licenze);
        btn_home_licenze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragmentContent(Menu_fragment.this, Licenze_fragment.class, false);
            }
        });

        CircleImageView btn_home_guida = (CircleImageView) rootView.findViewById(R.id.home_guida);
        btn_home_guida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragmentContent(Menu_fragment.this, Guida_fragment.class, false);
            }
        });


        return rootView;
    }

}
