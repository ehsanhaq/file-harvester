package com.meh.harvester;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class HarvesterMainTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void scenarioTestWithCompression() throws Exception {
        final int N = 10;
        File inputDir = temporaryFolder.newFolder("input");
        String outputFile = temporaryFolder.getRoot().getAbsolutePath() + "/harvested.seq.deflate";
        File inputFile[] = new File[N];
        String data = "This is a test message";
        Map<Writable, Writable> expected = Maps.newHashMap();
        for (int i=0; i<N; i++) {
            inputFile[i] = new File(inputDir.getAbsolutePath(), "file." + i);
            Files.write(data.getBytes(), inputFile[i]);
            expected.put(new BytesWritable(inputFile[i].getAbsoluteFile().toString().getBytes()),
                    new BytesWritable(data.getBytes()));
        }
        new HarvesterMain().main(new String[]{"-ip", inputDir.getAbsolutePath(),
                "-f", outputFile,
                "--",
                "-cm", "BLOCK"});

        Map<Writable, Writable> actual = readSequenceFile(Paths.get(outputFile));

        Assert.assertEquals(expected, actual);
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