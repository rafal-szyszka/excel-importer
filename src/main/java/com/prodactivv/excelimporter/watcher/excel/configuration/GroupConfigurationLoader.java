package com.prodactivv.excelimporter.watcher.excel.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public class GroupConfigurationLoader extends ConfigurationLoader<ProcessedFileRootConfigurationLocation> {

    @Override
    protected Optional<Path> findConfigInLocation(Path fileName) {
        try (
                Stream<Path> pathStream = Files.find(
                        fileName.getParent(),
                        1,
                        (path, basicFileAttributes) -> path.toFile().getName().matches("config-([a-zA-Z0-9_-]+).json")
                )
        ) {
            return pathStream.filter(path -> {
                        String configFileName = path.toFile().getName();
                        return fileName.toFile().getName().startsWith(
                                configFileName.substring(
                                        configFileName.indexOf("-") + 1,
                                        configFileName.lastIndexOf(".")
                                )
                        );
                    })
                    .findFirst();
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
