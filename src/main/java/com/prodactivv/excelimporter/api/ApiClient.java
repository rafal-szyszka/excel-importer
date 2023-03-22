package com.prodactivv.excelimporter.api;

import com.google.common.io.BaseEncoding;
import com.prodactivv.excelimporter.Credentials;
import com.prodactivv.excelimporter.utils.HashingAndEncoding;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

public class ApiClient {

    private static final String LOGIN_ENDPOINT = "/index.php/restApi/generateJWT";
    private static final String SAVE_FORM_ENDPOINT = "/index.php/restApi/forms/method/saveForm/debug/1/groupIndex";
    private static final String START_PROCESS_ENDPOINT = "/index.php/restApi/workflow/method/start/parameters";

    public static Optional<String> getLoginToken(String server, String login, String password) {

        String userKey = String.format("%s:%s", login, password);
//        String userKey = String.format("%s:%s", login, Hashing.sha256().hashString(password, StandardCharsets.UTF_8));
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

            return new SaveFormResult(
                    error,
                    SaveFormEndpointHelper.getMessage(savedFormData),
                    response.toString(),
                    SaveFormEndpointHelper.getModelName(savedFormData),
                    SaveFormEndpointHelper.getId(savedFormData)
            );
        } catch (Throwable e) {
            System.err.println(e.getMessage());
            return new SaveFormResult(e.getMessage(), "", "", "", -1L);
        }
    }

    public static StartProcessResult startProcess(Credentials credentials, StartProcessParameters startParameters) {
        JsonNode response = null;
        try {
            String url = String.format("%s%s/", credentials.server(), START_PROCESS_ENDPOINT);
            url += URLEncoder.encode("{\"configId\":" + startParameters.configId() + "}", StandardCharsets.UTF_8);
            response = Unirest.post(url)
                    .headers(Map.ofEntries(
                            Map.entry("Authorization", credentials.key())
                    ))
                    .body(startParameters.initialData())
                    .asJson()
                    .getBody();

            return new StartProcessResult(
                    Integer.valueOf(response.getObject().getJSONObject("start").getString("instanceId")),
                    Integer.valueOf(startParameters.initialData().dataId)
            );

        } catch (Throwable t) {
            System.err.println(t.getMessage());
            if (response != null) {
                System.err.println(response.toPrettyString());
            }
            return new StartProcessResult(-1, Integer.valueOf(startParameters.initialData().dataId));
        }


    }
}
