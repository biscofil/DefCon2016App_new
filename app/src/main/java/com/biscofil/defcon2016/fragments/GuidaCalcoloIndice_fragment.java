package com.biscofil.defcon2016.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biscofil.defcon2016.R;

public class GuidaCalcoloIndice_fragment extends Fragment {

    public GuidaCalcoloIndice_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_guida_calcolo_indice, container, false);
        getActivity().setTitle(getString(R.string.calcolo_fragment_title));
        return rootView;
    }
}

