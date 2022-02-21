package com.prodactivv.excelimporter.cli;

import com.prodactivv.excelimporter.Credentials;
import com.prodactivv.excelimporter.IMessageAreaHandler;
import com.prodactivv.excelimporter.api.ApiClient;
import com.prodactivv.excelimporter.exceptions.InvalidCredentialsException;
import com.prodactivv.excelimporter.watcher.NewFileListener;
import com.prodactivv.excelimporter.watcher.excel.ExcelFileProcessor;

import java.io.File;

public class CliApp {

    private final String server;
    private final String user;
    private final String password;
    private final String fileToImport;

    public CliApp(String server, String user, String password, String fileToImport) {
        this.server = server;
        this.user = user;
        this.password = password;
        this.fileToImport = fileToImport;
    }

    public void run() throws InvalidCredentialsException {
        Credentials credentials = ApiClient.getLoginToken(server, user, password)
                .map(token -> new Credentials(server, user, token))
                .orElseThrow(new InvalidCredentialsException("Invalid credentials!"));

        File file = new File(fileToImport);
        new NewFileListener(getMessageAreaHandler(), file.getParentFile().getAbsolutePath(), new ExcelFileProcessor(), credentials)
                .runForPath(file.toPath().getFileName());
    }

    private IMessageAreaHandler getMessageAreaHandler() {
        return new IMessageAreaHandler() {
            @Override
            public void showNewDirectoryInfo(String name) {
                addMessage(name);
            }

            @Override
            public void showDeletedDirectoryInfo(String directory) {
                addMessage(directory);
            }

            @Override
            public void addMessage(String message) {
                System.out.println(message);
            }

            @Override
            public void updateMessage(String message, String constMessage) {
                addMessage(message);
            }
        };
    }
}
