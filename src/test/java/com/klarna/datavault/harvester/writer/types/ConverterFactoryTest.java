package com.klarna.datavault.harvester.writer.types;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.junit.Assert;
import org.junit.Test;

public class ConverterFactoryTest {
    @Test
    public void converterFactoryPositive() {
        Assert.assertEquals(BytesWritable.class, ConverterFactory.create(ConverterFactory.Type.BINARY).getClassName());
        Assert.assertEquals(Text.class, ConverterFactory.create(ConverterFactory.Type.TEXT).getClassName());
        Assert.assertEquals(NullWritable.class, ConverterFactory.create(ConverterFactory.Type.NULL).getClassName());
    }
}
