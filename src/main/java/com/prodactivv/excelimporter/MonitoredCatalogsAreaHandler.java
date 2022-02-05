package com.prodactivv.excelimporter;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public record MonitoredCatalogsAreaHandler(VBox monitoredDirsArea, MessageAreaHandler messageAreaHandler) {

    public void maintainNoMonitoredDirsMessage() {
        monitoredDirsArea.getChildren()
                .addListener((ListChangeListener<? super Node>) removed -> {
                    if (removed.next() && !removed.wasAdded()) {
                        ObservableList<? extends Node> list = removed.getList();
                        if (removed.wasRemoved()) {
                            removed.getRemoved().forEach(node -> {
                                if (node instanceof HBox hBox) {
                                    messageAreaHandler.showDeletedDirectoryInfo(((Label) hBox.getChildren().get(0)).getText());
                                }
                            });

                            if (list.size() == 0) {
                                Label noMonitoredCatalogInfo = new Label("Aktualnie nie monitorujesz żadnego katalogu.");
                                noMonitoredCatalogInfo.setWrapText(true);
                                noMonitoredCatalogInfo.setTextAlignment(TextAlignment.CENTER);
                                monitoredDirsArea.getChildren().add(noMonitoredCatalogInfo);
                            }
                        }
                    }
                });

        monitoredDirsArea.getChildren()
                .addListener(
                        (ListChangeListener<? super Node>) addedOrRemoved -> {
                            if (addedOrRemoved.next()) {
                                ObservableList<? extends Node> list = addedOrRemoved.getList();
                                if (addedOrRemoved.wasAdded()) {
                                    addedOrRemoved.getAddedSubList().forEach(node -> {
                                        if (node instanceof HBox hBox) {
                                            messageAreaHandler.showNewDirectoryInfo(((Label) hBox.getChildren().get(0)).getText());
                                        }
                                    });
                                }

                                if (list.size() > 1 && list.get(0) instanceof Label) {
                                    Platform.runLater(() -> monitoredDirsArea.getChildren().remove(list.get(0)));
                                }
                            }
                        }
                );
    }
}
