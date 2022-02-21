package com.prodactivv.excelimporter;

import com.prodactivv.excelimporter.cli.CliApp;
import com.prodactivv.excelimporter.exceptions.InvalidCredentialsException;
import org.apache.commons.cli.*;

public class App {
    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        try {
            CliOptions cliOptions = new CliOptions();
            CommandLine line = parser.parse(cliOptions.getDefaultCommandLineOptions(), args);

            ifRequestedShowHelp(cliOptions, line);

            if (line.hasOption(CliOptions.WINDOW_LESS)) {
                runInClMode(args, parser, cliOptions);
            } else {
                GuiApp.main(args);
            }

        } catch (ParseException | InvalidCredentialsException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void runInClMode(String[] args, CommandLineParser parser, CliOptions cliOptions) throws ParseException, InvalidCredentialsException {
        CommandLine line = parser.parse(cliOptions.getRequiredInWindowLessModeOptions(), args);

        (new CliApp(
                line.getOptionValue(CliOptions.SERVER),
                line.getOptionValue(CliOptions.USER),
                line.getOptionValue(CliOptions.PASSWORD),
                line.getOptionValue(CliOptions.FILE)
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
