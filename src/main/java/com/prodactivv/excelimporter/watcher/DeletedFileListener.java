package com.prodactivv.excelimporter.watcher;

import java.nio.file.Path;

public class DeletedFileListener implements IDeletedEntryInDirectoryListener {
    @Override
    public Status runForPath(Path pathToFile) {
        System.out.format("DELETED: %s\n", pathToFile);
        return Status.RUNNING;
    }
}
