package com.biscofil.defcon2016.lib;

import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by bisco on 09/02/2017.
 */

public class TutorialHandler {

    SharedPreferences sharedpreferences;

    private static final String cronologia = "classi_visitate";

    public TutorialHandler(SharedPreferences s) {
        sharedpreferences = s;
    }

    private String hash(Class a) {
        return a.getName().toLowerCase();
    }

    private void save(String s) {
        Set<String> classi_visitate = sharedpreferences.getStringSet(cronologia, new HashSet<String>());
        classi_visitate.add(s);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putStringSet(cronologia, classi_visitate);
        editor.commit();
    }

    private boolean exist(String s) {
        Set<String> classi_visitate = sharedpreferences.getStringSet(cronologia, new HashSet<String>());
        return classi_visitate.contains(s);
    }

    @Deprecated
    public boolean isFirstTimeHere(Class c) {
        String s = hash(c);
        boolean first_time = !this.exist(s);
        if (first_time) {
            this.save(s);
        }
        return first_time;
    }

    public boolean isFirstTimeHere(String s) {
        boolean first_time = !this.exist(s);
        if (first_time) {
            this.save(s);
        }
        return first_time;
    }

}
