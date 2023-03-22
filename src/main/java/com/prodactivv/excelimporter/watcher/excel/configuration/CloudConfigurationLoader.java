package com.prodactivv.excelimporter.watcher.excel.configuration;

import java.nio.file.Path;
import java.util.Optional;

public class CloudConfigurationLoader extends ConfigurationLoader<CloudConfigurationLocation> {

    @Override
    protected Optional<Path> findConfigInLocation(CloudConfigurationLocation cloudConfigurationLocation, Path fileName) {

        return Optional.empty();
    }
}
