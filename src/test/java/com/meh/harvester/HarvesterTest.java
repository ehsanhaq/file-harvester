package com.meh.harvester;

import com.google.common.collect.Ordering;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.meh.harvester.Harvester.Builder;
import static com.meh.harvester.Harvester.InputType;
import static com.meh.harvester.writer.FileWriterFactory.OutputFormat.FILE_LIST;

public class HarvesterTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test (expected = IllegalArgumentException.class)
    public void noInputFileOrDirectoryGiven() throws IOException {
        new Builder().build();
    }
    @Test (expected = FileNotFoundException.class)
     public void inputFileNotFound() throws IOException {
        new Builder()
                .setInputType(InputType.FILE)
                .setInputPath(Paths.get("foo"))
                .build();
    }
    @Test (expected = FileNotFoundException.class)
    public void inputDirNotFound() throws IOException {
        new Builder()
                .setInputPath(Paths.get("foo"))
                .build();
    }
    @Test (expected = IllegalArgumentException.class)
    public void outputFileIsNull() throws IOException {
        File file = temporaryFolder.newFile();
        new Builder()
                .setInputType(InputType.FILE)
                .setInputPath(file.toPath())
                .build();
    }
    @Test (expected = FileAlreadyExistsException.class)
    public void outputFileAlreadyExists() throws IOException {
        File inputFile = temporaryFolder.newFile();
        File outputFile = temporaryFolder.newFile();
        new Builder()
                .setInputType(InputType.FILE)
                .setInputPath(inputFile.toPath())
                .setOutputFile(outputFile.toPath())
                .build();
        Files.readAllLines(Paths.get(""), Charset.forName("UTF-8"));
    }

    @Test
    public void simpleScenario() throws Exception {
        Path inputPath = temporaryFolder.newFolder("input").toPath();
        List<String> expected = new ArrayList<>();
        for (int i=0; i<5; i++) {
            File file = new File(inputPath.toFile().getAbsolutePath() + "/file." + i);
            FileUtils.writeStringToFile(file, "This is a test string in file " + i);
            expected.add(file.getAbsolutePath());
        }
        Path outputPath = Paths.get(temporaryFolder.getRoot().getAbsolutePath() + "output");
        Harvester harvester = new Builder()
                .setInputType(InputType.DIRECTORY)
                .setInputPath(inputPath)
                .setOutputFile(outputPath)
                .setOutputFormat(FILE_LIST.name())
                .build();
        harvester.harvest();
        List<String> actual = Ordering.natural().sortedCopy(FileUtils.readLines(outputPath.toFile()));
        Assert.assertEquals(expected, actual);
    }
}
