package com.meh.harvester.writer.types;

import org.apache.hadoop.io.NullWritable;
import org.junit.Assert;
import org.junit.Test;

public class NullWritableConverterTest {
    @Test
    public void nullWritableWithData() {
        byte bytes[] = "foo".getBytes();
        Assert.assertEquals(NullWritable.get(), new NullWritableConverter().convertBytesToObject(bytes));
    }

    @Test
    public void nullWritableConverterClass() {
        Assert.assertEquals(NullWritable.class, new NullWritableConverter().getClassName());
    }
}
