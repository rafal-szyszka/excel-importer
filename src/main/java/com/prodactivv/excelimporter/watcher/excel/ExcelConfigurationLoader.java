package com.prodactivv.excelimporter.watcher.excel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prodactivv.excelimporter.utils.ExcelFiles;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ExcelConfigurationLoader {

    public static final String DEFAULT_CONFIG_FILE = "config.json";

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

    public List<Path> getAllGroupConfigFiles(Path baseDir) throws IOException {
        return Files.find(
                baseDir,
                1,
                (path, basicFileAttributes) -> path.toFile().getName().matches("config-([a-zA-Z0-9_-]+).json")
        ).collect(Collectors.toList());
    }

}
