package com.klarna.datavault.harvester.writer.types;

import org.apache.hadoop.io.BytesWritable;
import org.junit.Assert;
import org.junit.Test;

public class BytesWritableConverterTest {
    @Test
    public void withAllBytes() {
        byte[] bytes = new byte[256];
        for (int i=0; i<256; i++) {
            bytes[i] = (byte)i;
        }
        BytesWritable bytesWritable = new BytesWritableConverter().convertBytesToObject(bytes);
        Assert.assertArrayEquals(bytes, bytesWritable.getBytes());
    }

    @Test
    public void byteWritableConverterClass() {
        Assert.assertEquals(BytesWritable.class, new BytesWritableConverter().getClassName());
    }
}
