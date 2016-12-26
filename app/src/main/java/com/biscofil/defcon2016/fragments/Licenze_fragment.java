package com.biscofil.defcon2016.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biscofil.defcon2016.R;

public class Licenze_fragment extends Fragment {

    public Licenze_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_licenze, container, false);
        getActivity().setTitle("BOH");
        return rootView;
    }

}
