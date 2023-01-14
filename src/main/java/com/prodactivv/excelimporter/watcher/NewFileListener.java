package com.prodactivv.excelimporter.watcher;

import com.prodactivv.excelimporter.Credentials;
import com.prodactivv.excelimporter.IMessageAreaHandler;
import com.prodactivv.excelimporter.api.ApiClient;
import com.prodactivv.excelimporter.api.SaveFormResult;
import com.prodactivv.excelimporter.utils.ExcelFiles;
import com.prodactivv.excelimporter.watcher.excel.ExcelConfiguration;
import com.prodactivv.excelimporter.watcher.excel.ExcelConfigurationLoader;
import com.prodactivv.excelimporter.watcher.excel.ExcelFileProcessor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public class NewFileListener implements INewEntryInDirectoryListener {

    private final IMessageAreaHandler messageAreaHandler;
    private final String dirPath;

    private final ExcelFileProcessor fileProcessor;
    private final Credentials credentials;

    public NewFileListener(IMessageAreaHandler messageAreaHandler, String dirPath, ExcelFileProcessor fileProcessor, Credentials credentials) {
        this.messageAreaHandler = messageAreaHandler;
        this.dirPath = dirPath;
        this.fileProcessor = fileProcessor;
        this.credentials = credentials;
    }

    @Override
    public Status runForPath(Path pathToFile) {
        messageAreaHandler.setTag(pathToFile.getFileName() + "_log.txt");

        if (!ExcelFiles.isExcelFile(pathToFile)) {
            String fileName = pathToFile.toString();
            messageAreaHandler.addMessage(
                    String.format("Dozwolone formaty plików: %s", ExcelFiles.getExcelExtensions())
            );
            messageAreaHandler.addMessage(
                    String.format("Nieobsługiwany format pliku: %s", fileName.substring(fileName.lastIndexOf(".") + 1))
            );
            return Status.ERROR;
        }

        Optional<ExcelConfiguration> configuration = getConfiguration(pathToFile);
        if (configuration.isEmpty()) {
            return Status.ERROR;
        }

        try {
            ExcelConfiguration excelConfiguration = configuration.get();
            messageAreaHandler.addMessage(String.format("Konfiguracja '%s' załadowana (%s)", excelConfiguration.name(), excelConfiguration.configurations().size()));

            excelConfiguration.configurations().forEach(worksheetConfig -> {
                messageAreaHandler.addMessage("Przetwarzanie: " + worksheetConfig.sheet());
                Optional<List<String>> jsons = fileProcessor.mapExcelToConfiguration(Path.of(dirPath, pathToFile.toString()).toFile(), worksheetConfig);
                jsons.ifPresentOrElse(
                        saveFormJsons -> {
                            messageAreaHandler.addMessage(String.format("Wiersze do importu: %s", saveFormJsons.size()));
                            Map<String, List<SaveFormResult>> results = saveFormJsons.stream()
                                    .map(saveFormJson -> ApiClient.saveForm(credentials, saveFormJson, pathToFile.getFileName().toString()))
                                    .peek(result -> messageAreaHandler.addMessage(result.error().equals("") ? result.message() : result.error() + "\n" + result.jsonResponse()))
                                    .collect(Collectors.teeing(
                                            Collectors.filtering(saveFormResult -> saveFormResult.error().equals(""), Collectors.toList()),
                                            Collectors.filtering(saveFormResult -> !saveFormResult.error().equals(""), Collectors.toList()),
                                            (successes, errors) -> Map.of("successes", successes, "errors", errors)
                                    ));

                            List<SaveFormResult> errors = results.get("errors");
                            if (errors.size() > 0) {
                                errors.forEach(error -> messageAreaHandler.addMessage(error.message()));
                                messageAreaHandler.addMessage("Szczegóły błędów:");
                            }

                            messageAreaHandler.addMessage("Zakończono import!\n\tZaimportowano:\t" + results.get("successes").size() + "\n\tBłędy:\t\t\t" + errors.size());
                        },
                        () -> messageAreaHandler.addMessage("Brak danych do importu!")
                );
            });

            return Status.SUCCESS;

        } catch (Throwable e) {
            messageAreaHandler.addMessage(e.getMessage());
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
