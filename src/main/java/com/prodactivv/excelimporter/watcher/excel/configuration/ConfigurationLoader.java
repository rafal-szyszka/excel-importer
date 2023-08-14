package com.prodactivv.excelimporter.watcher.excel.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prodactivv.excelimporter.utils.ExcelFiles;
import com.prodactivv.excelimporter.watcher.excel.ExcelConfiguration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public abstract class ConfigurationLoader<Location> {

    public Optional<ExcelConfiguration> load(Location location, Path fileName) {
        Optional<Path> configInLocation = findConfigInLocation(location, fileName);
        if (configInLocation.isEmpty()) {
            return Optional.empty();
        }

        Path configPath = Paths.get(ExcelFiles.getFileNameWithoutExtension(configInLocation.get()) + ".json");

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
    protected abstract Optional<Path> findConfigInLocation(Location location, Path fileName);
}
