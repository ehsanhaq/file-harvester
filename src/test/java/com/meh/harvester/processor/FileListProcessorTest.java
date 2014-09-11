package com.meh.harvester.processor;

import com.google.common.io.Files;
import com.meh.harvester.writer.OutputFileWriter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

import static org.mockito.Mockito.mock;

public class FileListProcessorTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test (expected = IllegalArgumentException.class)
    public void pathMatcherNull() throws FileNotFoundException {
        new FileListProcessor(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void listFileNameIsNull() throws FileNotFoundException {
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + "*.*");
        new FileListProcessor(pathMatcher, null);
    }

    @Test (expected = FileNotFoundException.class)
    public void listFileDoesNotExists() throws FileNotFoundException {
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + "*.*");
        new FileListProcessor(pathMatcher, Paths.get("/does/not/exists"));
    }

    @Test (expected = FileNotFoundException.class)
    public void listFileIsNotaFile() throws FileNotFoundException {
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + "*.*");
        new FileListProcessor(pathMatcher, temporaryFolder.getRoot().toPath());
    }

    @Test (expected = FileNotFoundException.class)
    public void listFileContainsPathThatDoNotExists() throws IOException {
        File file = temporaryFolder.newFile();
        Files.write("/do/not/exist".getBytes(), file);
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + "*.*");
        FileListProcessor fileListProcessor = new FileListProcessor(pathMatcher, file.toPath());
        OutputFileWriter outputFileWriter = mock(OutputFileWriter.class);
        fileListProcessor.process(outputFileWriter);
    }

    @Test (expected = FileNotFoundException.class)
    public void listFileContainsPathThatIsNotFile() throws IOException {
        File file = temporaryFolder.newFile();
        Files.write(temporaryFolder.getRoot().getAbsolutePath().getBytes(), file);
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + "*.*");
        FileListProcessor fileListProcessor = new FileListProcessor(pathMatcher, file.toPath());
        OutputFileWriter outputFileWriter = mock(OutputFileWriter.class);
        fileListProcessor.process(outputFileWriter);
    }
}
