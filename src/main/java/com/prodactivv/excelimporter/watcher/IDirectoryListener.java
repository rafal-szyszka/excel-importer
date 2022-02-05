package com.prodactivv.excelimporter.watcher;

import java.nio.file.Path;

public interface IDirectoryListener {

    enum Status {
        SUCCESS, PARTIAL, ERROR, RUNNING, WARNING
    }

    Status runForPath(Path pathToFile);

}
