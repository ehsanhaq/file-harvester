package com.klarna.datavault.harvester.processor;

import com.klarna.datavault.harvester.writer.OutputFileWriter;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class DirectoryTreeProcessorTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test (expected = IllegalArgumentException.class)
    public void pathMatcherNull() throws FileNotFoundException {
        new DirectoryTreeProcessor(null, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void rootPathIsNull() throws FileNotFoundException {
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + "*.*");
        new DirectoryTreeProcessor(pathMatcher, null);
    }

    @Test (expected = FileNotFoundException.class)
    public void whenRootPathDoesNotExists() throws FileNotFoundException {
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + "*.*");
        new DirectoryTreeProcessor(pathMatcher, Paths.get("/does/not/exists"));
    }

    @Test (expected = FileNotFoundException.class)
    public void whenRootPathIsNotADirectory() throws IOException {
        File file = tempFolder.newFile();
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + "*.*");
        new DirectoryTreeProcessor(pathMatcher, file.toPath());
    }

    @Test
    public void processWithEmptyDirectory() throws IOException {
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + "*.*");
        DirectoryTreeProcessor directoryTreeProcessor = new DirectoryTreeProcessor(pathMatcher,
                tempFolder.getRoot().toPath());
        OutputFileWriter outputFileWriter = mock(OutputFileWriter.class);
        directoryTreeProcessor.process(outputFileWriter);
        verifyNoMoreInteractions(outputFileWriter);
    }

    @Test
    public void processWithSingleFileDirectory() throws IOException {
        File file = tempFolder.newFile();
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + "*.*");
        DirectoryTreeProcessor directoryTreeProcessor = new DirectoryTreeProcessor(pathMatcher,
                tempFolder.getRoot().toPath());
        OutputFileWriter outputFileWriter = mock(OutputFileWriter.class);
        directoryTreeProcessor.process(outputFileWriter);
        verify(outputFileWriter).write(file.toPath());
        verifyNoMoreInteractions(outputFileWriter);
    }

    @Test
    public void withMultipleFileInOneDirectory() throws IOException {
        final int N = 10;
        File files [] = new File[N];
        for (int i=0; i<N; i++) {
            files[i] = tempFolder.newFile();
        }
        OutputFileWriter outputFileWriter = mock(OutputFileWriter.class);
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + "*.*");
        DirectoryTreeProcessor directoryTreeProcessor = new DirectoryTreeProcessor(pathMatcher,
                tempFolder.getRoot().toPath());
        directoryTreeProcessor.process(outputFileWriter);
        for (int i=0; i<N; i++) {
            verify(outputFileWriter).write(files[i].toPath());
        }
        verifyNoMoreInteractions(outputFileWriter);
    }

    @Test
    public void withMultipleFileInMultipleDirectories() throws IOException {
        File folder1 = tempFolder.newFolder("folder1");
        File folder2 = tempFolder.newFolder("folder2");
        File folder11 = new File(folder1.getAbsolutePath() + "/folder11");
        folder11.mkdir();
        File files[] = new File[8];
        files[0] = tempFolder.newFile("file.00");
        files[1] = tempFolder.newFile("file.01");
        (files[2] = new File(folder1.getAbsolutePath() + "/file.10")).createNewFile();
        (files[4] = new File(folder1.getAbsolutePath() + "/file.11")).createNewFile();
        (files[3] = new File(folder2.getAbsolutePath() + "/file.20")).createNewFile();
        (files[5] = new File(folder2.getAbsolutePath() + "/file.21")).createNewFile();
        (files[6] = new File(folder2.getAbsolutePath() + "/file.22")).createNewFile();
        (files[7] = new File(folder11.getAbsolutePath() + "/file.110")).createNewFile();

        OutputFileWriter outputFileWriter = mock(OutputFileWriter.class);
        final List<String> pathsCalled = new ArrayList<>();
        for (int i=0; i<8; i++) {
            doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                    pathsCalled.add(invocationOnMock.getArguments()[0].toString());
                    return null;
                }
            }).when(outputFileWriter).write(files[i].toPath());
        }
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + "*.*");
        DirectoryTreeProcessor directoryTreeProcessor = new DirectoryTreeProcessor(pathMatcher,
                tempFolder.getRoot().toPath());
        directoryTreeProcessor.process(outputFileWriter);
        List<String> expected = new ArrayList<>();
        for (int i=0; i<8; i++) {
            verify(outputFileWriter).write(files[i].toPath());
            expected.add(files[i].getAbsolutePath());
        }
        verifyNoMoreInteractions(outputFileWriter);
        Collections.sort(expected);
        Collections.sort(pathsCalled);
        Assert.assertEquals(expected, pathsCalled);
    }
}
