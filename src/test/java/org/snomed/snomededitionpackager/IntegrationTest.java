package org.snomed.snomededitionpackager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.snomed.snomededitionpackager.domain.datastore.DataStore;
import org.snomed.snomededitionpackager.domain.exporting.*;
import org.snomed.snomededitionpackager.domain.rf2.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
public abstract class IntegrationTest {
	@Autowired
	protected DataStore dataStore;

	@Autowired
	protected FileNameService fileNameService;

	@Autowired
	protected ExportService exportService;

	@BeforeEach
	void beforeEach() {
		this.dataStore.clear();
	}

	protected void addConcept(Concept concept) {
		this.dataStore.createConcept(concept);
	}

	protected void addDescription(Description description) {
		this.dataStore.createDescription(description);
	}

	protected void addIdentifier(Identifier identifier) {
		this.dataStore.createIdentifier(identifier);
	}

	protected void addRelationship(Relationship relationship) {
		this.dataStore.createRelationship(relationship);
	}

	protected void addRelationship(ConcreteRelationship relationship) {
		this.dataStore.createConcreteRelationship(relationship);
	}

	protected void addAxiom(ReferenceSetMember referenceSetMember) {
		this.dataStore.createAxiom(referenceSetMember);
	}

	protected void addStatedRelationship(Relationship relationship) {
		this.dataStore.createStatedRelationship(relationship);
	}

	protected void addTextDefinition(Description description) {
		this.dataStore.createTextDefinition(description);
	}

	protected void addReferenceSetMember(ReferenceSetMember referenceSetMember) {
		this.dataStore.createReferenceSetMember(referenceSetMember);
	}

	protected void cacheFileName(String refsetId, String refsetName) {
		this.dataStore.cacheFileName(Map.of(refsetId, refsetName));
	}

	protected void cacheFileHeader(String refsetId, String refsetHeader) {
		this.dataStore.cacheHeader(Map.of(refsetId, refsetHeader));
	}

	protected void cacheEmptyFile(String fileName, String fileHeader) {
		this.dataStore.cacheEmptyFiles(Map.of(fileName, fileHeader));
	}

	protected String createExportLocation() {
		return "target/" + System.currentTimeMillis();
	}

	protected Map<String, List<String>> unzipExportLocation(String zipFilePath) {
		zipFilePath = zipFilePath + ".zip";
		if (zipFilePath == null || zipFilePath.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<String, List<String>> lines = new HashMap<>();
		try (ZipFile zipFile = new ZipFile(zipFilePath)) {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = entries.nextElement();

				if (zipEntry.isDirectory()) {
					continue;
				}

				try (InputStream inputStream = zipFile.getInputStream(zipEntry); BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

					List<String> fileLines = bufferedReader.lines().collect(Collectors.toList());
					lines.put(zipEntry.getName(), fileLines);
				}
			}
		} catch (Exception e) {
			return Collections.emptyMap();
		}

		return lines;
	}
}
