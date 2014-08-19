package com.klarna.datavault.harvester.processor;

import com.klarna.datavault.harvester.writer.OutputFileWriter;

import java.io.IOException;

/**
 * Interface for processing the input files using the {@link OutputFileWriter} instance.
 */
public interface Processor {

    /**
     * Process input files using the given {@code outputFileWriter}
     * @param outputFileWriter Output file writer to write processing results.
     */
    public void process(OutputFileWriter outputFileWriter) throws IOException;
}
