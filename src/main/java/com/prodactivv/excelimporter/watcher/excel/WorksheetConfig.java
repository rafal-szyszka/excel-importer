package com.prodactivv.excelimporter.watcher.excel;

import java.util.List;

public record WorksheetConfig(
        String sheet,
        Integer formId,
        Boolean transposed,
        Integer startRow,
        String startColumn,
        Integer endRow,
        String endColumn,
        Integer configId,
        List<ColumnMapping> mapping
) { }
