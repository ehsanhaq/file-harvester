package com.meh.harvester;

import com.google.common.base.Preconditions;
import com.meh.harvester.processor.Processor;
import com.meh.harvester.processor.ProcessorFactory;
import com.meh.harvester.writer.OutputFileWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;

import static com.meh.harvester.writer.FileWriterFactory.OutputFormat;
import static com.meh.harvester.writer.FileWriterFactory.createWriter;
import static java.text.MessageFormat.format;

/**
 * Harvester class that glues different parts of the application and executes
 * the harvesting.
 */
public class Harvester {
    private Path inputPath;
    private InputType inputType;

    private PathMatcher pathMatcher;

    private OutputFormat outputFormat;
    private Path outputFile;

    private String [] otherArguments;

    /** Private constructor to be called from the builder **/
    private Harvester(Path inputPath, InputType inputType, PathMatcher filePattern, Path outputFile,
                      OutputFormat outputFormat, String [] otherArguments) {
        this.inputPath = inputPath;
        this.inputType = inputType;

        this.outputFile = outputFile;
        this.outputFormat = outputFormat;

        this.pathMatcher = filePattern;
        this.otherArguments = otherArguments;
    }

    /**
     * Executes the file harvesting process.
     * @throws Exception
     */
    public void harvest() throws Exception {
        Processor processor = ProcessorFactory.createProcessor(inputType, pathMatcher, inputPath);
        OutputFileWriter outputFileWriter = createWriter(outputFormat, outputFile, otherArguments);
        processor.process(outputFileWriter);
        outputFileWriter.close();
    }

    public enum InputType {
        DIRECTORY,
        FILE
    }

    /**
     * Builder class for creating Harvester instance.
     */
    public static class Builder {
        Path inputPath = null;
        InputType inputType = InputType.DIRECTORY;

        PathMatcher filePattern = FileSystems.getDefault().getPathMatcher("glob:" + "*.*");

        Path outputFile = null;
        OutputFormat outputFormat = OutputFormat.SEQUENCE;

        String [] otherArguments;

        public Builder setInputPath(Path inputPath) {
            this.inputPath = inputPath;
            return this;
        }
        public Builder setInputType(InputType inputType) {
            this.inputType = inputType;
            return this;
        }
        public Builder setFilePattern(String filePattern) {
            this.filePattern = FileSystems.getDefault().getPathMatcher("glob:" + filePattern);
            return this;
        }
        public Builder setOutputFile(Path outputFile) {
            this.outputFile = outputFile;
            return this;
        }
        public Builder setOutputFormat(String outputFormat) {
            this.outputFormat = OutputFormat.valueOf(outputFormat);
            return this;
        }
        public Builder setOtherArguments(String[] otherArguments) {
            this.otherArguments = otherArguments;
            return this;
        }

        public Harvester build() throws IOException {
            Preconditions.checkArgument(inputPath != null, "Input Path must not be null.");
            if (inputType == InputType.FILE) {
                if(!Files.exists(inputPath) || !Files.isRegularFile(inputPath)) {
                    throw new FileNotFoundException(format("path {0} does not exists or is not a file", inputPath));
                }
            } else if (inputType == InputType.DIRECTORY) {
                if (!Files.exists(this.inputPath) || !Files.isDirectory(this.inputPath)) {
                    throw new FileNotFoundException(format("path {0} does not exists or is not a directory", this
                            .inputPath));
                }
            }
            Preconditions.checkArgument(outputFile != null, "outputFile must not be null.");
            if (Files.exists(outputFile)) {
                throw new FileAlreadyExistsException(format("output file {0} already exists", outputFile));
            }

            return new Harvester(inputPath, inputType, filePattern, outputFile, outputFormat, otherArguments);
        }
    }
}