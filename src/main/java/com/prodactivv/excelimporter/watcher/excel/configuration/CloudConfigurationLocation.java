package com.prodactivv.excelimporter.watcher.excel.configuration;

public class CloudConfigurationLocation implements ConfigurationLocation<String> {

    private final String url;

    public CloudConfigurationLocation(String url) {
        this.url = url;
    }

    @Override
    public String getLocation() {
        return url;
    }
}
