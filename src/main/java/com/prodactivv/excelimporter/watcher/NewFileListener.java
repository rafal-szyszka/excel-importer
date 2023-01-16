package com.prodactivv.excelimporter.watcher;

import com.prodactivv.excelimporter.Credentials;
import com.prodactivv.excelimporter.IMessageAreaHandler;
import com.prodactivv.excelimporter.api.ApiClient;
import com.prodactivv.excelimporter.api.SaveFormResult;
import com.prodactivv.excelimporter.api.StartProcessParameters;
import com.prodactivv.excelimporter.api.StartProcessResult;
import com.prodactivv.excelimporter.utils.ExcelFiles;
import com.prodactivv.excelimporter.watcher.excel.ExcelConfiguration;
import com.prodactivv.excelimporter.watcher.excel.ExcelConfigurationLoader;
import com.prodactivv.excelimporter.watcher.excel.ExcelFileProcessor;
import com.prodactivv.excelimporter.watcher.excel.WorksheetConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public class NewFileListener implements INewEntryInDirectoryListener {

    public static final String SUCCESSES = "successes";
    public static final String ERRORS = "errors";
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
                            Map<String, List<SaveFormResult>> saveFormResults = saveForms(pathToFile, saveFormJsons);
                            Map<String, List<StartProcessResult>> startProcessResults = startProcesses(worksheetConfig, saveFormResults.get(SUCCESSES));

                            List<SaveFormResult> errors = saveFormResults.get(ERRORS);
                            if (errors.size() > 0) {
                                errors.forEach(error -> messageAreaHandler.addMessage(error.message()));
                                messageAreaHandler.addMessage("Szczegóły błędów:");
                            }

                            messageAreaHandler.addMessage(
                                    "Zakończono import!\n\tZaimportowanych:\t\t\t" +
                                    saveFormResults.get(SUCCESSES).size() +
                                    "\n\tBłędów:\t\t\t\t\t" + errors.size() +
                                    "\n\tUruchomionych procesów:\t\t" +
                                    startProcessResults.get(SUCCESSES).size()
                            );
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

    private Map<String, List<StartProcessResult>> startProcesses(WorksheetConfig worksheetConfig, List<SaveFormResult> saveFormResults) {
        if (worksheetConfig.configId() == null) {
            return Map.of(SUCCESSES, List.of(), ERRORS, List.of());
        }

        messageAreaHandler.addMessage(String.format("Uruchamianie %s procesów", saveFormResults.size()));
        return saveFormResults.stream()
                .map(saveFormResult -> ApiClient.startProcess(credentials, new StartProcessParameters(worksheetConfig.configId(), saveFormResult.modelName(), saveFormResult.id().toString())))
                .peek(startProcessResult -> messageAreaHandler.addMessage(startProcessResult.instanceId() > -1 ?
                        "Uruchomiono proces %s dla rekordu o id %s - id utworzonej instancji: %s".formatted(worksheetConfig.configId(), startProcessResult.recordId(), startProcessResult.instanceId()) :
                        "Nie udało się uruchomić procesu %s dla rekordu o id %s".formatted(worksheetConfig.configId(), startProcessResult.recordId())))
                .collect(Collectors.teeing(
                        Collectors.filtering(startProcessResult -> startProcessResult.instanceId() > -1, Collectors.toList()),
                        Collectors.filtering(startProcessResult -> startProcessResult.instanceId() == -1, Collectors.toList()),
                        (successes, errors) -> Map.of(SUCCESSES, successes, ERRORS, errors)
                ));
    }

    private Map<String, List<SaveFormResult>> saveForms(Path pathToFile, List<String> saveFormJsons) {
        messageAreaHandler.addMessage(String.format("Importowanie %s rekordów", saveFormJsons.size()));
        return saveFormJsons.stream()
                .map(saveFormJson -> ApiClient.saveForm(credentials, saveFormJson, pathToFile.getFileName().toString()))
                .peek(result -> messageAreaHandler.addMessage(result.error().equals("") ? result.message() : result.error() + "\n" + result.jsonResponse()))
                .collect(Collectors.teeing(
                        Collectors.filtering(saveFormResult -> saveFormResult.error().equals(""), Collectors.toList()),
                        Collectors.filtering(saveFormResult -> !saveFormResult.error().equals(""), Collectors.toList()),
                        (successes, errors) -> Map.of(SUCCESSES, successes, ERRORS, errors)
                ));
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
