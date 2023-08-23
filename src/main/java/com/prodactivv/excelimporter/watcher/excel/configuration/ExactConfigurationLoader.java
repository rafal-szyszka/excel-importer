package com.prodactivv.excelimporter.watcher.excel.configuration;

import com.prodactivv.excelimporter.utils.ExcelFiles;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ExactConfigurationLoader extends ConfigurationLoader<ProcessedFileRootConfigurationLocation> {

    @Override
    protected Optional<Path> findConfigInLocation(Path fileName) {
        Path configPath = Path.of(
                fileName.getParent().toAbsolutePath().toString(),
                ExcelFiles.getFileNameWithoutExtension(fileName) + ".json"
        );
        return Files.exists(configPath) ? Optional.of(configPath) : Optional.empty();
    }
}
