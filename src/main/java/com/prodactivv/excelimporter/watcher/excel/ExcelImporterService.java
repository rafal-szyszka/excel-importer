package com.prodactivv.excelimporter.watcher.excel;

import com.prodactivv.excelimporter.MessageAreaHandler;
import com.prodactivv.excelimporter.Credentials;
import com.prodactivv.excelimporter.watcher.DeletedFileListener;
import com.prodactivv.excelimporter.watcher.DirectoryWatcherTask;
import com.prodactivv.excelimporter.watcher.ModifiedFileListener;
import com.prodactivv.excelimporter.watcher.NewFileListener;
import com.prodactivv.excelimporter.watcher.domain.TraceableWatcherTaskThread;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.util.Pair;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ExcelImporterService {

    private final ObservableMap<UUID, TraceableWatcherTaskThread> runningThreads;
    private final MessageAreaHandler messageAreaHandler;
    private Credentials credentials;

    public ExcelImporterService(ObservableMap<UUID, TraceableWatcherTaskThread> runningThreads, MessageAreaHandler messageAreaHandler, Credentials credentials) {
        this.runningThreads = runningThreads;
        this.messageAreaHandler = messageAreaHandler;
        this.credentials = credentials;
    }

    public Pair<UUID, DirectoryWatcherTask> addWatchedDirectory(File file) {
        UUID uuid = UUID.nameUUIDFromBytes(file.getAbsolutePath().getBytes(StandardCharsets.UTF_8));
        DirectoryWatcherTask watcherTask = new DirectoryWatcherTask(
                file.toPath(),
                new NewFileListener(messageAreaHandler, file.getAbsolutePath(), new ExcelFileProcessor(), credentials),
                new DeletedFileListener(),
                new ModifiedFileListener()
        );
        Thread thread = new Thread(watcherTask);
        runningThreads.put(uuid, new TraceableWatcherTaskThread(watcherTask, thread));

        thread.start();
        return new Pair<>(uuid, watcherTask);
    }

    public void runSingleFile(File file) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                (new NewFileListener(messageAreaHandler, file.getParentFile().getAbsolutePath(), new ExcelFileProcessor(), credentials))
                        .runForPath(file.toPath().getFileName());
                return null;
            }
        };

        (new Thread(task)).start();
    }

    public void killAllWatchers() {
        runningThreads.keySet().forEach(this::killWatcher);
    }

    public void killWatcher(UUID uuid) {
        runningThreads.get(uuid).thread().interrupt();
    }
}
