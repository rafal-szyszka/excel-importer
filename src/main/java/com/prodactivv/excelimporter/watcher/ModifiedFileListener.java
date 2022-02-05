package com.prodactivv.excelimporter.watcher;

import java.nio.file.Path;

public class ModifiedFileListener implements IModifiedEntryInDirectoryListener {
    @Override
    public Status runForPath(Path pathToFile) {
        System.out.format("MODIFIED: %s\n", pathToFile);
        return Status.RUNNING;
    }
}
