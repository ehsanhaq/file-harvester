package com.meh.harvester.writer.types;

/**
 * Interface for converting bytes into some class of type T.
 * @param <T> class type to convert.
 */
public interface Converter<T> {
    Class<T> getClassName();
    T convertBytesToObject(byte[] in);
}
