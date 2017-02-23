package com.biscofil.defcon2016.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.biscofil.defcon2016.MainActivity;
import com.biscofil.defcon2016.R;

public class Guida_fragment extends Fragment {

    public Guida_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_guida, container, false);
        getActivity().setTitle(getString(R.string.guida_fragment_title));

        final Menu menuNav = ((MainActivity) getActivity()).getDrawer().getMenu();
        menuNav.findItem(R.id.menu_guida).setChecked(true);

        WebView guida_webview = (WebView) rootView.findViewById(R.id.guida_webview);
        guida_webview.loadUrl(getString(R.string.web_url) + getString(R.string.webview_guida_url));
        return rootView;
    }

}
