package com.prodactivv.excelimporter.utils;

import java.nio.file.Path;
import java.util.Locale;

public class ExcelFiles {

    public static boolean isExcelFile(Path pathToFile) {
        String absolutePath = pathToFile.toString();
        absolutePath = absolutePath.substring(absolutePath.lastIndexOf(".") + 1).toLowerCase(Locale.ROOT);
        return absolutePath.equals("xls") || absolutePath.equals("xlsx") || absolutePath.equals("xlsm");
    }

    public static String getFileNameWithoutExtension(Path pathToFile) {
        String pathString = pathToFile.getFileName().toString();
        int dotIndex = pathString.lastIndexOf('.');
        return dotIndex == -1 ? pathString : pathString.substring(0, dotIndex);
    }

    public static String getExcelExtensions() {
        return "xls, xlsx, xlsm";
    }

}
