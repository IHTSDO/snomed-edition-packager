package org.snomed.snomededitionpackager.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.snomededitionpackager.rf2.RF2Constants;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ReadmeGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadmeGenerator.class);

    private static final String INDENTATION = "    ";

    public static void generate(String readmeFilename, String releaseInformationFilename, String readmeHeader, String readmeEndDate, ZipFile zipFile, OutputStream readmeOutputStream) throws IOException {
        LOGGER.info("Generating Readme file...");
        readmeEndDate = (readmeEndDate != null) ? readmeEndDate : "";
        if (readmeHeader == null) {
            throw new IllegalArgumentException("Readme header has not been supplied.  Unable to continue");
        }
        readmeHeader = readmeHeader.replace("{readmeEndDate}", readmeEndDate);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(readmeOutputStream, RF2Constants.UTF_8))) {
            writer.write(readmeHeader);
            writer.write(RF2Constants.LINE_ENDING);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            Map<String, String> map = new TreeMap<>();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                map.put(entry.getName(), entry.getName());
            }
            int index = 0;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String[] parts = key.split("/");
                if (index == 0) {
                    writeLine(parts[0], 1, writer);
                    index++;
                }
                writeLine(parts[parts.length - 1], parts.length, writer);
            }
            writeLine(readmeFilename, 2, writer);
            writeLine(releaseInformationFilename, 2, writer);
        }
    }

    private static void writeLine(String line, int depth, BufferedWriter writer) throws IOException {
        for (int a = 0; a < depth; a++) {
            writer.write(INDENTATION);
        }
        if (line != null) {
            writer.write(line);
        }
        writer.write(RF2Constants.LINE_ENDING);
    }

}
