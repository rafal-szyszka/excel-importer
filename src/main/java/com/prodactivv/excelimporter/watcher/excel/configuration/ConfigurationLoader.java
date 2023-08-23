package com.prodactivv.excelimporter.watcher.excel.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prodactivv.excelimporter.watcher.excel.ExcelConfiguration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public abstract class ConfigurationLoader<Location> {

    public Optional<ExcelConfiguration> load(Path fileName) {
        Optional<Path> configInLocation = findConfigInLocation(fileName);
        if (configInLocation.isEmpty()) {
            return Optional.empty();
        }

        Path configPath = configInLocation.get();

        if (!Files.exists(configPath)) {
            return Optional.empty();
        }

        try {
            Path path = Paths.get(configPath.toUri());
            String json = String.join("\n", Files.readAllLines(path));
            ObjectMapper objectMapper = new ObjectMapper();
            return Optional.of(objectMapper.readValue(json, ExcelConfiguration.class));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    protected abstract Optional<Path> findConfigInLocation(Path fileName);
}
