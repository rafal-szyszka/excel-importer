package com.prodactivv.excelimporter.api;

public class StartProcessParameters {

    private final Integer configId;
    private final ProcessInitialData initialData;

    public StartProcessParameters(Integer configId, String dataModelName, String dataId) {
        this.configId = configId;
        this.initialData = new ProcessInitialData(dataModelName, dataId);
    }

    public Integer configId() {
        return configId;
    }

    public ProcessInitialData initialData() {
        return initialData;
    }
}
