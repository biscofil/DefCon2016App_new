package com.biscofil.defcon2016.fragments;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.biscofil.defcon2016.R;
import com.biscofil.defcon2016.lib.PageFragment;

import java.util.Locale;

public class GuidaCalcoloIndice_fragment extends PageFragment {

    public GuidaCalcoloIndice_fragment() {
        super(R.layout.fragment_guida_calcolo_indice, R.string.calcolo_fragment_title, R.id.menu_calc);
    }

    @Override
    public void doWhatever(View rootView, Bundle sBundle) {
        String lang = Locale.getDefault().getDisplayLanguage();
        WebView calcolo_webview = (WebView) rootView.findViewById(R.id.calcolo_webview);
        calcolo_webview.loadUrl(getString(R.string.web_url) + getString(R.string.webview_calcolo_url) + "/" + lang);
    }
}

