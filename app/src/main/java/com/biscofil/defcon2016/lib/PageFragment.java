package com.biscofil.defcon2016.lib;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.biscofil.defcon2016.MainActivity;

/**
 * Created by bisco on 01/03/2017.
 */

public abstract class PageFragment extends Fragment {

    public Context mContext;
    public Activity mAct;
    public Menu menuNav;

    private int layout_id;
    private int title_string_id;
    private int menu_item_id;

    public PageFragment(int layout, int title, int menu) {
        super();
        layout_id = layout;
        title_string_id = title;
        menu_item_id = menu;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(layout_id, container, false);
        mAct = getActivity();
        mContext = getContext();
        mAct.setTitle(getString(title_string_id));
        menuNav = ((MainActivity) getActivity()).getDrawer().getMenu();
        menuNav.findItem(menu_item_id).setChecked(true);
        doWhatever(rootView, savedInstanceState);
        return rootView;
    }

    public abstract void doWhatever(View v, Bundle savedInstanceState);

    {

    }
}
