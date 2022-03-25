package com.prodactivv.excelimporter;

import com.prodactivv.excelimporter.cli.CliApp;
import com.prodactivv.excelimporter.cli.CliMessageHandler;
import com.prodactivv.excelimporter.exceptions.InvalidCredentialsException;
import com.prodactivv.excelimporter.watcher.excel.ExcelImporterService;
import javafx.collections.FXCollections;
import org.apache.commons.cli.*;

import java.io.IOException;

public class App {

    public static void main(String[] args) {
        int exit = -1;
        ExcelImporterService excelImporterService = null;
        CommandLineParser parser = new DefaultParser();
        try {
            CliOptions cliOptions = new CliOptions();
            CommandLine line = parser.parse(cliOptions.getDefaultCommandLineOptions(), args);

            ifRequestedShowHelp(cliOptions, line);

            if (line.hasOption(CliOptions.WINDOW_LESS)) {
                CliMessageHandler messageAreaHandler = new CliMessageHandler();
                excelImporterService = new ExcelImporterService(
                        FXCollections.observableHashMap(), messageAreaHandler, null
                );
                runInClMode(args, parser, cliOptions, excelImporterService);
                System.out.println("Press enter to exit");
                //noinspection UnusedAssignment
                exit = System.in.read();
            } else {
                GuiApp.main(args);
            }

        } catch (ParseException | InvalidCredentialsException | IOException e) {
            System.err.println(e.getMessage());
        }

        if (excelImporterService != null) {
            excelImporterService.killAllWatchers();
        }
    }

    private static void runInClMode(String[] args, CommandLineParser parser, CliOptions cliOptions, ExcelImporterService excelImporterService) throws ParseException, InvalidCredentialsException {
        CommandLine line = parser.parse(cliOptions.getRequiredInWindowLessModeOptions(), args);

        String optionalDirsValue = line.getOptionValue(CliOptions.DIRS);
        (new CliApp(
                line.getOptionValue(CliOptions.SERVER),
                line.getOptionValue(CliOptions.USER),
                line.getOptionValue(CliOptions.PASSWORD),
                line.getOptionValue(CliOptions.FILE),
                optionalDirsValue == null ? null : optionalDirsValue.split(",")
        )).run();
    }

    private static void ifRequestedShowHelp(CliOptions cliOptions, CommandLine line) {
        if (line.hasOption(CliOptions.HELP)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("ExcelImporter", cliOptions.getDefaultCommandLineOptions());
            System.exit(0);
        }
    }
}
