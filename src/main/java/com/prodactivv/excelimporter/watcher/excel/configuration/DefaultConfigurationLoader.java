package com.prodactivv.excelimporter.watcher.excel.configuration;

import com.prodactivv.excelimporter.watcher.excel.ExcelConfigurationLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class DefaultConfigurationLoader extends ConfigurationLoader<ProcessedFileRootConfigurationLocation> {

    @Override
    protected Optional<Path> findConfigInLocation(ProcessedFileRootConfigurationLocation location, Path fileName) {
        Path configPath = Path.of(location.getLocationString(), ExcelConfigurationLoader.DEFAULT_CONFIG_FILE);

        return Files.exists(configPath) ? Optional.of(configPath) : Optional.empty();
    }
}
