package com.biscofil.defcon2016.fragments;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.biscofil.defcon2016.EcoMe;
import com.biscofil.defcon2016.R;

public class Menu_fragment extends Fragment {

    public Menu_fragment() {
    }

    public Fragment setFragmentContent(Class fragmentClass) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            //fragmentManager.beginTransaction().add(fragment, null).addToBackStack(null).commit();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();

            return fragment;
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        getActivity().setTitle("Home");

        Button btn_home_map = (Button) rootView.findViewById(R.id.home_map);
        btn_home_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragmentContent(Map_fragment.class);
            }
        });

        if (((EcoMe) getActivity().getApplication()).offlineMode) {
            btn_home_map.setEnabled(false);
            btn_home_map.setPaintFlags(btn_home_map.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        Button btn_home_credits = (Button) rootView.findViewById(R.id.home_credits);
        btn_home_credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragmentContent(Credits_fragment.class);
            }
        });

        Button btn_home_licenze = (Button) rootView.findViewById(R.id.home_licenze);
        btn_home_licenze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragmentContent(Licenze_fragment.class);
            }
        });

        Button btn_home_guida = (Button) rootView.findViewById(R.id.home_guida);
        btn_home_guida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragmentContent(Guida_fragment.class);
            }
        });


        return rootView;
    }

}
