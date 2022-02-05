package com.prodactivv.excelimporter.watcher;

import com.prodactivv.excelimporter.utils.Colors;
import javafx.concurrent.Task;
import javafx.scene.shape.Circle;

import java.nio.file.*;
import java.util.Optional;

public class DirectoryWatcherTask extends Task<Void> {

    private final Path pathToObserve;
    private final INewEntryInDirectoryListener newEntryListener;
    private final IDeletedEntryInDirectoryListener deletedEntryListener;
    private final IModifiedEntryInDirectoryListener modifiedEntryListener;

    private Circle statusDiode;

    public DirectoryWatcherTask(Path pathToObserve, INewEntryInDirectoryListener newEntryListener, IDeletedEntryInDirectoryListener deletedEntryListener, IModifiedEntryInDirectoryListener modifiedEntryListener) {
        this.pathToObserve = pathToObserve;
        this.newEntryListener = newEntryListener;
        this.deletedEntryListener = deletedEntryListener;
        this.modifiedEntryListener = modifiedEntryListener;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected Void call() throws Exception {
        WatchService directoryWatcher = FileSystems.getDefault().newWatchService();
        pathToObserve.register(
                directoryWatcher,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY
        );

        WatchKey key;
        while ((key = directoryWatcher.take()) != null) {
            key.pollEvents().forEach(event -> {
                Optional<Path> newFile = Optional.ofNullable((Path) event.context());

                if (event.kind().name().equals(StandardWatchEventKinds.ENTRY_CREATE.name())) {
                    newFile.ifPresent(file -> {
                        statusDiode.setFill(Colors.RUNNING);
                        IDirectoryListener.Status result = newEntryListener.runForPath(file);
                        if (result != IDirectoryListener.Status.RUNNING) {
                            statusDiode.setFill(Colors.getStatusColor(result));
                        }
                    });
                } else if (event.kind().name().equals(StandardWatchEventKinds.ENTRY_DELETE.name())) {
                    newFile.ifPresent(deletedEntryListener::runForPath);
                } else if (event.kind().name().equals(StandardWatchEventKinds.ENTRY_MODIFY.name())) {
                    newFile.ifPresent(modifiedEntryListener::runForPath);
                }

            });
            if (!key.reset()) break;
        }
        return null;
    }

    public void setStatusDiode(Circle statusDiode) {
        this.statusDiode = statusDiode;
    }
}
