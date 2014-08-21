file-harvester
==============
A command line utility to harvest small files into sequence file format.

Usage
-----
java -jar file-harvester-x.x-jar-with-dependencies.jar [harvester-options] -- [output-format-options]

**harvester-options:**

    -ip <input-path>      Input path to harvest, must be a directory or file. 
    -it <input-type>      Input path type DIRECTORY|FILE default is DIRECTORY. 
    -me <match>           Glob expression to match file for harvesting, default is *.* 
    -f <output-file>      Mandatory outfile file name. 
    -of <output-format>   Output file format, SEQUENCE|ZIP|TAR, default is SEQUENCE. 
                          Other formats are not supported! 

**output-format-options:** specific to the output format 

Output Format Specific Options
------------------------------
**Sequence File**

    -cm <compression-mode>    Compression mode NONE|RECORD|BLOCK default is NONE
    -cd <compression-codec>   Compression codec class, applicable only if compression-mode is not NONE. 
                              Default is org.apache.hadoop.io.compress.DeflateCodec.
    -kc <key-class>           Key class of the sequence file BINARY|TEXT|NULL default is BINARY.
    -vc <value-class>         Value class of the sequence file BINARY|TEXT|NULL default is BINARY
                           
Examples
--------
```
    java -jar file-harvester-x.x-jar-with-dependencies.jar -ip /directory -f harvested.seq
    java -jar file-harvester-x.x-jar-with-dependencies.jar -ip /directory -f harvested.seq.deflate -- -cm BLOCK
    java -jar file-harvester-x.x-jar-with-dependencies.jar -ip /directory -f harvested.seq.bzip2 -- -cm BLOCK -cd org.apache.hadoop.io.compress.BZip2Codec
    java -jar file-harvester-x.x-jar-with-dependencies.jar -ip /directory -f harvested.seq -- -vc TEXT    
```