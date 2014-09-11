package com.meh.harvester.writer.types;

import org.apache.hadoop.io.Text;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.Charset;

public class TextConverterTest {
    @Test
    public void byteArrayConvertedToUTF8() {
        byte[] bytes = new byte[256];
        for (int i=0; i<256; i++) {
            bytes[i] = (byte)i;
        }
        Text actual = new TextConverter().convertBytesToObject(bytes);
        Assert.assertEquals(new Text(new String(bytes, Charset.forName("utf-8"))), actual);
        Assert.assertNotEquals(new Text(bytes), actual);
    }

    @Test
    public void textConverterClass() {
        Assert.assertEquals(Text.class, new TextConverter().getClassName());
    }
}
