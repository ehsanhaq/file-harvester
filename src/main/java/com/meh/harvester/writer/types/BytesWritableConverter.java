package com.meh.harvester.writer.types;

import org.apache.hadoop.io.BytesWritable;

/**
 * Convert byte array into Hadoop BytesWritable format.
 */
public class BytesWritableConverter implements Converter<BytesWritable> {
    @Override
    public Class<BytesWritable> getClassName() {
        return BytesWritable.class;
    }

    @Override
    public BytesWritable convertBytesToObject(byte[] in) {
        return new BytesWritable(in);
    }
}
