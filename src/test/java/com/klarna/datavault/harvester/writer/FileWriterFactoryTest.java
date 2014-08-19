package com.klarna.datavault.harvester.writer;


import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.NotImplementedException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Paths;

import static com.klarna.datavault.harvester.writer.FileWriterFactory.*;
import static com.klarna.datavault.harvester.writer.FileWriterFactory.createWriter;

public class FileWriterFactoryTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test(expected = IllegalArgumentException.class)
    public void typeNull() throws IOException, ParseException {
        createWriter(null, Paths.get(""), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void outputFileNull() throws IOException, ParseException {
        createWriter(OutputFormat.SEQUENCE, null, null);
    }

    @Test(expected = NotImplementedException.class)
    public void typeNotImplemented() throws IOException, ParseException {
        createWriter(OutputFormat.ZIP, null, null);
    }

    @Test
    public void createSequenceFileWriter() throws IOException {
        String fileStr = folder.getRoot().getAbsolutePath() + "/outputFile.seq";
        OutputFileWriter fileWriter = createWriter(OutputFormat.SEQUENCE, Paths.get(fileStr), null);
        Assert.assertTrue(fileWriter instanceof SequenceFileWriter);
    }

    @Test
    public void createListFileWriter() throws IOException {
        String fileStr = folder.getRoot().getAbsolutePath() + "/outputFile.seq";
        OutputFileWriter fileWriter = createWriter(OutputFormat.FILE_LIST, Paths.get(fileStr), null);
        Assert.assertTrue(fileWriter instanceof ListFileWriter);
    }
}
