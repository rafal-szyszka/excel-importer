package com.prodactivv.excelimporter;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ServerInfo {
    private final StringProperty visible = new SimpleStringProperty();
    private final StringProperty algorithm = new SimpleStringProperty();

    public ServerInfo(String visible, String algorithm) {
        setVisible(visible);
        setAlgorithm(algorithm);
    }

    public ServerInfo() {
    }

    public static ServerInfo instance(){
        return new ServerInfo();
    }

    public String getVisible() {
        return visible.get();
    }

    public StringProperty visibleProperty() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible.set(visible);
    }

    public String getAlgorithm() {
        return algorithm.get();
    }

    public StringProperty algorithmProperty() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm.set(algorithm);
    }

    @Override
    public String toString() {
        return visible.getValue();
    }
}
