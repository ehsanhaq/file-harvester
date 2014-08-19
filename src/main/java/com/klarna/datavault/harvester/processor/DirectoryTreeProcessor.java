package com.klarna.datavault.harvester.processor;

import com.google.common.base.Preconditions;
import com.klarna.datavault.harvester.visitor.HarvestVisitor;
import com.klarna.datavault.harvester.writer.OutputFileWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

import static java.text.MessageFormat.format;

/**
 * Processor for processing files in a Directory and its sub-directory using the {@code Files.walkFileTree()}
 */
public class DirectoryTreeProcessor extends BaseProcessor {

    private Path rootPath;

    public DirectoryTreeProcessor(PathMatcher pathMatcher, Path rootPath) throws FileNotFoundException {
        super(pathMatcher);
        Preconditions.checkArgument(rootPath != null, "rootPath can not be null.");
        this.rootPath = rootPath;
        if (!Files.exists(rootPath) || !Files.isDirectory(rootPath)) {
            throw new FileNotFoundException(format("path {0} does not exists or is not a directory", rootPath));
        }
    }

    /**
     * Processes files while visiting the file tree and output the result of processing via {@code outputFileWriter}.
     * @param outputFileWriter Output file writer to write processing results.
     * @throws IOException
     */
    @Override
    public void process(OutputFileWriter outputFileWriter) throws IOException {
        HarvestVisitor harvestVisitor = new HarvestVisitor(pathMatcher, outputFileWriter);
        Files.walkFileTree(rootPath, harvestVisitor);
    }
}
