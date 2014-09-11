package com.meh.harvester.writer;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Interface for Output file writer, to process files and output results.
 */
public interface OutputFileWriter {
    /**
     * Called to process {@code inputFile}
     * @param inputFile File to be processed.
     * @throws IOException
     */
    public void write(Path inputFile) throws IOException;

    /**
     * Called when the writer has finished writing and no more writes will happen on this writer.
     * After this method is called {@link #write(java.nio.file.Path)} should not be called.
     * @throws IOException
     */
    public void close() throws IOException;
}