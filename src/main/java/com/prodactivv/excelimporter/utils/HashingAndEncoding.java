package com.prodactivv.excelimporter.utils;

import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;

import java.nio.charset.StandardCharsets;

public class HashingAndEncoding {

    @SuppressWarnings("UnstableApiUsage")
    public static String getSha256Base64Encoded(String value) {
        return BaseEncoding.base64Url()
                .encode(
                        Hashing.sha256()
                                .hashString(value, StandardCharsets.UTF_8)
                                .asBytes()
                );
    }

}
