package com.biscofil.defcon2016;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;


public class Utils {

    public static Fragment setFragmentContent(AppCompatActivity act, Class fragmentClass, boolean first) {
        return setFragmentContent(act.getSupportFragmentManager(), fragmentClass, first);
    }

    public static Fragment setFragmentContent(Fragment f, Class fragmentClass, boolean first) {
        return setFragmentContent(f.getActivity().getSupportFragmentManager(), fragmentClass, first);
    }


    private static Fragment setFragmentContent(FragmentManager fm, Class fragmentClass, boolean first) {
        try {
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

            //if (first) {
            ft.replace(R.id.flContent, fragment).commit();
            /*} else {
                ft.replace(R.id.flContent, fragment).addToBackStack(null).commit();
            }*/
            return fragment;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
