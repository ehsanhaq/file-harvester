package com.meh.harvester.processor;

import org.apache.commons.lang.NotImplementedException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

import static com.meh.harvester.Harvester.InputType;
import static java.text.MessageFormat.format;

public class ProcessorFactory {
    private ProcessorFactory() {}

    public static Processor createProcessor(InputType inputType, PathMatcher pathMatcher, Path inputPath)
            throws IOException {
        if (inputType == InputType.FILE) {
            return new FileListProcessor(pathMatcher, inputPath);
        } else if (inputType == InputType.DIRECTORY) {
            return new DirectoryTreeProcessor(pathMatcher, inputPath);
        } else {
            throw new NotImplementedException(format("Input type {0} not implemented", inputType));
        }
    }
}
