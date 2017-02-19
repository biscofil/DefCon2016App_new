package com.biscofil.defcon2016;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class LicenzeAdapter extends ArrayAdapter<Licenza> {

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