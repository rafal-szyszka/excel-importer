package com.prodactivv.excelimporter.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AppTokenGenerator {

    public static String generateToken(String uuid, String key) throws JsonProcessingException {
        Map<String, String> header = new HashMap<>();
        Map<String, String> payload = new HashMap<>();

        header.put("typ", "sig");
        header.put("alg", "HS512");

        payload.put("app", uuid);

        ObjectMapper mapper = new ObjectMapper();
        String headerString = BaseEncoding.base64().encode(
                mapper.writeValueAsString(header).getBytes(StandardCharsets.UTF_8)
        );

        String payloadString = BaseEncoding.base64().encode(
                mapper.writeValueAsString(payload).getBytes(StandardCharsets.UTF_8)
        );

        String signature = BaseEncoding.base64().encode(
                Hashing.hmacSha512(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"))
                        .hashString(headerString + "." + payloadString, StandardCharsets.UTF_8)
                        .toString()
                        .getBytes(StandardCharsets.UTF_8)
        );

        return String.format("%s.%s.%s", headerString, payloadString, signature);
    }

}
