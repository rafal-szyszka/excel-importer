package com.prodactivv.excelimporter.utils;

import com.prodactivv.excelimporter.watcher.IDirectoryListener;
import javafx.scene.paint.Paint;

public class Colors {

    public static final Paint RUNNING = Paint.valueOf("#00bcd4");
    public static final Paint SUCCESS = Paint.valueOf("#4caf50");
    public static final Paint PARTIAL = Paint.valueOf("#ff9800");
    public static final Paint ERROR = Paint.valueOf("#d32f2f");
    public static final Paint WARNING = Paint.valueOf("#fdd835");

    public static Paint getStatusColor(IDirectoryListener.Status status) {
        return switch (status) {
            case SUCCESS -> Colors.SUCCESS;
            case PARTIAL -> Colors.PARTIAL;
            case ERROR -> Colors.ERROR;
            case RUNNING -> Colors.RUNNING;
            case WARNING -> Colors.WARNING;
        };
    }
}
