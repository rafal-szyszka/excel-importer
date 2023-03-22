package com.prodactivv.excelimporter.watcher.excel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prodactivv.excelimporter.utils.ExcelFiles;
import com.prodactivv.excelimporter.watcher.excel.configuration.CloudConfigurationLoader;
import com.prodactivv.excelimporter.watcher.excel.configuration.DefaultConfigurationLoader;
import com.prodactivv.excelimporter.watcher.excel.configuration.ExactConfigurationLoader;
import com.prodactivv.excelimporter.watcher.excel.configuration.GroupConfigurationLoader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExcelConfigurationLoader {

    public static final String DEFAULT_CONFIG_FILE = "config.json";

    private final GroupConfigurationLoader groupConfigLoader;
    private final ExactConfigurationLoader exactConfigLoader;
    private final DefaultConfigurationLoader defaultConfigurationLoader;
    private final CloudConfigurationLoader cloudConfigLoader;

    public ExcelConfigurationLoader(GroupConfigurationLoader groupConfigLoader, ExactConfigurationLoader exactConfigLoader, DefaultConfigurationLoader defaultConfigurationLoader, CloudConfigurationLoader cloudConfigLoader) {
        this.groupConfigLoader = groupConfigLoader;
        this.exactConfigLoader = exactConfigLoader;
        this.defaultConfigurationLoader = defaultConfigurationLoader;
        this.cloudConfigLoader = cloudConfigLoader;
    }

    public ExcelConfiguration loadConfiguration(Path pathToExcel) throws IOException {
        Path configPath = Paths.get(ExcelFiles.getFileNameWithoutExtension(pathToExcel) + ".json");
        if (Files.exists(configPath)) {
            Path path = Paths.get(configPath.toUri());
            String json = String.join("\n", Files.readAllLines(path));
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, ExcelConfiguration.class);
        } else {
            throw new FileNotFoundException(String.format("Plik konfiguracyjny %s nie został znaleziony", ExcelFiles.getFileNameWithoutExtension(pathToExcel) + ".json"));
        }
    }

    public List<Path> getAllGroupConfigFiles(Path baseDir) {
        try(
                Stream<Path> pathStream = Files.find(
                        baseDir,
                        1,
                        (path, basicFileAttributes) -> path.toFile().getName().matches("config-([a-zA-Z0-9_-]+).json")
                )
            ) {
            return pathStream.collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
