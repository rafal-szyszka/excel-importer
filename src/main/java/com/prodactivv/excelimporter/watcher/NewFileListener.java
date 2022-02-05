package com.prodactivv.excelimporter.watcher;

import com.prodactivv.excelimporter.Credentials;
import com.prodactivv.excelimporter.MessageAreaHandler;
import com.prodactivv.excelimporter.api.ApiClient;
import com.prodactivv.excelimporter.api.SaveFormResult;
import com.prodactivv.excelimporter.utils.ExcelFiles;
import com.prodactivv.excelimporter.watcher.excel.ExcelConfiguration;
import com.prodactivv.excelimporter.watcher.excel.ExcelConfigurationLoader;
import com.prodactivv.excelimporter.watcher.excel.ExcelFileProcessor;
import com.prodactivv.excelimporter.watcher.excel.WorksheetConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NewFileListener implements INewEntryInDirectoryListener {

    private final MessageAreaHandler messageAreaHandler;
    private final String dirPath;

    private final ExcelFileProcessor fileProcessor;
    private final Credentials credentials;

    public NewFileListener(MessageAreaHandler messageAreaHandler, String dirPath, ExcelFileProcessor fileProcessor, Credentials credentials) {
        this.messageAreaHandler = messageAreaHandler;
        this.dirPath = dirPath;
        this.fileProcessor = fileProcessor;
        this.credentials = credentials;
    }

    @Override
    public Status runForPath(Path pathToFile) {
        if (ExcelFiles.isExcelFile(pathToFile)) {
            Optional<ExcelConfiguration> configuration = getConfiguration(pathToFile);
            if (configuration.isPresent()) {
                ExcelConfiguration excelConfiguration = configuration.get();
                messageAreaHandler.addMessage(String.format("Konfiguracja '%s' załadowana (%s)", excelConfiguration.name(), excelConfiguration.configurations().size()));
                for (WorksheetConfig worksheetConfig : excelConfiguration.configurations()) {
                    Optional<List<String>> jsons = fileProcessor.mapExcelToConfiguration(Path.of(dirPath, pathToFile.toString()).toFile(), worksheetConfig);
                    jsons.ifPresentOrElse(
                            strings -> {
                                messageAreaHandler.addMessage(String.format("Rows to import: %s", strings.size()));
                                List<String> errors = strings.stream()
                                        .map(saveFormJson -> ApiClient.saveForm(credentials, saveFormJson))
                                        .peek(result -> messageAreaHandler.addMessage(result.error().equals("") ? result.message() : result.error() + "\n" + result.jsonResponse()))
                                        .map(SaveFormResult::error)
                                        .filter(error -> !error.isEmpty())
                                        .toList();

                                messageAreaHandler.addMessage(
                                        String.format("Zakończono import pliku %s\n\t\tBłędów: %s", pathToFile, errors.size())
                                );
                            },
                            () -> messageAreaHandler.addMessage("No data found to import")
                    );
                }
                return Status.SUCCESS;
            }
            return Status.ERROR;
        } else if (pathToFile.toFile().isFile()) {
            String fileName = pathToFile.toString();
            messageAreaHandler.addMessage(
                    String.format("Dozwolone formaty plików: %s", ExcelFiles.getExcelExtensions())
            );
            messageAreaHandler.addMessage(
                    String.format("Nieobsługiwany format pliku: %s", fileName.substring(fileName.lastIndexOf(".") + 1))
            );
            return Status.ERROR;
        }

        return Status.WARNING;
    }

    private Optional<ExcelConfiguration> getConfiguration(Path pathToFile) {
        ExcelConfigurationLoader loader = new ExcelConfigurationLoader();
        try {
            return Optional.ofNullable(loader.loadConfiguration(Path.of(dirPath, pathToFile.toString())));
        } catch (IOException e) {
            messageAreaHandler.addMessage(e.getMessage());
            try {
                Optional<Path> configFilePath = loader.getAllGroupConfigFiles(Path.of(dirPath))
                        .stream()
                        .filter(path -> {
                            String configFileName = path.toFile().getName();
                            return pathToFile.toFile().getName().startsWith(
                                    configFileName.substring(
                                            configFileName.indexOf("-") + 1,
                                            configFileName.lastIndexOf(".")
                                    )
                            );
                        })
                        .findFirst();
                if (configFilePath.isPresent()) {
                    return Optional.ofNullable(loader.loadConfiguration(configFilePath.get()));
                } else {
                    messageAreaHandler.addMessage("Brak konfiguracji grupowej");
                    return getDefaultExcelConfiguration(loader);
                }
            } catch (IOException ex) {
                messageAreaHandler.addMessage(ex.getMessage());
                return getDefaultExcelConfiguration(loader);
            }
        }
    }

    private Optional<ExcelConfiguration> getDefaultExcelConfiguration(ExcelConfigurationLoader loader) {
        try {
            return Optional.ofNullable(loader.loadConfiguration(Path.of(dirPath, ExcelConfigurationLoader.DEFAULT_CONFIG_FILE)));
        } catch (IOException exc) {
            messageAreaHandler.addMessage(exc.getMessage());
        }
        return Optional.empty();
    }
}
