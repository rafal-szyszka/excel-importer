package com.prodactivv.excelimporter;

import com.prodactivv.excelimporter.cli.CliApp;
import com.prodactivv.excelimporter.cli.CliMessageHandler;
import com.prodactivv.excelimporter.exceptions.InvalidCredentialsException;
import org.apache.commons.cli.*;

import java.io.*;

public class App {

    public static void main(String[] args) {
        int exit = -1;
        CommandLineParser parser = new DefaultParser();
        try {
            CliOptions cliOptions = new CliOptions();
            CommandLine line = parser.parse(cliOptions.getDefaultCommandLineOptions(), args);

            ifRequestedShowHelp(cliOptions, line);

            if (line.hasOption(CliOptions.WINDOW_LESS)) {
                CliMessageHandler messageAreaHandler = new CliMessageHandler();
                runInClMode(args, parser, cliOptions);
                System.out.println("Press enter to exit");
                //noinspection UnusedAssignment
                exit = System.in.read();
            } else {
                GuiApp.main(args);
            }

        } catch (ParseException | InvalidCredentialsException | IOException e) {
            System.err.println(e.getMessage());
        }

    }

    private static PrintStream outputFile(String filename) throws FileNotFoundException {
        return new PrintStream(new BufferedOutputStream(new FileOutputStream(filename)), true);
    }

    private static void runInClMode(String[] args, CommandLineParser parser, CliOptions cliOptions) throws ParseException, InvalidCredentialsException {
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
