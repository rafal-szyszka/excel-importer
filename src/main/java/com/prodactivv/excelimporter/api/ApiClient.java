package com.prodactivv.excelimporter.api;

import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.prodactivv.excelimporter.Credentials;
import com.prodactivv.excelimporter.utils.HashingAndEncoding;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

public class ApiClient {

    private static final String LOGIN_ENDPOINT = "/index.php/restApi/generateJWT";
    private static final String SAVE_FORM_ENDPOINT = "/index.php/restApi/forms/method/saveForm/debug/1/groupIndex";

    public static Optional<String> getLoginToken(String server, String login, String password) {

//        String userKey = String.format("%s:%s", login, password);
        String userKey = String.format("%s:%s", login, Hashing.sha256().hashString(password, StandardCharsets.UTF_8));
        userKey = BaseEncoding.base64().encode(userKey.getBytes(StandardCharsets.UTF_8));

        try {
            String endpoint = String.format("%s%s", server, LOGIN_ENDPOINT);
            return Optional.ofNullable(
                    Unirest.post(endpoint)
                            .body(String.format("{\"user-key\":\"%s\"}", userKey))
                            .asJson()
                            .getBody()
                            .getObject()
                            .getString("token")
            );
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return Optional.empty();
        }
    }

    public static SaveFormResult saveForm(Credentials credentials, String saveFormBody, String index) {
        try {
            JsonNode response = Unirest.post(String.format("%s%s/%s", credentials.server(), SAVE_FORM_ENDPOINT, index))
                    .headers(Map.ofEntries(
                            Map.entry("Authorization", credentials.key()),
                            Map.entry("checksum", HashingAndEncoding.getHmacSha512(saveFormBody))
                    ))
                    .body(saveFormBody)
                    .asJson()
                    .getBody();

            JSONObject savedFormData = response.getObject().getJSONObject("saveForm");
            String error = savedFormData.getString("error");

            String message;
            Long id;
            try {
                message = getMessage(savedFormData);
                id = getId(savedFormData);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                return new SaveFormResult(e.getMessage(), "", "", -1L);
            }

            return new SaveFormResult(error, message, response.toString(), id);
        } catch (UnirestException e) {
            System.err.println(e.getMessage());
            return new SaveFormResult(e.getMessage(), "", "", -1L);
        }
    }

    private static String getMessage(JSONObject savedFormData) {
        return Jsoup.clean(
                savedFormData.getString("mess"),
                Safelist.none()
        );
    }

    private static Long getId(JSONObject savedFormData) {
        String mainModelName = (String) savedFormData
                .getJSONObject("ids")
                .names()
                .get(0);

        return savedFormData.getJSONObject("ids")
                .getJSONArray(mainModelName)
                .getLong(0);
    }

}
