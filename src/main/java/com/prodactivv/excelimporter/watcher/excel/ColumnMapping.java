package com.prodactivv.excelimporter.watcher.excel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ColumnMapping(String column, String techName) {
}
