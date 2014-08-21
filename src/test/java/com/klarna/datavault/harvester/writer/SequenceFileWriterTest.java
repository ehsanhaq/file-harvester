package com.klarna.datavault.harvester.writer;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.klarna.datavault.harvester.writer.types.ConverterFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.io.compress.BZip2Codec;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SequenceFileWriterTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Path outputPath;
    private Path outputPathCompressed;

    @Before
    public void setup() {
        outputPath = Paths.get(temporaryFolder.getRoot().getAbsolutePath() + "/output.seq");
        outputPathCompressed = Paths.get(temporaryFolder.getRoot().getAbsolutePath() + "/output_compressed.seq");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullInBuilderConstructor() throws Exception {
        new SequenceFileWriter.Builder(null).build();
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void fileAlreadyExist() throws Exception {
        File alreadyExistingFile = temporaryFolder.newFile();
        new SequenceFileWriter.Builder(alreadyExistingFile.toPath())
                .build();
    }

    @Test
    public void withNoInputFile() throws Exception {
        SequenceFileWriter sequenceFileWriter = new SequenceFileWriter.Builder(outputPath).build();
        sequenceFileWriter.close();

        Map<Writable, Writable> actual = readSequenceFile(outputPath);
        Assert.assertEquals(new HashMap<Writable, Writable>(), actual);
    }

    @Test
    public void withSingleInputFile() throws Exception {
        File inputFile = temporaryFolder.newFile();
        String data = "This is a test message";
        Files.write(data.getBytes(), inputFile);

        Map<BytesWritable, BytesWritable> expected = Maps.newHashMap();
        expected.put(new BytesWritable(inputFile.getAbsoluteFile().toString().getBytes()),
                new BytesWritable(data.getBytes()));
        SequenceFileWriter sequenceFileWriter = new SequenceFileWriter.Builder(outputPath).build();
        sequenceFileWriter.write(Paths.get(inputFile.getAbsolutePath()));
        sequenceFileWriter.close();

        Map<Writable, Writable> actual = readSequenceFile(outputPath);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void withMultipleFiles() throws Exception {
        final int N = 10;
        File inputFile[] = new File[N];
        String data = "This is a test message";

        Map<Writable, Writable> expected = Maps.newHashMap();

        SequenceFileWriter sequenceFileWriter = new SequenceFileWriter.Builder(outputPath).build();

        for (int i=0; i<N; i++) {
            inputFile[i] = temporaryFolder.newFile();
            Files.write(data.getBytes(), inputFile[i]);
            expected.put(new BytesWritable(inputFile[i].getAbsoluteFile().toString().getBytes()),
                new BytesWritable(data.getBytes()));
            sequenceFileWriter.write(Paths.get(inputFile[i].getAbsolutePath()));
        }
        sequenceFileWriter.close();

        Map<Writable, Writable> actual = readSequenceFile(outputPath);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void useTextFormat() throws Exception {
        final int N = 10;
        File inputFile[] = new File[N];
        String data = "This is a test message";

        Map<Writable, Writable> expected = Maps.newHashMap();

        SequenceFileWriter sequenceFileWriter = new SequenceFileWriter.Builder(outputPath)
                .setKeyClass(ConverterFactory.Type.TEXT).setValueClass(ConverterFactory.Type.TEXT).build();

        for (int i=0; i<N; i++) {
            inputFile[i] = temporaryFolder.newFile();
            Files.write(data.getBytes(), inputFile[i]);
            expected.put(new Text(inputFile[i].getAbsolutePath().getBytes()),
                    new Text(data.getBytes()));
            sequenceFileWriter.write(Paths.get(inputFile[i].getAbsolutePath()));
        }
        sequenceFileWriter.close();

        Map<Writable, Writable> actual = readSequenceFile(outputPath);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void useNullKeyFormat() throws Exception {
        final int N = 10;
        File inputFile[] = new File[N];
        String data = "This is a test message";

        Map<Writable, Writable> expected = Maps.newHashMap();

        SequenceFileWriter sequenceFileWriter = new SequenceFileWriter.Builder(outputPath)
                .setKeyClass(ConverterFactory.Type.NULL).setValueClass(ConverterFactory.Type.TEXT).build();

        for (int i=0; i<N; i++) {
            inputFile[i] = temporaryFolder.newFile();
            Files.write(data.getBytes(), inputFile[i]);
            expected.put(NullWritable.get(),
                    new Text(data.getBytes()));
            sequenceFileWriter.write(Paths.get(inputFile[i].getAbsolutePath()));
        }
        sequenceFileWriter.close();

        Map<Writable, Writable> actual = readSequenceFile(outputPath);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void useDefaultCompression() throws Exception {
        final int N = 100;
        File inputFile[] = new File[N];
        String data = "This is a test message which is contains repeated repeated repeated repeated repeated " +
                "words for better compression.";

        Map<Writable, Writable> expected = Maps.newHashMap();

        SequenceFileWriter sequenceFileWriterPlain = new SequenceFileWriter.Builder(outputPath)
                .setCompressionMode(SequenceFile.CompressionType.NONE)
                .setKeyClass(ConverterFactory.Type.NULL).setValueClass(ConverterFactory.Type.TEXT).build();
        SequenceFileWriter sequenceFileWriterCompressed =
                new SequenceFileWriter.Builder(outputPathCompressed)
                .setCompressionMode(SequenceFile.CompressionType.BLOCK)
                .setKeyClass(ConverterFactory.Type.NULL).setValueClass(ConverterFactory.Type.TEXT).build();

        for (int i=0; i<N; i++) {
            inputFile[i] = temporaryFolder.newFile();
            Files.write(data.getBytes(), inputFile[i]);
            expected.put(NullWritable.get(),
                    new Text(data.getBytes()));
            sequenceFileWriterPlain.write(Paths.get(inputFile[i].getAbsolutePath()));
            sequenceFileWriterCompressed.write(Paths.get(inputFile[i].getAbsolutePath()));
        }
        sequenceFileWriterPlain.close();
        sequenceFileWriterCompressed.close();

        Assert.assertTrue(outputPathCompressed.toFile().length() < outputPath.toFile().length());
        Map<Writable, Writable> actualPlain = readSequenceFile(outputPath);
        Map<Writable, Writable> actualCompressed = readSequenceFile(outputPathCompressed);

        Assert.assertEquals(expected, actualPlain);
        Assert.assertEquals(expected, actualCompressed);
    }

    @Test
    public void useExplicitCompression() throws Exception {
        final int N = 100;
        File inputFile[] = new File[N];
        String data = "This is a test message which is contains repeated repeated repeated repeated repeated " +
                "words for better compression.";

        Map<Writable, Writable> expected = Maps.newHashMap();

        SequenceFileWriter sequenceFileWriterPlain = new SequenceFileWriter.Builder(outputPath)
                .setCompressionMode(SequenceFile.CompressionType.NONE)
                .setKeyClass(ConverterFactory.Type.NULL).setValueClass(ConverterFactory.Type.TEXT).build();
        SequenceFileWriter sequenceFileWriterCompressed =
                new SequenceFileWriter.Builder(outputPathCompressed)
                        .setCompressionCodec(BZip2Codec.class.getName())
                        .setCompressionMode(SequenceFile.CompressionType.BLOCK)
                        .setKeyClass(ConverterFactory.Type.NULL).setValueClass(ConverterFactory.Type.TEXT).build();

        for (int i=0; i<N; i++) {
            inputFile[i] = temporaryFolder.newFile();
            Files.write(data.getBytes(), inputFile[i]);
            expected.put(NullWritable.get(),
                    new Text(data.getBytes()));
            sequenceFileWriterPlain.write(Paths.get(inputFile[i].getAbsolutePath()));
            sequenceFileWriterCompressed.write(Paths.get(inputFile[i].getAbsolutePath()));
        }
        sequenceFileWriterPlain.close();
        sequenceFileWriterCompressed.close();

        Assert.assertTrue(outputPathCompressed.toFile().length() < outputPath.toFile().length());
        Map<Writable, Writable> actualPlain = readSequenceFile(outputPath);
        Map<Writable, Writable> actualCompressed = readSequenceFile(outputPathCompressed);

        Assert.assertEquals(expected, actualPlain);
        Assert.assertEquals(expected, actualCompressed);
    }

    private Map<Writable, Writable> readSequenceFile(Path filePath) throws IOException,
            IllegalAccessException, InstantiationException {
        File sequenceFile = filePath.toFile();
        SequenceFile.Reader reader = new SequenceFile.Reader(new Configuration(),
                SequenceFile.Reader.file(new org.apache.hadoop.fs.Path(sequenceFile.getAbsolutePath())));
        Map<Writable, Writable> output = Maps.newHashMap();
        while (true) {
            Writable key;
            if (reader.getKeyClass() == NullWritable.class) {
                key = NullWritable.get();
            } else {
                key = (Writable) reader.getKeyClass().newInstance();
            }
            Writable value = (Writable) reader.getValueClass().newInstance();
            if (!reader.next(key, value)) {
                break;
            }
            output.put(key, value);
        }
        return output;
    }
}