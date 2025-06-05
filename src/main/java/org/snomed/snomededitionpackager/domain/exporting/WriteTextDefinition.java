package org.snomed.snomededitionpackager.domain.exporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.snomededitionpackager.domain.datastore.DataStore;
import org.snomed.snomededitionpackager.domain.rf2.Description;
import org.snomed.snomededitionpackager.domain.rf2.RF2;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
public class WriteTextDefinition implements ExportWriter {
	private static final Logger LOGGER = LoggerFactory.getLogger(WriteTextDefinition.class);

	private final DataStore dataStore;

	public WriteTextDefinition(DataStore dataStore) {
		this.dataStore = dataStore;
	}

	@Override
	public boolean write(ExportConfiguration exportConfiguration) {
		if (exportConfiguration == null) {
			return false;
		}

		// Prepare text file(s)
		String rf2Package = exportConfiguration.getRf2Package();
		String shortName = exportConfiguration.getShortName();
		String effectiveTime = exportConfiguration.getEffectiveTime();
		boolean full = exportConfiguration.isFull();
		boolean sort = exportConfiguration.isSort();

		for (String languageCode : dataStore.readTextDefinitionLanguageCodes()) {
			Path fullPath = Paths.get(FileNameService.getTextDefinition(rf2Package, RF2.FULL, languageCode, shortName, effectiveTime));
			Path snapshotPath = Paths.get(FileNameService.getTextDefinition(rf2Package, RF2.SNAPSHOT, languageCode, shortName, effectiveTime));

			// Create text file(s)
			boolean createdTextFiles = createTextFiles(full, fullPath, snapshotPath);
			if (!createdTextFiles) {
				return false;
			}

			// Write to text file(s)
			if (!writeTextDefinitions(full, sort, fullPath, snapshotPath, languageCode)) {
				return false;
			}
		}

		return true;
	}

	private boolean writeTextDefinitions(boolean full, boolean sort, Path fullPath, Path snapshotPath, String languageCode) {
		try (BufferedWriter fullWriter = initBufferedWriter(full, fullPath); BufferedWriter snapshotWriter = initBufferedWriter(snapshotPath)) {
			writeHeader(full, RF2.HEADER_TEXT_DEFINITION, fullPath, snapshotPath);

			for (Map.Entry<String, Set<Description>> entrySet : dataStore.readTextDefinitions(sort).entrySet()) {
				Set<Description> value = entrySet.getValue();

				// Write latest to Snapshot
				Description latest = value.iterator().next();
				boolean matchLanguageCode = Objects.equals(latest.getLanguageCode(), languageCode);
				if (matchLanguageCode) {
					snapshotWriter.write(latest.toRF2() + RF2.LINE_ENDING);
				}

				if (full) {
					// Sort effectiveTime in ascending order
					List<Description> textDefinitions = sortByEffectiveTime(sort, value);

					// Write everything to Full
					for (Description textDefinition : textDefinitions) {
						matchLanguageCode = Objects.equals(textDefinition.getLanguageCode(), languageCode);
						if (matchLanguageCode) {
							fullWriter.write(textDefinition.toRF2() + RF2.LINE_ENDING);
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Failed to write to Text Definition file.", e);
			return false;
		}

		return true;
	}

	private List<Description> sortByEffectiveTime(boolean sort, Set<Description> value) {
		List<Description> descriptions = new ArrayList<>(value);
		if (sort) {
			Collections.reverse(descriptions);
		}

		return descriptions;
	}
}