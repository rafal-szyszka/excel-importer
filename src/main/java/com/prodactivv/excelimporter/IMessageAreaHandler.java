package com.prodactivv.excelimporter;

public interface IMessageAreaHandler {
    void setTag(String tag);

    void showNewDirectoryInfo(String name);

    void showDeletedDirectoryInfo(String directory);

    void addMessage(String message);

    void updateMessage(String message, String constMessage);
}
