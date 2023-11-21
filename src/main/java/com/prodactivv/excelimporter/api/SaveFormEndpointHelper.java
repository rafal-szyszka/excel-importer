package com.prodactivv.excelimporter.api;

import kong.unirest.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class SaveFormEndpointHelper {

    public static String getMessage(JSONObject savedFormData) {
        return Jsoup.clean(
                savedFormData.getString("mess"),
                Safelist.none()
        );
    }

    public static Long getId(JSONObject savedFormData) {
        return savedFormData
                .getJSONArray("recordInfo")
                .getJSONObject(0)
                .getLong("id");
    }

    public static String getModelName(JSONObject savedFormData) {
        return savedFormData
                .getJSONArray("recordInfo")
                .getJSONObject(0)
                .getString("model");
    }
}
