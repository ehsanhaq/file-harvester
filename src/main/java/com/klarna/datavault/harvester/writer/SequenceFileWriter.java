package com.klarna.datavault.harvester.writer;

import com.google.common.base.Preconditions;
import com.klarna.datavault.harvester.writer.types.Converter;
import com.klarna.datavault.harvester.writer.types.ConverterFactory;
import org.apache.commons.cli.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.DeflateCodec;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.klarna.datavault.harvester.writer.types.ConverterFactory.Type;
import static org.apache.hadoop.io.SequenceFile.*;

/**
 * Writer for creating a sequence file from the contents of other files.
 */
public class SequenceFileWriter extends OutputFileWriterBase {

    private Converter keyConverter;
    private Converter valueConverter;
    private CompressionType compressionType;
    private Writer writer;

    private SequenceFileWriter(Path outputPath, Type keyType, Type valueType,
                               CompressionType compressionType) throws IOException {
        super(outputPath);
        this.compressionType = compressionType;
        this.keyConverter = ConverterFactory.create(keyType);
        this.valueConverter = ConverterFactory.create(valueType);
        writer = createSequenceFileWriter(outputFile);
    }

    /** Helper method to create a new sequence file **/
    private Writer createSequenceFileWriter(File outputPath) throws IOException {
        Configuration conf = new Configuration();
        org.apache.hadoop.fs.Path path = new org.apache.hadoop.fs.Path(outputPath.getAbsolutePath());

        CompressionCodec codec = new DeflateCodec();
        Writer.Option optPath = Writer.file(path);
        Writer.Option optKey = Writer.keyClass(keyConverter.getClassName());
        Writer.Option optValue = Writer.valueClass(valueConverter.getClassName());
        Writer.Option optCom = Writer.compression(compressionType, codec);

        return createWriter(conf, optPath, optKey, optValue, optCom);
    }

    /**
     * Add the contents of the file {@code inputFile} in the sequence file.
     * @param inputFile File to be processed.
     * @throws IOException
     */
    @Override
    public void write(Path inputFile) throws IOException {
        byte[] bytes = Files.readAllBytes(inputFile);
        writer.append(keyConverter.convertBytesToObject(inputFile.toString().getBytes()),
                valueConverter.convertBytesToObject(bytes));
    }

    /**
     * Close the writer instance. After this method invocation no further {@link #write(java.nio.file.Path)} should
     * be called.
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        writer.close();
    }

    /**
     * Builder class for creating {@code SequenceFileWriter} instance.
     */
    public static class Builder {
        private final Path outputFile;
        private CompressionType compressionType = CompressionType.NONE;
        private Type keyType = Type.BINARY;
        private Type valueType = Type.BINARY;
        private String[] otherArguments;

        /**
         * Constructor takes the output sequence file name,
         * @param outputPath Output path of sequence file.
         */
        public Builder(Path outputPath) {
            Preconditions.checkArgument(outputPath != null, "outputPath can not be null");
            this.outputFile = outputPath;
        }

        /**
         * Sets the compression type
         * @param compressionType enumerated value of compression type to use for the sequence file.
         * @return this instance of the class.
         */
        public Builder setCompressionMode(CompressionType compressionType) {
            this.compressionType = compressionType;
            return this;
        }

        /**
         * Sets the key class type
         * @param keyType enumerated value for key class to be used for the sequence file.
         * @return this instance of the class.
         */
        public Builder setKeyClass(Type keyType) {
            this.keyType = keyType;
            return this;
        }

        /**
         * Sets the value class type
         * @param valueType enumerated value for value class to be used for the sequence file.
         * @return this instance of the class.
         */
        public Builder setValueClass(Type valueType) {
            this.valueType = valueType;
            return this;
        }

        /**
         * Sets other arguments for the {@code SequenceFileWriter}
         * @param otherArguments additional arguments for {@code SequenceFileWriter}
         * @return this instance of the class.
         */
        public Builder setOtherArguments(String [] otherArguments) {
            this.otherArguments = otherArguments;
            return this;
        }

        /**
         * Builds the {@code SequenceFileWriter} instance
         * @return an instance of {@code SequenceFileWriter}
         * @throws IOException
         */
        public SequenceFileWriter build() throws IOException {
            if (otherArguments != null) {
                parseOtherArguments(otherArguments);
            }
            return new SequenceFileWriter(outputFile, this.keyType, this.valueType, compressionType);
        }

        /** Helper class to parse {@link SequenceFileWriter} specific arguments. **/
        private void parseOtherArguments(String [] otherArguments) {
            Option KEY_CLASS = OptionBuilder.hasArgs(1).withArgName("key-class")
                    .withDescription("Key class of the sequence file BINARY|TEXT|NULL default is BINARY.").create("kc");
            Option VALUE_CLASS = OptionBuilder.hasArgs(1).withArgName("value-class")
                    .withDescription("Value class of the sequence file BINARY|TEXT|NULL default is BINARY.")
                    .create("vc");
            Option COMPRESSION_MODE = OptionBuilder.hasArgs(1).withArgName("compression-mode")
                    .withDescription("Compression mode NONE|RECORD|BLOCK default is NONE").create("cm");
            Option COMPRESSION_CODEC = OptionBuilder.hasArgs(1).withArgName("compression-codec")
                    .withDescription("Compression codec class.").create("cd");
            Options options = new Options()
                    .addOption(KEY_CLASS)
                    .addOption(VALUE_CLASS)
                    .addOption(COMPRESSION_MODE)
                    .addOption(COMPRESSION_CODEC);
            CommandLine commandLine = null;
            try {
                commandLine = new BasicParser().parse(options, otherArguments);
            } catch (ParseException e) {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("cmd", options, true);
            }
            this.setKeyClass(Type.valueOf(commandLine.getOptionValue(KEY_CLASS.getOpt(), Type.BINARY.name())));
            this.setValueClass(Type.valueOf(commandLine.getOptionValue(VALUE_CLASS.getOpt(), Type.BINARY.name())));
            this.setCompressionMode(CompressionType.valueOf(commandLine.getOptionValue(COMPRESSION_MODE.getOpt(),
                    CompressionType.NONE.name())));
        }
    }
}
