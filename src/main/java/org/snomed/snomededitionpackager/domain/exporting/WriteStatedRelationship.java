package org.snomed.snomededitionpackager.domain.exporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.snomededitionpackager.domain.datastore.DataStore;
import org.snomed.snomededitionpackager.domain.rf2.RF2;
import org.snomed.snomededitionpackager.domain.rf2.Relationship;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
public class WriteStatedRelationship implements ExportWriter {
	private static final Logger LOGGER = LoggerFactory.getLogger(WriteStatedRelationship.class);

	private final DataStore dataStore;

	public WriteStatedRelationship(DataStore dataStore) {
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
		Path fullPath = Paths.get(FileNameService.getStatedRelationship(rf2Package, RF2.FULL, shortName, effectiveTime));
		Path snapshotPath = Paths.get(FileNameService.getStatedRelationship(rf2Package, RF2.SNAPSHOT, shortName, effectiveTime));
		boolean full = exportConfiguration.isFull();
		boolean sort = exportConfiguration.isSort();

		// Create text file(s)
		boolean createdTextFiles = createTextFiles(full, fullPath, snapshotPath);
		if (!createdTextFiles) {
			return false;
		}

		// Write to text file(s)
		try (BufferedWriter fullWriter = initBufferedWriter(full, fullPath); BufferedWriter snapshotWriter = initBufferedWriter(snapshotPath)) {
			writeHeader(full, RF2.HEADER_STATED_RELATIONSHIP, fullPath, snapshotPath);

			for (Map.Entry<String, Set<Relationship>> entrySet : dataStore.readStatedRelationships(sort).entrySet()) {
				Set<Relationship> value = entrySet.getValue();

				// Write latest to Snapshot
				snapshotWriter.write(value.iterator().next().toRF2() + RF2.LINE_ENDING);

				if (full) {
					// Sort effectiveTime in ascending order
					List<Relationship> statedRelationships = new ArrayList<>(value);
					if (sort) {
						Collections.reverse(statedRelationships);
					}

					// Write everything to Full
					for (Relationship relationship : statedRelationships) {
						fullWriter.write(relationship.toRF2() + RF2.LINE_ENDING);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Failed to write to Stated Relationship file.", e);
			return false;
		}

		return true;
	}
}