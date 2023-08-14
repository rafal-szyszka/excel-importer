module com.prodactivv.excelimporter {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.fasterxml.jackson.databind;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires com.google.common;
    requires unirest.java;
    requires org.jsoup;
    requires org.json;
    requires commons.cli;

    opens com.prodactivv.excelimporter to javafx.fxml;
    opens com.prodactivv.excelimporter.watcher.excel to com.fasterxml.jackson.databind;
    exports com.prodactivv.excelimporter;
    exports com.prodactivv.excelimporter.api;
    exports com.prodactivv.excelimporter.utils;
    exports com.prodactivv.excelimporter.watcher;
    exports com.prodactivv.excelimporter.watcher.excel.configuration;
    exports com.prodactivv.excelimporter.watcher.excel;
    exports com.prodactivv.excelimporter.watcher.domain;
}