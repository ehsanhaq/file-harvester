package com.meh.harvester.visitor;

import com.google.common.base.Preconditions;
import com.meh.harvester.writer.OutputFileWriter;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * File visitor used by the {@link Files#walkFileTree(java.nio.file.Path, java.nio.file.FileVisitor)} to visit
 * each file in a file tree and output files matching a certain pattern via an instance of
 * {@link com.meh.harvester.writer.OutputFileWriter}.
 */
public class HarvestVisitor extends SimpleFileVisitor<Path> {

    private PathMatcher pathMatcher;
    private OutputFileWriter fileWriter;

    public HarvestVisitor(PathMatcher pathMatcher, OutputFileWriter fileWriter) {
        Preconditions.checkArgument(pathMatcher != null, "pathMatcher can not be null");
        Preconditions.checkArgument(fileWriter != null, "fileWriter can not be null");
        this.pathMatcher = pathMatcher;
        this.fileWriter = fileWriter;
    }

    /**
     * Method invoked for a file in the file tree. If the file matches the @see #pathMatcher pattern then the file is
     * also processed by the @see #fileWriter.
     * @param file File for which the method is invoked.
     * @param fileAttributes File attributes of the file for which the method is invoked.
     * @return {@link java.nio.file.FileVisitResult#CONTINUE}
     * @throws IOException
     */
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes fileAttributes) throws IOException {
        if (fileAttributes.isRegularFile()) {
            if (pathMatcher.matches(file.getFileName())) {
                fileWriter.write(file);
            }
        }
        return FileVisitResult.CONTINUE;
    }
}
