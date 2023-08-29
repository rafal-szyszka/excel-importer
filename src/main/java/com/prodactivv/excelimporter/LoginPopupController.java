package com.prodactivv.excelimporter;

import com.prodactivv.excelimporter.api.ApiLoginTask;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class LoginPopupController {

    @FXML
    private ChoiceBox<ServerInfo> servers;
    @FXML
    private TextField login;
    @FXML
    private PasswordField password;
    @FXML
    private Button loginButton;

    private Credentials credentials;

    public void signIn() {
        loginButton.setDisable(true);
        Stage stage = (Stage) password.getScene().getWindow();

        ApiLoginTask loginTask = new ApiLoginTask(servers.getValue().getVisible(), login.getText(), password.getText(), stage, servers.getValue().getAlgorithm());
        loginTask.addEventHandler(
                WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                e -> loginTask.getValue().ifPresentOrElse(
                        credentials -> {
                            this.credentials = credentials;
                            stage.close();
                        },
                        () -> {
                            loginButton.setDisable(false);
                            password.setBorder(new Border(new BorderStroke(
                                    Color.RED,
                                    BorderStrokeStyle.SOLID,
                                    new CornerRadii(3),
                                    BorderWidths.DEFAULT)
                            ));
                        }
                )
        );

        new Thread(loginTask).start();
    }

    public Credentials getCredentials() {
        return credentials;
    }
}
