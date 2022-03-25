package com.prodactivv.excelimporter.api;

import com.prodactivv.excelimporter.Credentials;
import javafx.concurrent.Task;

import java.util.List;
import java.util.stream.Collectors;

public class ApiUploadFormTask extends Task<List<SaveFormResult>> {

    private final Credentials credentials;
    private final List<String> body;

    public ApiUploadFormTask(Credentials credentials, List<String> body) {
        this.credentials = credentials;
        this.body = body;
    }

    @Override
    protected List<SaveFormResult> call() {
        return body.stream()
                .map(saveFormJson -> ApiClient.saveForm(credentials, saveFormJson, String.valueOf(body.size())))
                .collect(Collectors.toList());
    }
}
