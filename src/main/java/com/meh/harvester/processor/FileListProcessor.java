package com.meh.harvester.processor;

import com.google.common.base.Preconditions;
import com.meh.harvester.writer.OutputFileWriter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

import static java.text.MessageFormat.format;

/**
 * Processor that Reads the list of files from a text file to process from.
 * Each line of the file should contain the path string of a file to process.
 */
public class FileListProcessor extends BaseProcessor {

    Path listFile;

    public FileListProcessor(PathMatcher pathMatcher, Path listFile) throws FileNotFoundException {
        super(pathMatcher);
        Preconditions.checkArgument(listFile != null, "listFileName can not be null");
        this.listFile = listFile;
        if (!Files.exists(listFile) || !Files.isRegularFile(listFile)) {
            throw new FileNotFoundException(format("path {0} does not exists or is not a file", listFile));
        }
    }

    /**
     * Processes files listed in {@link #listFile} and output the result of processing via {@code outputFileWriter}.
     * @param outputFileWriter Output file writer to write processing results.
     * @throws IOException
     */
    @Override
    public void process(OutputFileWriter outputFileWriter) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(listFile.toFile()))) {
            String fileName = null;
            while ((fileName = bufferedReader.readLine()) != null) {
                Path filePath = Paths.get(fileName);
                if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                    throw new FileNotFoundException(format("Path {0} does not exists, or is not a file", fileName));
                }
                if (pathMatcher.matches(filePath)) {
                    outputFileWriter.write(filePath);
                }
            }
        }
    }
}
