package com.meh.harvester.writer.types;

/**
 * Factory for creating Converter instances.
 */
public class ConverterFactory {
    public enum Type {
        BINARY,
        TEXT,
        NULL,
    };

    public static Converter create(Type type) {
        if (type == Type.TEXT) {
            return new TextConverter();
        } else if (type == Type.BINARY) {
            return new BytesWritableConverter();
        } else if (type == Type.NULL) {
            return new NullWritableConverter();
        } else {
            throw new IllegalArgumentException("Invalid type " + type.toString());
        }
    }
}
