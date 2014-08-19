package com.klarna.datavault.harvester.writer;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.NotImplementedException;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Factory for creating Output file writers.
 */
public class FileWriterFactory {

    private FileWriterFactory() {}

    public enum OutputFormat {
        SEQUENCE,
        ZIP,
        TAR,
        FILE_LIST
    }

    /**
     * Creates an instance of a class which implements @see OutputFileWriter.
     * @param type Enumerated type to decide which concrete class to createWriter.
     * @param outputPath Output file for the new instance of @see OutputFileWriter.
     * @param otherArguments Additional parameters for the writer.
     * @return an instance of a class that implements @OutputFileWriter, based on the @see type.
     * @throws IOException
     */
    public static OutputFileWriter createWriter(OutputFormat type, Path outputPath, String[] otherArguments)
            throws IOException {
        Preconditions.checkArgument(type != null, "type can not be null");
        switch (type) {
            case SEQUENCE:
                return new SequenceFileWriter.Builder(outputPath).setOtherArguments(otherArguments).build();
            case FILE_LIST:
                return new ListFileWriter(outputPath);
            default:
                throw new NotImplementedException(type + " is not implemented.");
        }
    }
}
