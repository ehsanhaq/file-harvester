package com.klarna.datavault.harvester.writer.types;

import org.apache.hadoop.io.NullWritable;

/**
 * Convert byte array into Hadoop NullWritable format.
 */
public class NullWritableConverter implements Converter<NullWritable> {
    @Override
    public Class<NullWritable> getClassName() {
        return NullWritable.class;
    }

    @Override
    public NullWritable convertBytesToObject(byte[] in) {
        return NullWritable.get();
    }
}
