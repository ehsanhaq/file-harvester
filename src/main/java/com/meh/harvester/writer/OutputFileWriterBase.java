package com.meh.harvester.writer;

import com.google.common.base.Preconditions;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Base class for implementing OutputFileWriter
 */
public abstract class OutputFileWriterBase implements OutputFileWriter {

    protected File outputFile;

    /**
     * Base constructor creates a new output file. The constructor throws
     * {@link java.nio.file.FileAlreadyExistsException} when the file already exists.
     * @param outputPath The output file name to be created.
     * @throws IOException
     */
    public OutputFileWriterBase(Path outputPath) throws IOException {
        Preconditions.checkArgument(outputPath != null, "outputPath can not be null.");
        if (Files.exists(outputPath)) {
            throw new FileAlreadyExistsException(outputPath + " already exists");
        }
        Files.createFile(outputPath);
        this.outputFile = outputPath.toFile();
    }
}
