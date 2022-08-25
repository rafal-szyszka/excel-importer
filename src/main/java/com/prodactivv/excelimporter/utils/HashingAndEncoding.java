package com.prodactivv.excelimporter.utils;

import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;

import java.nio.charset.StandardCharsets;

public class HashingAndEncoding {

    private static final String secret = "UEGhy3GnjOZeyQKoTNJNsehXFO0o14sQOCX5ycfQJhDfKft53fVz0OPeEN1mKLCL";

    @SuppressWarnings("UnstableApiUsage")
    public static String getSha256Base64Encoded(String value) {
        return BaseEncoding.base64Url()
                .encode(
                        Hashing.sha256()
                                .hashString(value, StandardCharsets.UTF_8)
                                .asBytes()
                );
    }

    @SuppressWarnings("UnstableApiUsage")
    public static String getHmacSha512(String value) {
        return Hashing.hmacSha512(secret.getBytes()).hashString(value, StandardCharsets.UTF_8).toString();
    }
}
