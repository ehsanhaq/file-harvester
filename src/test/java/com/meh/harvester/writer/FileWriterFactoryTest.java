package com.meh.harvester.writer;


import org.apache.commons.lang.NotImplementedException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Paths;

import static com.meh.harvester.writer.FileWriterFactory.OutputFormat;
import static com.meh.harvester.writer.FileWriterFactory.createWriter;

public class FileWriterFactoryTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test(expected = IllegalArgumentException.class)
    public void typeNull() throws Exception {
        createWriter(null, Paths.get(""), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void outputFileNull() throws Exception {
        createWriter(OutputFormat.SEQUENCE, null, null);
    }

    @Test(expected = NotImplementedException.class)
    public void typeNotImplemented() throws Exception {
        createWriter(OutputFormat.ZIP, null, null);
    }

    @Test
    public void createSequenceFileWriter() throws Exception {
        String fileStr = folder.getRoot().getAbsolutePath() + "/outputFile.seq";
        OutputFileWriter fileWriter = createWriter(OutputFormat.SEQUENCE, Paths.get(fileStr), null);
        Assert.assertTrue(fileWriter instanceof SequenceFileWriter);
    }

    @Test
    public void createListFileWriter() throws Exception {
        String fileStr = folder.getRoot().getAbsolutePath() + "/outputFile.seq";
        OutputFileWriter fileWriter = createWriter(OutputFormat.FILE_LIST, Paths.get(fileStr), null);
        Assert.assertTrue(fileWriter instanceof ListFileWriter);
    }
}
