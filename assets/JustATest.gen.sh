#!/bin/bash
# Main-Class: org.apache.avro.tool.Main
jarpath=${JCLOUD_HOME}/tool/avro/avro-tools-1.8.1.jar
java -jar $jarpath idl JustATest.avdl JustATest.avpr
java -jar $jarpath compile protocol JustATest.avpr avpr

cp -r avpr/com/neohope ../src/main/java/com/
