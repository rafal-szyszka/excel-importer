package com.prodactivv.excelimporter.cli;

import com.prodactivv.excelimporter.Credentials;
import com.prodactivv.excelimporter.api.ApiClient;
import com.prodactivv.excelimporter.exceptions.InvalidCredentialsException;
import com.prodactivv.excelimporter.watcher.DeletedFileListener;
import com.prodactivv.excelimporter.watcher.DirectoryWatcherTask;
import com.prodactivv.excelimporter.watcher.ModifiedFileListener;
import com.prodactivv.excelimporter.watcher.NewFileListener;
import com.prodactivv.excelimporter.watcher.excel.ExcelFileProcessor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CliApp {

    private final String server;
    private final String user;
    private final String password;
    private final String fileToImport;
    private final List<String> dirsToMonitor;


    public CliApp(String server, String user, String password, String fileToImport, String[] dirsToMonitor) {
        this.server = server;
        this.user = user;
        this.password = password;
        this.fileToImport = fileToImport;
        this.dirsToMonitor = dirsToMonitor == null ? new ArrayList<>() : List.of(dirsToMonitor);
    }

    public void run() throws InvalidCredentialsException {
        Credentials credentials = ApiClient.getLoginToken(server, user, password)
                .map(token -> new Credentials(server, user, token))
                .orElseThrow(new InvalidCredentialsException("Invalid credentials!"));

        if (dirsToMonitor != null) {
            monitorDirs(credentials);
        } else {
            File file = new File(fileToImport);
            new NewFileListener(new CliMessageHandler(), file.getParentFile().getAbsolutePath(), new ExcelFileProcessor(), credentials)
                .runForPath(file.toPath().getFileName());
        }
    }

    private void monitorDirs(Credentials credentials) {
        dirsToMonitor.stream()
                .map(File::new)
                .filter(File::isDirectory)
                .forEach(
                        dir -> {
                            DirectoryWatcherTask watcherTask = new DirectoryWatcherTask(
                                    dir.toPath(),
                                    new NewFileListener(new CliMessageHandler(), dir.getAbsolutePath(), new ExcelFileProcessor(), credentials),
                                    new DeletedFileListener(),
                                    new ModifiedFileListener()
                            );

                            try {
                                watcherTask.forceCall();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                );
    }
}
