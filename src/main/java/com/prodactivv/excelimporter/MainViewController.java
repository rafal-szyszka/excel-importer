package com.prodactivv.excelimporter;

import com.prodactivv.excelimporter.utils.ObservedEntryView;
import com.prodactivv.excelimporter.watcher.DirectoryWatcherTask;
import com.prodactivv.excelimporter.watcher.domain.TraceableWatcherTaskThread;
import com.prodactivv.excelimporter.watcher.excel.ExcelConfigurationLoader;
import com.prodactivv.excelimporter.watcher.excel.ExcelImporterService;
import com.prodactivv.excelimporter.watcher.excel.configuration.CloudConfigurationLoader;
import com.prodactivv.excelimporter.watcher.excel.configuration.DefaultConfigurationLoader;
import com.prodactivv.excelimporter.watcher.excel.configuration.ExactConfigurationLoader;
import com.prodactivv.excelimporter.watcher.excel.configuration.GroupConfigurationLoader;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

public class MainViewController implements Initializable {

    public static final int STATUS_DIODE = 1;

    private static final String[] FILE_EXTENSIONS = {"*.xlsm", "*.xlsx"};
    private static final String EXTENSION_FILTER_DESCRIPTION = "Arkusz programu Microsoft Excel";

    @FXML
    private VBox monitoredCatalogs;
    @FXML
    private TextArea messageArea;
    @FXML
    private Label loggedInAs;

    private Stage primaryStage;
    private ExcelImporterService importerService;
    private ExcelConfigurationLoader configLoader;

    public void importSingleFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter(EXTENSION_FILTER_DESCRIPTION, FILE_EXTENSIONS));
        Optional<File> selectedFile = Optional.ofNullable(fileChooser.showOpenDialog(primaryStage));

        selectedFile.ifPresent(file -> importerService.runSingleFile(file));
    }

    public void chooseCatalogToMonitor() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Optional<File> selectedDirectory = Optional.ofNullable(directoryChooser.showDialog(primaryStage));

        selectedDirectory.ifPresent(file -> {
            Pair<UUID, DirectoryWatcherTask> uuidWatcherPair = importerService.addWatchedDirectory(file);
            HBox monitoredCatalogView = (HBox) createMonitoredCatalogView(file.getAbsolutePath(), uuidWatcherPair.getKey());
            uuidWatcherPair.getValue().setStatusDiode((Circle) monitoredCatalogView.getChildren().get(STATUS_DIODE));
            monitoredCatalogs.getChildren().add(monitoredCatalogView);
        });
    }

    public void deleteLogs() {
        messageArea.clear();
    }

    public void beforeClose() {
        importerService.killAllWatchers();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setMonitoredCatalogsAreaHandler(MonitoredCatalogsAreaHandler monitoredCatalogsAreaHandler) {
        monitoredCatalogsAreaHandler.maintainNoMonitoredDirsMessage();
    }

    public void initImporterService(ObservableMap<UUID, TraceableWatcherTaskThread> observableHashMap, MessageAreaHandler messageAreaHandler, Credentials credentials) {
        importerService = new ExcelImporterService.Builder()
                .setRunningThreads(observableHashMap)
                .setMessageAreaHandler(messageAreaHandler)
                .setCredentials(credentials)
                .setConfigLoader(configLoader)
                .build();
    }

    public TextArea getMessageArea() {
        return messageArea;
    }

    public VBox getMonitoredCatalogs() {
        return monitoredCatalogs;
    }

    private Node createMonitoredCatalogView(String monitoredCatalogPath, UUID uuid) {
        ObservedEntryView observedEntryView = new ObservedEntryView(monitoredCatalogPath);
        observedEntryView.setStopButtonListener(() -> {
            importerService.killWatcher(uuid);
            monitoredCatalogs.getChildren().remove(observedEntryView.getView());
        });
        return observedEntryView.getView();
    }

    public void setCredentials(Credentials credentials) {
        loggedInAs.setText(String.format("Zalogowany jako: %s", credentials.login()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configLoader = new ExcelConfigurationLoader.Builder()
                .setCloudConfigLoader(new CloudConfigurationLoader())
                .setExactConfigLoader(new ExactConfigurationLoader())
                .setGroupConfigLoader(new GroupConfigurationLoader())
                .setDefaultConfigurationLoader(new DefaultConfigurationLoader())
                .build();
    }
}
