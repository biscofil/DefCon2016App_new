package com.biscofil.defcon2016;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by bisco on 06/12/2016.
 */
public class Struttura implements Serializable {

    public int id;
    public String nome;
    public LatLng lat_lng;
    public String descrizione;
    public String sito_web;
    public String url_img;

    public double punteggio;

    public String data_dati;
}
