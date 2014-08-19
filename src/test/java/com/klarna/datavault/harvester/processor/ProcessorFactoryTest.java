package com.klarna.datavault.harvester.processor;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;

import static com.klarna.datavault.harvester.Harvester.InputType;

public class ProcessorFactoryTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test(expected = IllegalArgumentException.class)
    public void pathMatcherNull() throws IOException {
        ProcessorFactory.createProcessor(InputType.DIRECTORY, null, null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void inputPathNull() throws IOException {
        ProcessorFactory.createProcessor(InputType.DIRECTORY,
                FileSystems.getDefault().getPathMatcher("glob:" + "*.*"),
                null);
    }

    @Test
    public void createProcessorDirectory() throws IOException {
        File newFolder = temporaryFolder.newFolder();
        Processor processor = ProcessorFactory.createProcessor(InputType.DIRECTORY,
                FileSystems.getDefault().getPathMatcher("glob:" + "*.*"),
                newFolder.toPath());
        Assert.assertNotNull(processor);
        Assert.assertTrue(processor instanceof DirectoryTreeProcessor);
    }

    @Test
    public void createProcessorFile() throws IOException {
        File newFolder = temporaryFolder.newFile();
        Processor processor = ProcessorFactory.createProcessor(InputType.FILE,
                FileSystems.getDefault().getPathMatcher("glob:" + "*.*"),
                newFolder.toPath());
        Assert.assertNotNull(processor);
        Assert.assertTrue(processor instanceof FileListProcessor);
    }
}
