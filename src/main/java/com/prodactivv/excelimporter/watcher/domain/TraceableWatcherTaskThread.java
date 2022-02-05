package com.prodactivv.excelimporter.watcher.domain;

import com.prodactivv.excelimporter.watcher.DirectoryWatcherTask;

public record TraceableWatcherTaskThread(DirectoryWatcherTask watcherTask, Thread thread) { }
