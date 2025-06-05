package org.snomed.snomededitionpackager.domain.exporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ExportService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExportService.class);

	private final List<ExportWriter> exportWriters = new ArrayList<>();

	public ExportService(WriteRoot writeRoot, WriteConcept writeConcept, WriteDescription writeDescription, WriteTextDefinition writeTextDefinition, WriteIdentifier writeIdentifier, WriteRelationship writeRelationship, WriteStatedRelationship writeStatedRelationship, WriteConcreteRelationship writeConcreteRelationship, WriteAxiom writeAxiom, WriteReferenceSetMember writeReferenceSetMember, WriteReleasePackageInformation writeReleasePackageInformation, WriteEmptyFiles writeEmptyFiles, WriteZip writeZip, WriteReadme writeReadme) {
		this.exportWriters.add(writeRoot);
		this.exportWriters.add(writeConcept);
		this.exportWriters.add(writeDescription);
		this.exportWriters.add(writeTextDefinition);
		this.exportWriters.add(writeIdentifier);
		this.exportWriters.add(writeRelationship);
		this.exportWriters.add(writeStatedRelationship);
		this.exportWriters.add(writeConcreteRelationship);
		this.exportWriters.add(writeAxiom);
		this.exportWriters.add(writeReferenceSetMember);
		this.exportWriters.add(writeReleasePackageInformation);
		this.exportWriters.add(writeEmptyFiles);
		this.exportWriters.add(writeReadme); // always add second last
		this.exportWriters.add(writeZip); // always add last
	}

	public boolean export(ExportConfiguration exportConfiguration) {
		long start;
		boolean success;
		long end;

		for (ExportWriter exportWriter : exportWriters) {
			start = System.currentTimeMillis();
			String simpleName = exportWriter.getClass().getSimpleName();
			success = exportWriter.write(exportConfiguration);
			end = System.currentTimeMillis();

			if (success) {
				LOGGER.debug("{} complete in {} seconds.", simpleName, total(start, end));
			} else {
				LOGGER.debug("{} failed after {} seconds.", simpleName, total(start, end));
				return false;
			}
		}

		return true;
	}

	private long total(long start, long end) {
		return (end - start) / 1_000;
	}
}