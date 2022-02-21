package com.prodactivv.excelimporter;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

public class CliOptions {

    public static final String HELP = "h";
    public static final String WINDOW_LESS = "w";
    public static final String FILE = "f";
    public static final String DIRS = "d";
    public static final String USER = "u";
    public static final String PASSWORD = "p";
    public static final String SERVER = "s";
    public static final String HELP_LONG = "help";
    public static final String WINDOW_LESS_LONG = "windowLess";
    public static final String FILE_LONG = "file";
    public static final String DIRS_LONG = "dirs";
    public static final String USER_LONG = "user";
    public static final String PASSWORD_LONG = "password";
    public static final String SERVER_LONG = "server";

    public Options getDefaultCommandLineOptions() {
        Options options = new Options();
        options.addOption(helpOption());
        options.addOption(windowLessOption(false));
        options.addOption(loginOption(false));
        options.addOption(passwordOption(false));
        options.addOption(serverDomainOption(false));
        options.addOptionGroup(fileOrDirOptionGroup(false));
        return options;
    }

    public Options getRequiredInWindowLessModeOptions() {
        Options options = new Options();
        options.addOption(windowLessOption(true));
        options.addOption(loginOption(true));
        options.addOption(passwordOption(true));
        options.addOption(serverDomainOption(true));
        options.addOptionGroup(fileOrDirOptionGroup(true));
        return options;
    }

    private Option helpOption() {
        return Option.builder(HELP)
                .longOpt(HELP_LONG)
                .build();
    }

    private Option windowLessOption(boolean required) {
        return Option.builder(WINDOW_LESS)
                .longOpt(WINDOW_LESS_LONG)
                .hasArg(false)
                .desc("Start application in command line mode.")
                .required(required)
                .build();
    }

    private OptionGroup fileOrDirOptionGroup(boolean required) {
        OptionGroup optionGroup = new OptionGroup();
        optionGroup.addOption(fileToImportOption(false));
//        optionGroup.addOption(directoriesToObserveOption(false));
        optionGroup.setRequired(required);
        return optionGroup;
    }

    private Option fileToImportOption(boolean required) {
        return Option.builder(FILE)
                .longOpt(FILE_LONG)
                .hasArg()
                .desc("Provide name or path to file to import. " +
                        "Directory observers are not allowed in CLI mode due to inability to recognize when program should exit. " +
                        "Being unable to properly close application can cause memory leak and unwanted behavior.")
                .required(required)
                .build();
    }

    private Option directoriesToObserveOption(boolean required) {
        return Option.builder(DIRS)
                .longOpt(DIRS_LONG)
                .hasArgs()
                .desc("Provide names or paths to directories to observe. Use , (comma) as separator. " +
                        "Avoid using whitespaces in paths. If you cannot avoid this, wrap path with \" \" (quotes).")
                .required(required)
                .build();
    }

    private Option loginOption(boolean required) {
        return Option.builder(USER)
                .longOpt(USER_LONG)
                .hasArg(true)
                .desc("User login.")
                .required(required)
                .optionalArg(false)
                .build();
    }

    private Option passwordOption(boolean required) {
        return Option.builder(PASSWORD)
                .longOpt(PASSWORD_LONG)
                .hasArg()
                .desc("User password. Note, password with whitespace at start or at the end, won't work.")
                .required(required)
                .build();
    }

    private Option serverDomainOption(boolean required) {
        return Option.builder(SERVER)
                .longOpt(SERVER_LONG)
                .hasArg()
                .desc("Provide full server domain name with protocol, i.e: https://example.domain.com")
                .required(required)
                .build();
    }

}
