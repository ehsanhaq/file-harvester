package com.klarna.datavault.harvester;

import com.google.common.collect.Maps;
import com.klarna.datavault.harvester.writer.types.ConverterFactory;
import org.apache.commons.cli.*;
import org.apache.hadoop.io.SequenceFile;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.util.Arrays;
import java.util.HashMap;

public class SampleTest {
    @Rule
    public TestName name = new TestName();

//    @Test
//    public void testListFileWriter() throws IOException {
//        OutputFileWriter outputFileWriter = new ListFileWriter("list_file_" + name.getMethodName());
//        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + "*.*");
//        HarvestVisitor harvestVisitor = new HarvestVisitor(pathMatcher, outputFileWriter);
//        long startMillis = System.currentTimeMillis();
//        Files.walkFileTree(Paths.get("/home/ehsan/base2"), harvestVisitor);
//        long endMillis = System.currentTimeMillis();
//        System.out.println("Total time for " + name.getMethodName() + ": " + (endMillis-startMillis));
//    }

    @Test
    public void testParse() {
        String [] args = {"-kc", "BINARY", "sjlks","-vc", "BINARY", "-cm", "BLOCK", "-cc", "ksdkskl", "--", "-as", "sdjl", "--", "-sh", "shjk"};
//        String [] args = {"-kc", "BINARY", "-vc", "BINARY", "-cm", "BLOCK", "-cc", "ksdkskl", "shjhaj", "shkjsh"};
        Option KEY_CLASS = OptionBuilder.hasArgs().withArgName("key-class")
                .withDescription("Key class of the sequence file BINARY|TEXT|NULL default is BINARY.").create("kc");
        Option VALUE_CLASS = OptionBuilder.hasArgs(1).withArgName("value-class")
                .withDescription("Value class of the sequence file BINARY|TEXT|NULL default is BINARY.")
                .create("vc");
        Option COMPRESSION_MODE = OptionBuilder.hasArgs(1).withArgName("compression-mode")
                .withDescription("Compression mode NONE|RECORD|BLOCK default is NONE").create("cm");
        Option COMPRESSION_CODEC = OptionBuilder.hasArgs(1).withArgName("compression-codec")
                .withDescription("Compression codec class.").create("cc");
        Options options = new Options()
                .addOption(KEY_CLASS)
                .addOption(VALUE_CLASS)
                .addOption(COMPRESSION_MODE)
                .addOption(COMPRESSION_CODEC);

        CommandLine commandLine = null;
        try {
            commandLine = new BasicParser().parse(options, args);
        } catch (ParseException e) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("cmd", options, true);
        }

        System.out.println(commandLine.getArgList());

        System.out.println(Arrays.asList(commandLine.getOptionValues(KEY_CLASS.getOpt())));
        System.out.println(ConverterFactory.Type.valueOf(commandLine.getOptionValue(VALUE_CLASS.getOpt(), "BINARY")));
        System.out.println(SequenceFile.CompressionType.valueOf(commandLine.getOptionValue(COMPRESSION_MODE
                        .getOpt(),
                "BINARY")));

    }
}
