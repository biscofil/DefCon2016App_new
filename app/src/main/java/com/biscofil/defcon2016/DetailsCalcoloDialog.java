package com.biscofil.defcon2016;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.biscofil.defcon2016.lib.XhrInterface;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailsCalcoloDialog extends DialogFragment {

    LatLng lat_lng;
    Context context;

    public void setStruttura(LatLng ll) {
        lat_lng = ll;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.dialog_fragment_dettagli_calcolo, null);

        builder.setView(view);
        builder.setNegativeButton(R.string.close_details_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DetailsCalcoloDialog.this.getDialog().cancel();
            }
        });

        String url = getString(R.string.web_url) + getString(R.string.xhr_controller) + getString(R.string.xhr_details_calcolo) + lat_lng.latitude + "/" + lat_lng.longitude;

        final ListView search_ins_listview = (ListView) view.findViewById(R.id.lv_storico);

        final TextView tv_details_pm10 = (TextView) view.findViewById(R.id.tv_details_pm10);
        final TextView tv_details_azoto = (TextView) view.findViewById(R.id.tv_details_azoto);
        final TextView tv_details_ozono = (TextView) view.findViewById(R.id.tv_details_ozono);

        final ProgressBar loading_dialog_details = (ProgressBar) view.findViewById(R.id.loading_dialog_details);

        ((EcoMe) getActivity().getApplication())._xhr_interface.volleyRequestObject(
                url,
                new XhrInterface.VolleyListener() {
                    @Override
                    public void onResponseObject(JSONObject response) {
                        JSONObject dati = null;
                        JSONObject indici = null;
                        try {
                            indici = response.getJSONObject("indici");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            Double azoto = indici.getDouble("sottoindice_azoto");
                            tv_details_azoto.setText(String.format("%.2f", azoto));
                        } catch (JSONException e) {
                            tv_details_azoto.setText("-");
                        }

                        try {
                            Double ozono = indici.getDouble("sottoindice_ozono");
                            tv_details_ozono.setText(String.format("%.2f", ozono));
                        } catch (JSONException e) {
                            tv_details_ozono.setText("-");
                        }

                        try {
                            Double pm10 = indici.getDouble("sottoindice_pm10");
                            tv_details_pm10.setText(String.format("%.2f", pm10));
                        } catch (JSONException e) {
                            tv_details_pm10.setText("-");
                        }

                        try {
                            HistoryAdapter adapter = new HistoryAdapter(getActivity(), response.getJSONArray("raw"));
                            search_ins_listview.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        loading_dialog_details.setVisibility(View.GONE);
                        search_ins_listview.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponseArray(JSONArray arr) {

                    }

                    @Override
                    public void onResponseErrr(String err) {
                        Toast.makeText(getContext(), err, Toast.LENGTH_LONG).show();
                    }
                }
        );

        return builder.create();
    }

    private class HistoryAdapter extends BaseAdapter implements ListAdapter {
        private JSONArray _storico = null;
        private Activity activity = null;

        public HistoryAdapter(Activity activity, JSONArray corsi) {
            assert activity != null;
            assert corsi != null;
            this._storico = corsi;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            if (null == _storico)
                return 0;
            else
                return _storico.length();
        }

        @Override
        public JSONObject getItem(int position) {
            if (null == _storico)
                return null;
            else
                try {
                    return _storico.getJSONObject(position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View v, ViewGroup vg) {
            if (v == null)
                v = activity.getLayoutInflater().inflate(R.layout.item_storico_list, null);
            final JSONObject ai = getItem(position);
            if (null != ai) {
                TextView tv_details_item_valore = (TextView) v.findViewById(R.id.tv_details_item_valore);
                TextView tv_details_item_out = (TextView) v.findViewById(R.id.tv_details_item_out);
                TextView tv_details_item_peso = (TextView) v.findViewById(R.id.tv_details_item_peso);
                TextView tv_details_item_distanza = (TextView) v.findViewById(R.id.tv_details_item_distanza);
                try {
                    tv_details_item_valore.setText(String.format("%.2f", ai.getDouble("val")));
                    tv_details_item_out.setText(String.format("%.2f", ai.getDouble("out")));
                    tv_details_item_peso.setText(String.format("%.2f", ai.getDouble("peso")));
                    tv_details_item_distanza.setText("[ " + String.format("%.2f", ai.getDouble("distanza")) + " Km ]");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return v;
        }
    }

}
