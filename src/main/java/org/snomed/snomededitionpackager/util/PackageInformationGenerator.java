package org.snomed.snomededitionpackager.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import static org.snomed.snomededitionpackager.rf2.RF2Constants.EFFECTIVE_TIME;
import static org.snomed.snomededitionpackager.rf2.RF2Constants.UTF_8;

public class PackageInformationGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PackageInformationGenerator.class);
    public static void generate(String effectiveTime, Map<String, Object> fieldMap, OutputStream releaseInformationOutputStream) throws IOException {
        LOGGER.info("Generating Release Package Information file...");
        for (String key : fieldMap.keySet()) {
            if (EFFECTIVE_TIME.equals(key)) {
                fieldMap.put(EFFECTIVE_TIME, effectiveTime);
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(releaseInformationOutputStream, UTF_8))) {
            Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().setPrettyPrinting().create();
            writer.write(gson.toJson(fieldMap));
        }
    }

}
