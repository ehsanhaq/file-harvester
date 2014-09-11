package com.meh.harvester;

import org.apache.commons.cli.*;

import java.nio.file.Paths;

import static com.meh.harvester.Harvester.Builder;
import static com.meh.harvester.Harvester.InputType.DIRECTORY;
import static com.meh.harvester.Harvester.InputType.valueOf;
import static com.meh.harvester.writer.FileWriterFactory.OutputFormat.SEQUENCE;

/**
 * Main class, entry point for executing the application.
 */
public class HarvesterMain {

    private static Option INPUT_PATH = OptionBuilder.hasArgs(1).withArgName("input-path").isRequired(true)
            .withDescription("Input path to harvest, must be a directory or file.").create("ip");
    private static Option INPUT_TYPE = OptionBuilder.hasArgs(1).withArgName("input-type")
            .withDescription("Input path type DIRECTORY|FILE default is DIRECTORY.").create("it");
    private static Option FILE_MATCH_EXPRESSION = OptionBuilder.hasArgs(1).withArgName("match")
            .withDescription("Glob expression to match file for harvesting, default is *.* (all files)").create("me");
    private static Option OUTPUT_FILE = OptionBuilder.hasArgs(1).withArgName("output-file").isRequired(true)
            .withDescription("Mandatory outfile file name.").create("f");
    private static Option OUTPUT_FORMAT = OptionBuilder.hasArgs(1).withArgName("output-format")
            .withDescription("Output file format. Supported values are SEQUENCE|ZIP|TAR, default is SEQUENCE")
            .create("of");

    public static void main(String[] args) throws ParseException, Exception {
        Options options = new Options()
                .addOption(INPUT_PATH)
                .addOption(INPUT_TYPE)
                .addOption(FILE_MATCH_EXPRESSION)
                .addOption(OUTPUT_FILE)
                .addOption(OUTPUT_FORMAT);
        CommandLine commandLine = null;
        try {
            commandLine = new BasicParser().parse(options, args);
        } catch(ParseException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar file-harvester-X.X.jar", options);
        }
        Harvester harvester = new Builder()
                .setInputPath(Paths.get(commandLine.getOptionValue(INPUT_PATH.getOpt())))
                .setInputType(valueOf(commandLine.getOptionValue(INPUT_TYPE.getOpt(), DIRECTORY.name())))
                .setFilePattern(commandLine.getOptionValue(FILE_MATCH_EXPRESSION.getOpt(), "*.*"))
                .setOutputFile(Paths.get(commandLine.getOptionValue(OUTPUT_FILE.getOpt())))
                .setOutputFormat(commandLine.getOptionValue(OUTPUT_FORMAT.getOpt(), SEQUENCE.name()))
                .setOtherArguments(commandLine.getArgs())
                .build();
        harvester.harvest();
    }
}
