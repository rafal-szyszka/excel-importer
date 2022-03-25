package com.prodactivv.excelimporter.cli;

import com.prodactivv.excelimporter.IMessageAreaHandler;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CliMessageHandler implements IMessageAreaHandler {

    private String tag;

    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }

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

        try {
            File file = new File("./" + tag);
            boolean fileExists = file.exists();
            if (!fileExists) {
                fileExists = file.createNewFile();
            }

            if (fileExists) {
                RandomAccessFile _file = new RandomAccessFile(file, "rw");
                _file.seek(_file.length());

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                message = String.format("%s\t%s\n", dtf.format(now), message);

                _file.writeBytes(message);
                _file.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void updateMessage(String message, String constMessage) {
        addMessage(message);
    }
}
