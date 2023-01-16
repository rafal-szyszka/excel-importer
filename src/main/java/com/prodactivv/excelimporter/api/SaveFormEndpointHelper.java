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
        return savedFormData.getJSONObject("ids")
                .getJSONArray(getModelName(savedFormData))
                .getLong(0);
    }

    public static String getModelName(JSONObject savedFormData) {
        return (String) savedFormData
                .getJSONObject("ids")
                .names()
                .get(0);
    }
}
