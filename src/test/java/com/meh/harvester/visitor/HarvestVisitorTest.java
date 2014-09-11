package com.meh.harvester.visitor;

import com.meh.harvester.writer.OutputFileWriter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.PathMatcher;

import static org.mockito.Mockito.*;

public class HarvestVisitorTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test(expected = IllegalArgumentException.class)
    public void nullMatacher() {
        OutputFileWriter outputFileWriter = mock(OutputFileWriter.class);
        new HarvestVisitor(null, outputFileWriter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void outputFileWriterNull() {
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + "*.*");
        new HarvestVisitor(pathMatcher, null);
    }

    @Test
    public void visitFileWithPattern() throws IOException {
        final int N = 5;
        File[] fileInterested = new File[N];
        File[] fileNotInterested = new File[N];
        OutputFileWriter outputFileWriter = mock(OutputFileWriter.class);
        for (int i=0; i<N; i++) {
            fileInterested[i] = tempFolder.newFile("file" + i + ".interested");
            fileNotInterested[i] = tempFolder.newFile("file" + i + ".notInterested");
        }
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + "*.interested");
        HarvestVisitor harvestVisitor = new HarvestVisitor(pathMatcher, outputFileWriter);
        Files.walkFileTree(tempFolder.getRoot().toPath(), harvestVisitor);
        for (int i=0; i<N; i++) {
            verify(outputFileWriter).write(fileInterested[i].toPath());
        }
        verifyNoMoreInteractions(outputFileWriter);
    }

    @Test
    public void visitFileIgnoreSymbolicFile() throws IOException {
        final int N = 5;
        File[] fileRegular = new File[N];
        OutputFileWriter outputFileWriter = mock(OutputFileWriter.class);
        for (int i=0; i<N; i++) {
            fileRegular[i] = tempFolder.newFile("file" + i + ".regular");
            Files.createSymbolicLink(new File(fileRegular[i].getAbsolutePath() + ".link").toPath(),
                    fileRegular[i].toPath());
        }
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + "*.regular");
        HarvestVisitor harvestVisitor = new HarvestVisitor(pathMatcher, outputFileWriter);
        Files.walkFileTree(tempFolder.getRoot().toPath(), harvestVisitor);
        for (int i=0; i<N; i++) {
            verify(outputFileWriter).write(fileRegular[i].toPath());
        }
        verifyNoMoreInteractions(outputFileWriter);
    }
}
