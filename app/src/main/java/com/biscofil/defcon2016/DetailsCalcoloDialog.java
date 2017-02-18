package com.biscofil.defcon2016;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by bisco on 18/02/2017.
 */

public class DetailsCalcoloDialog extends DialogFragment {

    LatLng lat_lng;
    Context context;

    public void setStruttura(LatLng ll) {
        lat_lng = ll;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // get context
        context = getActivity().getApplicationContext();
        // make dialog object
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // get the layout inflater
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflate our custom layout for the dialog to a View
        View view = li.inflate(R.layout.dialog_fragment_dettagli_calcolo, null);
        // inform the dialog it has a custom View

        builder.setTitle("dettagli calcolo");
        builder.setView(view);
        builder.setPositiveButton("Annulla", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DetailsCalcoloDialog.this.getDialog().cancel();
            }
        });


        //((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        // and if you need to call some method of the class

        //MyCustomView myView = (MyCustomView) view.findViewById(R.id.custom_id_in_my_custom_view);
        //myView.doSome("stuff");

        // create the dialog from the builder then show
/*
        String url = "url.../" + lat_lng.latitude + "/" + lat_lng.longitude;

        final ListView search_ins_listview = (ListView) view.findViewById(R.id.search_cor_listview);

        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        adapter = new SearchAdapter(getActivity(), response);
                        search_ins_listview.setAdapter(adapter);

                        search_ins_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                mListener.onDialogPositiveClick(CorsoPicker.this, (JSONObject) parent.getItemAtPosition(position));
                                CorsoPicker.this.dismiss();
                            }
                        });

                        search_edittext.setEnabled(true);

                        progressbar.setVisibility(View.GONE);
                        search_edittext.setVisibility(View.VISIBLE);
                        search_ins_listview.setVisibility(View.VISIBLE);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

// Access the RequestQueue through your singleton class.
        EcoMe.getInstance().addToRequestQueue(jsObjRequest);
*/
        return builder.create();
    }

    /*
    private class SearchAdapter extends BaseAdapter implements ListAdapter {
        private JSONArray corsi = null;
        private Activity activity = null;

        private JSONArray filteredData;    // Values to be displayed

        public SearchAdapter(Activity activity, JSONArray corsi) {
            assert activity != null;
            assert corsi != null;

            this.corsi = corsi;
            this.filteredData = corsi;
            this.activity = activity;

        }

        @Override
        public int getCount() {
            if (null == filteredData)
                return 0;
            else
                return filteredData.length();
        }

        @Override
        public JSONObject getItem(int position) {
            if (null == filteredData)
                return null;
            else
                try {
                    return filteredData.getJSONObject(position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            return null;
        }

        @Override
        public long getItemId(int position) {
            //JSONObject jsonObject = getItem(position);
            return position; //jsonObject.optLong("AF_ID");
        }

        @Override
        public View getView(int position, View v, ViewGroup vg) {
            if (v == null)
                v = activity.getLayoutInflater().inflate(R.layout.item_corso_search_list, null);

            final JSONObject ai = getItem(position);

            if (null != ai) {
                //Log.d("BISCO", ai.toString());

                TextView tv_cds_cod = (TextView) v.findViewById(R.id.tv_cds_cod);
                TextView tv_cds_des = (TextView) v.findViewById(R.id.tv_cds_des);
                TextView tv_tipo_corso_cod = (TextView) v.findViewById(R.id.tv_tipo_corso_cod);
                TextView tv_pds_cod = (TextView) v.findViewById(R.id.tv_pds_cod);

                try {
                    tv_cds_cod.setText("[" + ai.getString("CDS_COD") + "]");
                    tv_cds_des.setText(ai.getString("CDS_DES"));
                    tv_tipo_corso_cod.setText(ai.getString("TIPO_CORSO_COD"));
                    tv_pds_cod.setText(ai.getString("PDS_COD"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return v;
        }
    }*/

}
