package com.biscofil.defcon2016.fragments;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.biscofil.defcon2016.R;
import com.biscofil.defcon2016.lib.PageFragment;

import java.util.Locale;

public class Guida_fragment extends PageFragment {

    public Guida_fragment() {
        super(R.layout.fragment_guida, R.string.guida_fragment_title, R.id.menu_guida);
    }

    @Override
    public void doWhatever(View rootView, Bundle sBundle) {
        String lang = Locale.getDefault().getDisplayLanguage();
        WebView guida_webview = (WebView) rootView.findViewById(R.id.guida_webview);
        guida_webview.loadUrl(getString(R.string.web_url) + getString(R.string.webview_guida_url) + "/" + lang);
    }

}
