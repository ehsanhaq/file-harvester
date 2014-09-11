package com.meh.harvester.writer;

import com.google.common.base.Splitter;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListFileWriterTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Path outputFile;

    @Before
    public void setup() {
        outputFile = Paths.get(temporaryFolder.getRoot().getAbsolutePath() + "/output.list");
    }

    @Test(expected = IllegalArgumentException.class)
    public void outPutFileNull() throws IOException {
        new ListFileWriter(null);
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void fileAlreadyExists() throws IOException {
        File outputFile = temporaryFolder.newFile();
        new ListFileWriter(outputFile.toPath());
    }

    @Test
    public void withNoOuputFile() throws IOException {
        new ListFileWriter(outputFile);
        String fileList = FileUtils.readFileToString(outputFile.toFile());
        Assert.assertEquals("", fileList);
    }

    @Test
    public void withSingleInputFile() throws IOException {
        File inputFile = temporaryFolder.newFile();

        OutputFileWriter listFileWriter = new ListFileWriter(outputFile);
        listFileWriter.write(inputFile.toPath());
        String fileList = FileUtils.readFileToString(outputFile.toFile());
        Assert.assertEquals(inputFile.getAbsoluteFile() + "\n", fileList);
    }

    @Test
    public void withMultipleInputFiles() throws IOException {
        File inputFiles [] = new File[10];
        OutputFileWriter listFileWriter = new ListFileWriter(outputFile);
        List<String> expected = new ArrayList<>();
        for (int i=0; i<10; i++) {
            inputFiles[i] = temporaryFolder.newFile();
            listFileWriter.write(inputFiles[i].toPath());
            expected.add(inputFiles[i].getAbsolutePath());
        }

        List<String> actual = new ArrayList<>(
                Splitter.on("\n").omitEmptyStrings().splitToList(FileUtils.readFileToString(outputFile.toFile())));
        Collections.sort(actual);
        Collections.sort(expected);
        Assert.assertEquals(expected, actual);
    }
}
