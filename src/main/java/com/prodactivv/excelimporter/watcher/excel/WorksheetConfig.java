package com.prodactivv.excelimporter.watcher.excel;

import java.util.List;

public record WorksheetConfig(
        String sheet,
        Integer formId,
        Integer startRow,
        List<ColumnMapping> mapping
) { }
