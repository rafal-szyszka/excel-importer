package com.prodactivv.excelimporter;

import javafx.scene.control.TextArea;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageAreaHandler {

    private final TextArea textArea;

    public MessageAreaHandler(TextArea textArea) {
        this.textArea = textArea;
    }

    public void showNewDirectoryInfo(String name) {
        addMessage(
                String.format("%s %s",
                        "Dodano do obserwacji katalog:",
                        name
                )
        );
    }

    public void showDeletedDirectoryInfo(String directory) {
        addMessage(
                String.format("%s %s",
                        "Usunięto z obserwacji katalog",
                        directory
                )
        );
    }

    public void addMessage(String message) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        textArea.setText(
                String.format("%s\t%s\n%s",
                        dtf.format(now),
                        message,
                        textArea.getText()
                )
        );
    }

    public void updateMessage(String message, String constMessage) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        textArea.setText(
                String.format("%s\t%s\n%s",
                        dtf.format(now),
                        message,
                        constMessage
                )
        );
    }
}
