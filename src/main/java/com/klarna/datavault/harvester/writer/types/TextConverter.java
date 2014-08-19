package com.klarna.datavault.harvester.writer.types;

import org.apache.hadoop.io.Text;

import java.nio.charset.Charset;

/**
 * Convert byte array into Hadoop Text format.
 */
public class TextConverter implements Converter<Text> {
    @Override
    public Class<Text> getClassName() {
        return Text.class;
    }

    @Override
    public Text convertBytesToObject(byte[] in) {
        return new Text(new String(in, Charset.forName("utf-8")));
    }
}
