package com.biscofil.defcon2016.lib;

import org.json.JSONException;
import org.json.JSONObject;

public class Licenza {

    public int id;
    public String licenza;
    public String link_licenza;
    public String descrizione;

    public Licenza() {
    }

    public void parse(JSONObject obj) {
        try {
            this.id = obj.getInt("id");
            this.licenza = obj.getString("licenza");
            this.link_licenza = obj.getString("link_licenza");
            this.descrizione = obj.getString("descrizione");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
