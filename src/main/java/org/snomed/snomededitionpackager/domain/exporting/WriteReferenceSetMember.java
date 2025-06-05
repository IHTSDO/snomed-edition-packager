package org.snomed.snomededitionpackager.domain.exporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.snomededitionpackager.domain.datastore.DataStore;
import org.snomed.snomededitionpackager.domain.rf2.RF2;
import org.snomed.snomededitionpackager.domain.rf2.ReferenceSetMember;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class WriteReferenceSetMember implements ExportWriter {
	private static final Logger LOGGER = LoggerFactory.getLogger(WriteReferenceSetMember.class);

	private final DataStore dataStore;
	private final FileNameService fileNameService;

	public WriteReferenceSetMember(DataStore dataStore, FileNameService fileNameService) {
		this.dataStore = dataStore;
		this.fileNameService = fileNameService;
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
		boolean sort = exportConfiguration.isSort();
		Map<String, List<Set<ReferenceSetMember>>> referenceSetMembersByRefsetId = groupReferenceSetMembersByFileName(sort);

		for (Map.Entry<String, List<Set<ReferenceSetMember>>> entrySet : referenceSetMembersByRefsetId.entrySet()) {
			String refsetId = entrySet.getKey();
			List<Set<ReferenceSetMember>> values = entrySet.getValue();

			// Prepare text file(s)
			Path fullPath = Paths.get(fileNameService.getReferenceSet(rf2Package, RF2.FULL, shortName, effectiveTime, refsetId));
			Path snapshotPath = Paths.get(fileNameService.getReferenceSet(rf2Package, RF2.SNAPSHOT, shortName, effectiveTime, refsetId));
			boolean full = exportConfiguration.isFull();

			// Create text file(s)
			boolean createdTextFiles = createTextFiles(full, fullPath, snapshotPath);
			if (!createdTextFiles) {
				return false;
			}

			// Write to text file(s)
			try (BufferedWriter fullWriter = initBufferedWriter(full, fullPath); BufferedWriter snapshotWriter = initBufferedWriter(snapshotPath)) {
				writeHeader(full, dataStore.getHeader(refsetId), fullPath, snapshotPath);

				for (Set<ReferenceSetMember> value : values) {
					// Write latest to Snapshot
					snapshotWriter.write(value.iterator().next().toRF2() + RF2.LINE_ENDING);

					if (full) {
						// Sort effectiveTime in ascending order
						List<ReferenceSetMember> referenceSetMembers = sortByEffectiveTime(sort, value);

						// Write all to Full
						for (ReferenceSetMember referenceSetMember : referenceSetMembers) {
							fullWriter.write(referenceSetMember.toRF2() + RF2.LINE_ENDING);
						}
					}
				}
			} catch (Exception e) {
				LOGGER.error("Failed to write to ReferenceSetMember file.", e);
				return false;
			}
		}

		return true;
	}

	private Map<String, List<Set<ReferenceSetMember>>> groupReferenceSetMembersByFileName(boolean sort) {
		// Group members by their refset id
		Map<String, List<Set<ReferenceSetMember>>> referenceSetMembersByRefsetId = new HashMap<>();
		for (Map.Entry<String, Set<ReferenceSetMember>> entrySet : dataStore.readReferenceSetMembers().entrySet()) {
			Set<ReferenceSetMember> referenceSetMembers = entrySet.getValue();
			String refsetId = referenceSetMembers.iterator().next().getRefsetId();

			List<Set<ReferenceSetMember>> history = referenceSetMembersByRefsetId.get(refsetId);
			if (history == null) {
				history = new ArrayList<>();
			}

			history.add(referenceSetMembers);
			referenceSetMembersByRefsetId.put(refsetId, history);
		}

		// Group refset ids by their file name (i.e. multiple refsets can be in same file)
		Map<String, List<String>> refsetIdsByFileName = new HashMap<>();
		for (Map.Entry<String, List<Set<ReferenceSetMember>>> entrySet : referenceSetMembersByRefsetId.entrySet()) {
			String refsetId = entrySet.getKey();
			String fileName = dataStore.getFileName(refsetId);
			List<String> refsetIds = refsetIdsByFileName.get(fileName);
			if (refsetIds == null) {
				refsetIds = new ArrayList<>();
			}
			refsetIds.add(refsetId);
			refsetIdsByFileName.put(fileName, refsetIds);
		}

		// Merge reference set members that share same file name
		Map<String, List<Set<ReferenceSetMember>>> referenceSetMembersByFileName = new HashMap<>();
		for (Map.Entry<String, List<String>> entrySet : refsetIdsByFileName.entrySet()) {
			List<String> referenceSetTypes = entrySet.getValue();
			String lastReferenceSetType = null;
			List<Set<ReferenceSetMember>> sets = new ArrayList<>();
			for (String referenceSetType : referenceSetTypes) {
				lastReferenceSetType = referenceSetType;
				sets.addAll(referenceSetMembersByRefsetId.get(referenceSetType));
			}

			referenceSetMembersByFileName.put(lastReferenceSetType, sets);
		}


		return sort ? sortByUUID(referenceSetMembersByFileName) : referenceSetMembersByRefsetId;
	}

	private Map<String, List<Set<ReferenceSetMember>>> sortByUUID(Map<String, List<Set<ReferenceSetMember>>> input) {
		return input.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().sorted(Comparator.comparing(set -> UUID.fromString(set.iterator().next().getId()))).toList()));
	}

	private List<ReferenceSetMember> sortByEffectiveTime(boolean sort, Set<ReferenceSetMember> value) {
		List<ReferenceSetMember> referenceSetMembers = new ArrayList<>(value);
		if (sort) {
			Collections.reverse(referenceSetMembers);
		}

		return referenceSetMembers;
	}
}