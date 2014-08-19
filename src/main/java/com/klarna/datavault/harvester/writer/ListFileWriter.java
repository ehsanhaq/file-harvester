package com.klarna.datavault.harvester.writer;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Class to list the file tree in an output file.
 */
public class ListFileWriter extends OutputFileWriterBase {

    public ListFileWriter(Path outputPath) throws IOException {
        super(outputPath);
    }

    /**
     * Appends the path string of the {@code inputFile} to the output file {@code outputFile}.
     * @param inputFile Path of the file to be written in the output file {@code outputFile}.
     * @throws IOException
     */
    @Override
    public void write(Path inputFile) throws IOException {
        String fileName = inputFile.toString();
        FileUtils.write(outputFile, fileName + "\n", true);
    }

    @Override
    public void close() throws IOException {

    }
}
