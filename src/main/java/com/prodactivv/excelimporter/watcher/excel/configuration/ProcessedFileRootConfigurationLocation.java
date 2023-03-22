package com.prodactivv.excelimporter.watcher.excel.configuration;

import java.nio.file.Path;

public class ProcessedFileRootConfigurationLocation implements ConfigurationLocation<Path> {

    private final Path path;

    public ProcessedFileRootConfigurationLocation(Path path) {
        this.path = path;
    }

    @Override
    public Path getLocation() {
        return path;
    }

    public String getLocationString() {
        return path.toAbsolutePath().toString();
    }
}
