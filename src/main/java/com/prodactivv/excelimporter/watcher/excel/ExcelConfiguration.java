package com.prodactivv.excelimporter.watcher.excel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ExcelConfiguration(String name, List<WorksheetConfig> configurations) {

}
