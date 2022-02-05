package com.prodactivv.excelimporter.watcher.excel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ExcelFileProcessor {

    private static final String FORM_ID_KEY = "UIF_ID";
    private static final String EASY_FORM_KEY = "easyForm";

    public Optional<List<String>> mapExcelToConfiguration(File excel, WorksheetConfig configuration) {
        List<String> saveFormJsons = new ArrayList<>();
        try (FileInputStream stream = new FileInputStream(excel); Workbook workbook = new XSSFWorkbook(stream)) {
            Sheet sheet = workbook.getSheet(configuration.sheet());
            DataFormatter dataFormatter = new DataFormatter();

            int currentRow = configuration.startRow() - 1;
            int lastRow = sheet.getLastRowNum();

            ObjectMapper mapper = new ObjectMapper();

            while (currentRow <= lastRow) {
                Map<String, Object> saveRowData = new HashMap<>();
                saveRowData.put(FORM_ID_KEY, configuration.formId());
                saveRowData.put(EASY_FORM_KEY, true);
                Row row = sheet.getRow(currentRow++);
                for (ColumnMapping columnMapping : configuration.mapping()) {
                    Cell cell = CellUtil.getCell(row, CellReference.convertColStringToIndex(columnMapping.column()));
                    String cellValue = dataFormatter.formatCellValue(cell);
                    saveRowData.put(columnMapping.techName(), cellValue);
                }
                saveFormJsons.add(mapper.writeValueAsString(saveRowData));
            }

            return Optional.of(saveFormJsons);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
