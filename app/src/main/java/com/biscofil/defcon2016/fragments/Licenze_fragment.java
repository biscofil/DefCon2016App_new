package com.biscofil.defcon2016.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.biscofil.defcon2016.EcoMe;
import com.biscofil.defcon2016.R;
import com.biscofil.defcon2016.lib.Libreria;
import com.biscofil.defcon2016.lib.Licenza;
import com.biscofil.defcon2016.lib.PageFragment;
import com.biscofil.defcon2016.lib.XhrInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Licenze_fragment extends PageFragment {

    ListView listView, lv_librerie;

    public Licenze_fragment() {
        super(R.layout.fragment_licenze, R.string.licenze_fragment_title, R.id.menu_license);
    }

    @Override
    public void doWhatever(View rootView, Bundle sBundle) {
        listView = (ListView) rootView.findViewById(R.id.list_licenze);
        lv_librerie = (ListView) rootView.findViewById(R.id.lv_librerie);

        final List<Libreria> l = new ArrayList<>();

        l.add(new Libreria("CircleProgress", "https://github.com/lzyzsd/CircleProgress", "DWT*YW License"));
        l.add(new Libreria("MPAndroidChart", "https://github.com/PhilJay/MPAndroidChart", "Apache License V2.0"));
        l.add(new Libreria("CircleImageView", "https://github.com/hdodenhof/CircleImageView", "Apache License V2.0"));

        LibrerieAdapter adapter = new LibrerieAdapter(getContext(), R.layout.licenze_row, l);
        lv_librerie.setAdapter(adapter);

        lv_librerie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Libreria o = l.get(position);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(o.url_licenza));
                startActivity(i);
            }
        });

        ((EcoMe) getActivity().getApplication())._xhr_interface.volleyRequestArray(
                getString(R.string.web_url) + getString(R.string.xhr_controller) + getString(R.string.xhr_licenze),
                new XhrInterface.VolleyListener() {
                    @Override
                    public void onResponseObject(JSONObject obj) {

                    }

                    @Override
                    public void onResponseArray(JSONArray data) {
                        final List l = new LinkedList();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object = null;
                            try {
                                object = data.getJSONObject(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Licenza lic = new Licenza();
                            lic.parse(object);
                            l.add(lic);
                        }

                        LicenzeAdapter adapter = new LicenzeAdapter(mAct, R.layout.licenze_row, l);
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Licenza o = (Licenza) l.get(position);
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(o.link_licenza));
                                startActivity(i);
                            }
                        });

                    }

                    @Override
                    public void onResponseErrr(String err) {
                        Toast.makeText(getContext(), err, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private class LicenzeAdapter extends ArrayAdapter<Licenza> {

        public LicenzeAdapter(Context context, int textViewResourceId, List<Licenza> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.licenze_row, null);
            TextView nome = (TextView) convertView.findViewById(R.id.textViewName);
            TextView numero = (TextView) convertView.findViewById(R.id.textViewNumber);
            Licenza c = getItem(position);
            nome.setText(c.id + ". " + c.descrizione);
            numero.setText(c.licenza);
            return convertView;
        }

    }

    private class LibrerieAdapter extends ArrayAdapter<Libreria> {

        public LibrerieAdapter(Context context, int textViewResourceId, List<Libreria> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.licenze_row, null);
            TextView nome = (TextView) convertView.findViewById(R.id.textViewName);
            TextView numero = (TextView) convertView.findViewById(R.id.textViewNumber);
            Libreria c = getItem(position);
            nome.setText(c.nome);
            numero.setText(c.nome_licenza);
            return convertView;
        }

    }


}
