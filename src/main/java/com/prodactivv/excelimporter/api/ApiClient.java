package com.prodactivv.excelimporter.api;

import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.prodactivv.excelimporter.Credentials;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class ApiClient {

    private static final String LOGIN_ENDPOINT = "/index.php/restApi/generateJWT";
    private static final String SAVE_FORM_ENDPOINT = "/index.php/restApi/forms/method/saveForm";

    public static Optional<String> getLoginToken(String server, String login, String password) {

        String userKey = String.format("%s:%s", login, Hashing.sha256().hashString(password, StandardCharsets.UTF_8));
        userKey = BaseEncoding.base64().encode(userKey.getBytes(StandardCharsets.UTF_8));

        try {
            return Optional.ofNullable(
                    Unirest.post(String.format("%s/%s", server, LOGIN_ENDPOINT))
                            .body(String.format("{\"user-key\":\"%s\"}", userKey))
                            .asJson()
                            .getBody()
                            .getObject()
                            .getString("token")
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static SaveFormResult saveForm(Credentials credentials, String saveFormBody) {
        try {
            JsonNode response = Unirest.post(String.format("%s/%s", credentials.server(), SAVE_FORM_ENDPOINT))
                    .header("Authorization", credentials.key())
                    .body(saveFormBody)
                    .asJson()
                    .getBody();

            String error = response.getObject()
                    .getJSONObject("saveForm")
                    .getString("error");

            String message = Jsoup.clean(
                    response.getObject().getJSONObject("saveForm").getString("mess"),
                    Safelist.none()
            );

            return new SaveFormResult(error, message, response.toString());
        } catch (UnirestException e) {
            return new SaveFormResult(e.getMessage(), "", "");
        }
    }

}
