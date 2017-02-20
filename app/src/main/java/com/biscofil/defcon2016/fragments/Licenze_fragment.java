package com.biscofil.defcon2016.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.biscofil.defcon2016.R;
import com.biscofil.defcon2016.XhrInterface;
import com.biscofil.defcon2016.lib.Licenza;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class Licenze_fragment extends Fragment {

    public Licenze_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_licenze, container, false);
        getActivity().setTitle(getString(R.string.licenze_fragment_title));
        ListView listView = (ListView) rootView.findViewById(R.id.list_licenze);
        new MyTask(this, getActivity(), listView).execute();
        return rootView;
    }


    private class MyTask extends AsyncTask<Void, Void, List> {

        ListView listView;
        Fragment fra;
        Activity act;

        public MyTask(Fragment f, Activity a, ListView lv) {
            fra = f;
            act = a;
            listView = lv;
        }

        @Override
        protected void onPostExecute(final List l) {
            super.onPostExecute(l);
            LicenzeAdapter adapter = new LicenzeAdapter(act, R.layout.licenze_row, l);
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
        protected List doInBackground(Void... params) {
            try {
                try {
                    String url = getString(R.string.web_url) + getString(R.string.xhr_controller) + getString(R.string.xhr_licenze);
                    JSONArray data = new XhrInterface().getArray(url);
                    List list = new LinkedList();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject object = data.getJSONObject(i);
                        Licenza l = new Licenza();
                        l.parse(object);
                        list.add(l);
                    }
                    return list;
                } catch (Exception e) {
                    Log.e("ECOME", e.getLocalizedMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
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


}
