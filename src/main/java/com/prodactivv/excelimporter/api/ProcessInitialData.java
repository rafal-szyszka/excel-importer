package com.prodactivv.excelimporter.api;

public class ProcessInitialData {
    public String dataModelName;
    public String dataId;

    public ProcessInitialData() {}
    public ProcessInitialData(String dataModelName, String dataId) {
        this.dataModelName = dataModelName;
        this.dataId = dataId;
    }
}
