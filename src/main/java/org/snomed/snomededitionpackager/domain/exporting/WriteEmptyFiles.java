package org.snomed.snomededitionpackager.domain.exporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.snomededitionpackager.domain.datastore.DataStore;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Component
public class WriteEmptyFiles implements ExportWriter {
	private static final Logger LOGGER = LoggerFactory.getLogger(WriteEmptyFiles.class);

	private final DataStore dataStore;

	public WriteEmptyFiles(DataStore dataStore) {
		this.dataStore = dataStore;
	}

	@Override
	public boolean write(ExportConfiguration exportConfiguration) {
		if (exportConfiguration == null) {
			return false;
		}

		// Prepare text file(s)
		String rf2Package = exportConfiguration.getRf2Package();

		for (Map.Entry<String, String> entrySet : dataStore.readEmptyFiles().entrySet()) {
			String fileName = String.format("%s/%s.txt", rf2Package, entrySet.getKey());
			String fileHeader = entrySet.getValue();
			Path path = Paths.get(fileName);

			// Create text file(s)
			createTextFiles(path);

			// Write to text file(s)
			try (BufferedReader reader = Files.newBufferedReader(path); BufferedWriter writer = initBufferedWriter(path)) {
				String firstLine = reader.readLine();
				if (firstLine == null) {
					writer.write(fileHeader);
				}
			} catch (Exception e) {
				LOGGER.error("Failed to write to empty file.", e);
				return false;
			}
		}

		return true;
	}
}
