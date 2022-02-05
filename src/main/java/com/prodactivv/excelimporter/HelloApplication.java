package com.prodactivv.excelimporter;

import com.prodactivv.excelimporter.watcher.excel.ExcelImporterService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    private MainViewController controller;

    @Override
    public void start(Stage stage) throws IOException {
        showApp(stage, showLoginAndWaitForCredentials());
    }

    private void showApp(Stage stage, Credentials credentials) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 400);
        stage.setTitle("Bpower2 - Import!");
        stage.setScene(scene);


        controller = fxmlLoader.getController();
        controller.setCredentials(credentials);
        MessageAreaHandler messageAreaHandler = new MessageAreaHandler(controller.getMessageArea());

        controller.setPrimaryStage(stage);
        controller.setMonitoredCatalogsAreaHandler(
                new MonitoredCatalogsAreaHandler(
                        controller.getMonitoredCatalogs(),
                        messageAreaHandler
                )
        );
        controller.setImporterService(new ExcelImporterService(
                FXCollections.observableHashMap(),
                messageAreaHandler,
                credentials
        ));
        stage.show();
    }

    private Credentials showLoginAndWaitForCredentials() throws IOException {
        FXMLLoader loginLoader = new FXMLLoader(HelloApplication.class.getResource("login-popup.fxml"));
        Scene loginScene = new Scene(loginLoader.load(), 300, 220);
        Stage loginStage = new Stage();
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.setTitle("Zaloguj się");
        loginStage.setScene(loginScene);


        LoginPopupController controller = loginLoader.getController();
        loginStage.showAndWait();

        return controller.getCredentials();
    }

    @Override
    public void stop() throws Exception {
        controller.beforeClose();
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}