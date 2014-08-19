package com.klarna.datavault.harvester.processor;

import com.google.common.base.Preconditions;

import java.nio.file.PathMatcher;

/**
 * Base class for implementing Processor.
 */
public abstract class BaseProcessor implements Processor {

    /** Common variable between implementing processors **/
    PathMatcher pathMatcher;

    /**
     * Constructor that expects, the common variable pathMatcher needed in all implementing classes.
     * @param pathMatcher
     */
    public BaseProcessor(PathMatcher pathMatcher) {
        Preconditions.checkArgument(pathMatcher != null, "pathMatcher can not be null");
        this.pathMatcher = pathMatcher;
    }
}
