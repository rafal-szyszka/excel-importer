package com.prodactivv.excelimporter.api;

import com.prodactivv.excelimporter.Credentials;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.stage.Stage;

import java.util.Optional;

public class ApiLoginTask extends Task<Optional<Credentials>> {

    private final String server;
    private final String login;
    private final String password;
    private final Stage stage;
    private final String algorithm;

    public ApiLoginTask(String server, String login, String password, Stage stage, String algorithm) {
        this.server = server;
        this.login = login;
        this.password = password;
        this.stage = stage;
        this.algorithm = algorithm;
    }

    @Override
    protected Optional<Credentials> call() {
        stage.getScene().setCursor(Cursor.WAIT);
        Optional<String> loginToken = ApiClient.getLoginToken(server, login, password, algorithm);
        Optional<Credentials> credentials = loginToken.flatMap(s -> Optional.of(new Credentials(server, login, s, algorithm)));
        stage.getScene().setCursor(Cursor.DEFAULT);
        return credentials;
    }
}
