package org.snomed.snomededitionpackager.domain.exporting;

import org.junit.jupiter.api.Test;
import org.snomed.snomededitionpackager.IntegrationTest;
import org.snomed.snomededitionpackager.domain.rf2.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ExportServiceTest extends IntegrationTest {
	@Test
	void export_ShouldWriteExpectedLines_WhenGivenConcepts() {
		// data
		Concept jan = new Concept().setConceptId("12345").setEffectiveTime("20250101").setActive("1").setModuleId("A").setDefinitionStatusId("PRIMITIVE");
		addConcept(jan);

		Concept feb = new Concept().setConceptId("12345").setEffectiveTime("20250201").setActive("0").setModuleId("A").setDefinitionStatusId("PRIMITIVE");
		addConcept(feb);

		Concept mar = new Concept().setConceptId("12345").setEffectiveTime("20250301").setActive("1").setModuleId("A").setDefinitionStatusId("PRIMITIVE");
		addConcept(mar);

		// export
		String exportLocation = createExportLocation();
		String rf2PackageName = exportLocation.split("/")[1];
		boolean success = exportService.export(new ExportConfiguration(exportLocation, "MVN", "20250101", "*", "true", "false", "*"));
		assertTrue(success);

		// unzip
		Map<String, List<String>> unzipped = unzipExportLocation(exportLocation);
		List<String> full = unzipped.get(FileNameService.getConcept(rf2PackageName, RF2.FULL, "MVN", "20250101"));
		List<String> snapshot = unzipped.get(FileNameService.getConcept(rf2PackageName, RF2.SNAPSHOT, "MVN", "20250101"));

		// assert
		assertEquals(4, full.size());
		assertEquals(RF2.HEADER_CONCEPT, full.get(0));
		assertEquals(jan.toRF2(), full.get(3));
		assertEquals(feb.toRF2(), full.get(2));
		assertEquals(mar.toRF2(), full.get(1));

		assertEquals(2, snapshot.size());
		assertEquals(RF2.HEADER_CONCEPT, snapshot.get(0));
		assertEquals(mar.toRF2(), snapshot.get(1));
	}

	@Test
	void export_ShouldWriteConceptsInOrder_WhenSorted() {
		// data
		Concept a1 = new Concept().setConceptId("12345678910").setEffectiveTime("20250301").setActive("1").setModuleId("A").setDefinitionStatusId("PRIMITIVE");
		addConcept(a1);

		Concept a2 = new Concept().setConceptId("12345678910").setEffectiveTime("20250201").setActive("0").setModuleId("A").setDefinitionStatusId("PRIMITIVE");
		addConcept(a2);

		Concept a3 = new Concept().setConceptId("12345678910").setEffectiveTime("20250101").setActive("1").setModuleId("A").setDefinitionStatusId("PRIMITIVE");
		addConcept(a3);

		Concept b1 = new Concept().setConceptId("12345").setEffectiveTime("20250301").setActive("1").setModuleId("A").setDefinitionStatusId("PRIMITIVE");
		addConcept(b1);

		Concept b2 = new Concept().setConceptId("12345").setEffectiveTime("20250201").setActive("0").setModuleId("A").setDefinitionStatusId("PRIMITIVE");
		addConcept(b2);

		Concept b3 = new Concept().setConceptId("12345").setEffectiveTime("20250101").setActive("1").setModuleId("A").setDefinitionStatusId("PRIMITIVE");
		addConcept(b3);

		// export
		String exportLocation = createExportLocation();
		String rf2PackageName = exportLocation.split("/")[1];
		boolean success = exportService.export(new ExportConfiguration(exportLocation, "MVN", "20250101", "*", "true", "true", "*"));
		assertTrue(success);

		// unzip
		Map<String, List<String>> unzipped = unzipExportLocation(exportLocation);
		List<String> full = unzipped.get(FileNameService.getConcept(rf2PackageName, RF2.FULL, "MVN", "20250101"));
		List<String> snapshot = unzipped.get(FileNameService.getConcept(rf2PackageName, RF2.SNAPSHOT, "MVN", "20250101"));

		// assert
		assertEquals(7, full.size());
		assertEquals(RF2.HEADER_CONCEPT, full.get(0));
		assertEquals(b3.toRF2(), full.get(1));
		assertEquals(b2.toRF2(), full.get(2));
		assertEquals(b1.toRF2(), full.get(3));

		assertEquals(3, snapshot.size());
		assertEquals(RF2.HEADER_CONCEPT, snapshot.get(0));
		assertEquals(b1.toRF2(), snapshot.get(1));
		assertEquals(a1.toRF2(), snapshot.get(2));
	}

	@Test
	void export_ShouldWriteExpectedLines_WhenGivenDescriptions() {
		// data
		Description jan = new Description().setDescriptionId("678910").setEffectiveTime("20250101").setActive("1").setModuleId("A").setConceptId("12345").setLanguageCode("en").setTypeId("FSN").setCaseSignificanceId("CI");
		addDescription(jan);

		Description feb = new Description().setDescriptionId("678910").setEffectiveTime("20250201").setActive("0").setModuleId("A").setConceptId("12345").setLanguageCode("en").setTypeId("FSN").setCaseSignificanceId("CI");
		addDescription(feb);

		Description mar = new Description().setDescriptionId("678910").setEffectiveTime("20250301").setActive("1").setModuleId("A").setConceptId("12345").setLanguageCode("en").setTypeId("FSN").setCaseSignificanceId("CI");
		addDescription(mar);

		// export
		String exportLocation = createExportLocation();
		String rf2PackageName = exportLocation.split("/")[1];
		boolean success = exportService.export(new ExportConfiguration(exportLocation, "MVN", "20250101", "*", "true", "false", "*"));
		assertTrue(success);

		// unzip
		Map<String, List<String>> unzipped = unzipExportLocation(exportLocation);
		List<String> full = unzipped.get(FileNameService.getDescription(rf2PackageName, RF2.FULL, "en", "MVN", "20250101"));
		List<String> snapshot = unzipped.get(FileNameService.getDescription(rf2PackageName, RF2.SNAPSHOT, "en", "MVN", "20250101"));

		// assert
		assertEquals(4, full.size());
		assertEquals(RF2.HEADER_DESCRIPTION, full.get(0));
		assertEquals(jan.toRF2(), full.get(3));
		assertEquals(feb.toRF2(), full.get(2));
		assertEquals(mar.toRF2(), full.get(1));

		assertEquals(2, snapshot.size());
		assertEquals(RF2.HEADER_DESCRIPTION, snapshot.get(0));
		assertEquals(mar.toRF2(), snapshot.get(1));
	}

	@Test
	void export_ShouldWriteDescriptionsInOrder_WhenSorted() {
		// data
		Description a1 = new Description().setDescriptionId("67891011121314").setEffectiveTime("20250301").setActive("1").setModuleId("A").setConceptId("12345").setLanguageCode("en").setTypeId("FSN").setCaseSignificanceId("CI");
		addDescription(a1);

		Description a2 = new Description().setDescriptionId("67891011121314").setEffectiveTime("20250201").setActive("0").setModuleId("A").setConceptId("12345").setLanguageCode("en").setTypeId("FSN").setCaseSignificanceId("CI");
		addDescription(a2);

		Description a3 = new Description().setDescriptionId("67891011121314").setEffectiveTime("20250101").setActive("1").setModuleId("A").setConceptId("12345").setLanguageCode("en").setTypeId("FSN").setCaseSignificanceId("CI");
		addDescription(a3);

		Description b1 = new Description().setDescriptionId("678910").setEffectiveTime("20250301").setActive("1").setModuleId("A").setConceptId("12345").setLanguageCode("en").setTypeId("FSN").setCaseSignificanceId("CI");
		addDescription(b1);

		Description b2 = new Description().setDescriptionId("678910").setEffectiveTime("20250201").setActive("0").setModuleId("A").setConceptId("12345").setLanguageCode("en").setTypeId("FSN").setCaseSignificanceId("CI");
		addDescription(b2);

		Description b3 = new Description().setDescriptionId("678910").setEffectiveTime("20250101").setActive("1").setModuleId("A").setConceptId("12345").setLanguageCode("en").setTypeId("FSN").setCaseSignificanceId("CI");
		addDescription(b3);

		// export
		String exportLocation = createExportLocation();
		String rf2PackageName = exportLocation.split("/")[1];
		boolean success = exportService.export(new ExportConfiguration(exportLocation, "MVN", "20250101", "*", "true", "true", "*"));
		assertTrue(success);

		// unzip
		Map<String, List<String>> unzipped = unzipExportLocation(exportLocation);
		List<String> full = unzipped.get(FileNameService.getDescription(rf2PackageName, RF2.FULL, "en", "MVN", "20250101"));
		List<String> snapshot = unzipped.get(FileNameService.getDescription(rf2PackageName, RF2.SNAPSHOT, "en", "MVN", "20250101"));

		// assert
		assertEquals(7, full.size());
		assertEquals(RF2.HEADER_DESCRIPTION, full.get(0));
		assertEquals(b3.toRF2(), full.get(1));
		assertEquals(b2.toRF2(), full.get(2));
		assertEquals(b1.toRF2(), full.get(3));

		assertEquals(3, snapshot.size());
		assertEquals(RF2.HEADER_DESCRIPTION, snapshot.get(0));
		assertEquals(b1.toRF2(), snapshot.get(1));
		assertEquals(a1.toRF2(), snapshot.get(2));
	}

	@Test
	void export_ShouldWriteExpectedLines_WhenGivenIdentifiers() {
		// data
		Identifier jan = new Identifier().setAlternateIdentifier("12345").setEffectiveTime("20250101").setActive("1").setModuleId("A").setIdentifierSchemeId("B").setReferencedComponentId("678910");
		addIdentifier(jan);

		Identifier feb = new Identifier().setAlternateIdentifier("12345").setEffectiveTime("20250201").setActive("0").setModuleId("A").setIdentifierSchemeId("B").setReferencedComponentId("678910");
		addIdentifier(feb);

		Identifier mar = new Identifier().setAlternateIdentifier("12345").setEffectiveTime("20250301").setActive("1").setModuleId("A").setIdentifierSchemeId("B").setReferencedComponentId("678910");
		addIdentifier(mar);

		// export
		String exportLocation = createExportLocation();
		String rf2PackageName = exportLocation.split("/")[1];
		boolean success = exportService.export(new ExportConfiguration(exportLocation, "MVN", "20250101", "*", "true", "false", "*"));
		assertTrue(success);

		// unzip
		Map<String, List<String>> unzipped = unzipExportLocation(exportLocation);
		List<String> full = unzipped.get(FileNameService.getIdentifier(rf2PackageName, RF2.FULL, "MVN", "20250101"));
		List<String> snapshot = unzipped.get(FileNameService.getIdentifier(rf2PackageName, RF2.SNAPSHOT, "MVN", "20250101"));

		// assert
		assertEquals(4, full.size());
		assertEquals(RF2.HEADER_IDENTIFIER, full.get(0));
		assertEquals(jan.toRF2(), full.get(3));
		assertEquals(feb.toRF2(), full.get(2));
		assertEquals(mar.toRF2(), full.get(1));

		assertEquals(2, snapshot.size());
		assertEquals(RF2.HEADER_IDENTIFIER, snapshot.get(0));
		assertEquals(mar.toRF2(), snapshot.get(1));
	}

	@Test
	void export_ShouldWriteIdentifiersInOrder_WhenSorted() {
		// data
		Identifier a1 = new Identifier().setAlternateIdentifier("12345678910").setEffectiveTime("20250301").setActive("1").setModuleId("A").setIdentifierSchemeId("B").setReferencedComponentId("678910");
		addIdentifier(a1);

		Identifier a2 = new Identifier().setAlternateIdentifier("12345678910").setEffectiveTime("20250201").setActive("0").setModuleId("A").setIdentifierSchemeId("B").setReferencedComponentId("678910");
		addIdentifier(a2);

		Identifier a3 = new Identifier().setAlternateIdentifier("12345678910").setEffectiveTime("20250101").setActive("1").setModuleId("A").setIdentifierSchemeId("B").setReferencedComponentId("678910");
		addIdentifier(a3);

		Identifier b1 = new Identifier().setAlternateIdentifier("12345").setEffectiveTime("20250301").setActive("1").setModuleId("A").setIdentifierSchemeId("B").setReferencedComponentId("678910");
		addIdentifier(b1);

		Identifier b2 = new Identifier().setAlternateIdentifier("12345").setEffectiveTime("20250201").setActive("0").setModuleId("A").setIdentifierSchemeId("B").setReferencedComponentId("678910");
		addIdentifier(b2);

		Identifier b3 = new Identifier().setAlternateIdentifier("12345").setEffectiveTime("20250101").setActive("1").setModuleId("A").setIdentifierSchemeId("B").setReferencedComponentId("678910");
		addIdentifier(b3);

		// export
		String exportLocation = createExportLocation();
		String rf2PackageName = exportLocation.split("/")[1];
		boolean success = exportService.export(new ExportConfiguration(exportLocation, "MVN", "20250101", "*", "true", "true", "*"));
		assertTrue(success);

		// unzip
		Map<String, List<String>> unzipped = unzipExportLocation(exportLocation);
		List<String> full = unzipped.get(FileNameService.getIdentifier(rf2PackageName, RF2.FULL, "MVN", "20250101"));
		List<String> snapshot = unzipped.get(FileNameService.getIdentifier(rf2PackageName, RF2.SNAPSHOT, "MVN", "20250101"));

		// assert
		assertEquals(7, full.size());
		assertEquals(RF2.HEADER_IDENTIFIER, full.get(0));
		assertEquals(b3.toRF2(), full.get(1));
		assertEquals(b2.toRF2(), full.get(2));
		assertEquals(b1.toRF2(), full.get(3));

		assertEquals(3, snapshot.size());
		assertEquals(RF2.HEADER_IDENTIFIER, snapshot.get(0));
		assertEquals(b1.toRF2(), snapshot.get(1));
		assertEquals(a1.toRF2(), snapshot.get(2));
	}

	@Test
	void export_ShouldWriteExpectedLines_WhenGivenRelationships() {
		// data
		Relationship jan = new Relationship().setRelationshipId("12345").setEffectiveTime("20250101").setActive("1").setModuleId("A").setSourceId("678910").setDestinationId("1112131415").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addRelationship(jan);

		Relationship feb = new Relationship().setRelationshipId("12345").setEffectiveTime("20250201").setActive("0").setModuleId("A").setSourceId("678910").setDestinationId("1112131415").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addRelationship(feb);

		Relationship mar = new Relationship().setRelationshipId("12345").setEffectiveTime("20250301").setActive("1").setModuleId("A").setSourceId("678910").setDestinationId("1112131415").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addRelationship(mar);

		// export
		String exportLocation = createExportLocation();
		String rf2PackageName = exportLocation.split("/")[1];
		boolean success = exportService.export(new ExportConfiguration(exportLocation, "MVN", "20250101", "*", "true", "false", "*"));
		assertTrue(success);

		// unzip
		Map<String, List<String>> unzipped = unzipExportLocation(exportLocation);
		List<String> full = unzipped.get(FileNameService.getRelationship(rf2PackageName, RF2.FULL, "MVN", "20250101"));
		List<String> snapshot = unzipped.get(FileNameService.getRelationship(rf2PackageName, RF2.SNAPSHOT, "MVN", "20250101"));

		// assert
		assertEquals(4, full.size());
		assertEquals(RF2.HEADER_RELATIONSHIP, full.get(0));
		assertEquals(jan.toRF2(), full.get(3));
		assertEquals(feb.toRF2(), full.get(2));
		assertEquals(mar.toRF2(), full.get(1));

		assertEquals(2, snapshot.size());
		assertEquals(RF2.HEADER_RELATIONSHIP, snapshot.get(0));
		assertEquals(mar.toRF2(), snapshot.get(1));
	}

	@Test
	void export_ShouldWriteRelationshipsInOrder_WhenSorted() {
		// data
		Relationship a1 = new Relationship().setRelationshipId("12345678910").setEffectiveTime("20250301").setActive("1").setModuleId("A").setSourceId("678910").setDestinationId("1112131415").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addRelationship(a1);

		Relationship a2 = new Relationship().setRelationshipId("12345678910").setEffectiveTime("20250201").setActive("0").setModuleId("A").setSourceId("678910").setDestinationId("1112131415").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addRelationship(a2);

		Relationship a3 = new Relationship().setRelationshipId("12345678910").setEffectiveTime("20250101").setActive("1").setModuleId("A").setSourceId("678910").setDestinationId("1112131415").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addRelationship(a3);

		Relationship b1 = new Relationship().setRelationshipId("12345").setEffectiveTime("20250301").setActive("1").setModuleId("A").setSourceId("678910").setDestinationId("1112131415").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addRelationship(b1);

		Relationship b2 = new Relationship().setRelationshipId("12345").setEffectiveTime("20250201").setActive("0").setModuleId("A").setSourceId("678910").setDestinationId("1112131415").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addRelationship(b2);

		Relationship b3 = new Relationship().setRelationshipId("12345").setEffectiveTime("20250101").setActive("1").setModuleId("A").setSourceId("678910").setDestinationId("1112131415").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addRelationship(b3);

		// export
		String exportLocation = createExportLocation();
		String rf2PackageName = exportLocation.split("/")[1];
		boolean success = exportService.export(new ExportConfiguration(exportLocation, "MVN", "20250101", "*", "true", "true", "*"));
		assertTrue(success);

		// unzip
		Map<String, List<String>> unzipped = unzipExportLocation(exportLocation);
		List<String> full = unzipped.get(FileNameService.getRelationship(rf2PackageName, RF2.FULL, "MVN", "20250101"));
		List<String> snapshot = unzipped.get(FileNameService.getRelationship(rf2PackageName, RF2.SNAPSHOT, "MVN", "20250101"));

		// assert
		assertEquals(7, full.size());
		assertEquals(RF2.HEADER_RELATIONSHIP, full.get(0));
		assertEquals(b3.toRF2(), full.get(1));
		assertEquals(b2.toRF2(), full.get(2));
		assertEquals(b1.toRF2(), full.get(3));

		assertEquals(3, snapshot.size());
		assertEquals(RF2.HEADER_RELATIONSHIP, snapshot.get(0));
		assertEquals(b1.toRF2(), snapshot.get(1));
		assertEquals(a1.toRF2(), snapshot.get(2));
	}

	@Test
	void export_ShouldWriteExpectedLines_WhenGivenConcreteRelationships() {
		// data
		ConcreteRelationship jan = new ConcreteRelationship().setRelationshipId("12345").setEffectiveTime("20250101").setActive("1").setModuleId("A").setSourceId("678910").setValue("#1").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addRelationship(jan);

		ConcreteRelationship feb = new ConcreteRelationship().setRelationshipId("12345").setEffectiveTime("20250201").setActive("0").setModuleId("A").setSourceId("678910").setValue("#1").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addRelationship(feb);

		ConcreteRelationship mar = new ConcreteRelationship().setRelationshipId("12345").setEffectiveTime("20250301").setActive("1").setModuleId("A").setSourceId("678910").setValue("#1").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addRelationship(mar);

		// export
		String exportLocation = createExportLocation();
		String rf2PackageName = exportLocation.split("/")[1];
		boolean success = exportService.export(new ExportConfiguration(exportLocation, "MVN", "20250101", "*", "true", "false", "*"));
		assertTrue(success);

		// unzip
		Map<String, List<String>> unzipped = unzipExportLocation(exportLocation);
		List<String> full = unzipped.get(FileNameService.getConcreteRelationship(rf2PackageName, RF2.FULL, "MVN", "20250101"));
		List<String> snapshot = unzipped.get(FileNameService.getConcreteRelationship(rf2PackageName, RF2.SNAPSHOT, "MVN", "20250101"));

		// assert
		assertEquals(4, full.size());
		assertEquals(RF2.HEADER_CONCRETE_RELATIONSHIP, full.get(0));
		assertEquals(jan.toRF2(), full.get(3));
		assertEquals(feb.toRF2(), full.get(2));
		assertEquals(mar.toRF2(), full.get(1));

		assertEquals(2, snapshot.size());
		assertEquals(RF2.HEADER_CONCRETE_RELATIONSHIP, snapshot.get(0));
		assertEquals(mar.toRF2(), snapshot.get(1));
	}

	@Test
	void export_ShouldWriteConcreteRelationshipsInOrder_WhenSorted() {
		// data
		ConcreteRelationship a1 = new ConcreteRelationship().setRelationshipId("12345678910").setEffectiveTime("20250301").setActive("1").setModuleId("A").setSourceId("678910").setValue("#1").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addRelationship(a1);

		ConcreteRelationship a2 = new ConcreteRelationship().setRelationshipId("12345678910").setEffectiveTime("20250201").setActive("0").setModuleId("A").setSourceId("678910").setValue("#1").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addRelationship(a2);

		ConcreteRelationship a3 = new ConcreteRelationship().setRelationshipId("12345678910").setEffectiveTime("20250101").setActive("1").setModuleId("A").setSourceId("678910").setValue("#1").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addRelationship(a3);

		ConcreteRelationship b1 = new ConcreteRelationship().setRelationshipId("12345").setEffectiveTime("20250301").setActive("1").setModuleId("A").setSourceId("678910").setValue("#1").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addRelationship(b1);

		ConcreteRelationship b2 = new ConcreteRelationship().setRelationshipId("12345").setEffectiveTime("20250201").setActive("0").setModuleId("A").setSourceId("678910").setValue("#1").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addRelationship(b2);

		ConcreteRelationship b3 = new ConcreteRelationship().setRelationshipId("12345").setEffectiveTime("20250101").setActive("1").setModuleId("A").setSourceId("678910").setValue("#1").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addRelationship(b3);

		// export
		String exportLocation = createExportLocation();
		String rf2PackageName = exportLocation.split("/")[1];
		boolean success = exportService.export(new ExportConfiguration(exportLocation, "MVN", "20250101", "*", "true", "true", "*"));
		assertTrue(success);

		// unzip
		Map<String, List<String>> unzipped = unzipExportLocation(exportLocation);
		List<String> full = unzipped.get(FileNameService.getConcreteRelationship(rf2PackageName, RF2.FULL, "MVN", "20250101"));
		List<String> snapshot = unzipped.get(FileNameService.getConcreteRelationship(rf2PackageName, RF2.SNAPSHOT, "MVN", "20250101"));

		// assert
		assertEquals(7, full.size());
		assertEquals(RF2.HEADER_CONCRETE_RELATIONSHIP, full.get(0));
		assertEquals(b3.toRF2(), full.get(1));
		assertEquals(b2.toRF2(), full.get(2));
		assertEquals(b1.toRF2(), full.get(3));

		assertEquals(3, snapshot.size());
		assertEquals(RF2.HEADER_CONCRETE_RELATIONSHIP, snapshot.get(0));
		assertEquals(b1.toRF2(), snapshot.get(1));
		assertEquals(a1.toRF2(), snapshot.get(2));
	}

	@Test
	void export_ShouldWriteExpectedLines_WhenGivenAxioms() {
		// data
		String uuid = UUID.randomUUID().toString();
		ReferenceSetMember jan = new ReferenceSetMember().setId(uuid).setEffectiveTime("20250101").setActive("1").setModuleId("A").setRefsetId(RF2.REFSET_OWL_AXIOM).setReferencedComponentId("12345").setFieldNames(new String[]{"owlExpression"}).setOtherValues(new String[]{"a -> b"});
		addAxiom(jan);

		ReferenceSetMember feb = new ReferenceSetMember().setId(uuid).setEffectiveTime("20250201").setActive("0").setModuleId("A").setRefsetId(RF2.REFSET_OWL_AXIOM).setReferencedComponentId("12345").setFieldNames(new String[]{"owlExpression"}).setOtherValues(new String[]{"a -> b"});
		addAxiom(feb);

		ReferenceSetMember mar = new ReferenceSetMember().setId(uuid).setEffectiveTime("20250301").setActive("1").setModuleId("A").setRefsetId(RF2.REFSET_OWL_AXIOM).setReferencedComponentId("12345").setFieldNames(new String[]{"owlExpression"}).setOtherValues(new String[]{"a -> b"});
		addAxiom(mar);

		// export
		String exportLocation = createExportLocation();
		String rf2PackageName = exportLocation.split("/")[1];
		boolean success = exportService.export(new ExportConfiguration(exportLocation, "MVN", "20250101", "*", "true", "false", "*"));
		assertTrue(success);

		// unzip
		Map<String, List<String>> unzipped = unzipExportLocation(exportLocation);
		List<String> full = unzipped.get(FileNameService.getAxiom(rf2PackageName, RF2.FULL, "MVN", "20250101"));
		List<String> snapshot = unzipped.get(FileNameService.getAxiom(rf2PackageName, RF2.SNAPSHOT, "MVN", "20250101"));

		// assert
		assertEquals(4, full.size());
		assertEquals(RF2.HEADER_OWL_AXIOM, full.get(0));
		assertEquals(jan.toRF2(), full.get(3));
		assertEquals(feb.toRF2(), full.get(2));
		assertEquals(mar.toRF2(), full.get(1));

		assertEquals(2, snapshot.size());
		assertEquals(RF2.HEADER_OWL_AXIOM, snapshot.get(0));
		assertEquals(mar.toRF2(), snapshot.get(1));
	}

	@Test
	void export_ShouldWriteAxiomsInOrder_WhenSorted() {
		// data
		ReferenceSetMember a1 = new ReferenceSetMember().setId("6da0d5d5-cb3a-48f7-ac4b-10dffb024b5b").setEffectiveTime("20250301").setActive("1").setModuleId("A").setRefsetId(RF2.REFSET_OWL_AXIOM).setReferencedComponentId("12345").setFieldNames(new String[]{"owlExpression"}).setOtherValues(new String[]{"a -> b"});
		addAxiom(a1);

		ReferenceSetMember a2 = new ReferenceSetMember().setId("6da0d5d5-cb3a-48f7-ac4b-10dffb024b5b").setEffectiveTime("20250201").setActive("0").setModuleId("A").setRefsetId(RF2.REFSET_OWL_AXIOM).setReferencedComponentId("12345").setFieldNames(new String[]{"owlExpression"}).setOtherValues(new String[]{"a -> b"});
		addAxiom(a2);

		ReferenceSetMember a3 = new ReferenceSetMember().setId("6da0d5d5-cb3a-48f7-ac4b-10dffb024b5b").setEffectiveTime("20250101").setActive("1").setModuleId("A").setRefsetId(RF2.REFSET_OWL_AXIOM).setReferencedComponentId("12345").setFieldNames(new String[]{"owlExpression"}).setOtherValues(new String[]{"a -> b"});
		addAxiom(a3);

		ReferenceSetMember b1 = new ReferenceSetMember().setId("a7fe03fa-a2fe-4946-88d3-a5f402f750ae").setEffectiveTime("20250301").setActive("1").setModuleId("A").setRefsetId(RF2.REFSET_OWL_AXIOM).setReferencedComponentId("12345").setFieldNames(new String[]{"owlExpression"}).setOtherValues(new String[]{"a -> b"});
		addAxiom(b1);

		ReferenceSetMember b2 = new ReferenceSetMember().setId("a7fe03fa-a2fe-4946-88d3-a5f402f750ae").setEffectiveTime("20250201").setActive("0").setModuleId("A").setRefsetId(RF2.REFSET_OWL_AXIOM).setReferencedComponentId("12345").setFieldNames(new String[]{"owlExpression"}).setOtherValues(new String[]{"a -> b"});
		addAxiom(b2);

		ReferenceSetMember b3 = new ReferenceSetMember().setId("a7fe03fa-a2fe-4946-88d3-a5f402f750ae").setEffectiveTime("20250101").setActive("1").setModuleId("A").setRefsetId(RF2.REFSET_OWL_AXIOM).setReferencedComponentId("12345").setFieldNames(new String[]{"owlExpression"}).setOtherValues(new String[]{"a -> b"});
		addAxiom(b3);

		// export
		String exportLocation = createExportLocation();
		String rf2PackageName = exportLocation.split("/")[1];
		boolean success = exportService.export(new ExportConfiguration(exportLocation, "MVN", "20250101", "*", "true", "true", "*"));
		assertTrue(success);

		// unzip
		Map<String, List<String>> unzipped = unzipExportLocation(exportLocation);
		List<String> full = unzipped.get(FileNameService.getAxiom(rf2PackageName, RF2.FULL, "MVN", "20250101"));
		List<String> snapshot = unzipped.get(FileNameService.getAxiom(rf2PackageName, RF2.SNAPSHOT, "MVN", "20250101"));

		// assert
		assertEquals(7, full.size());
		assertEquals(RF2.HEADER_OWL_AXIOM, full.get(0));
		assertEquals(b3.toRF2(), full.get(1));
		assertEquals(b2.toRF2(), full.get(2));
		assertEquals(b1.toRF2(), full.get(3));

		assertEquals(3, snapshot.size());
		assertEquals(RF2.HEADER_OWL_AXIOM, snapshot.get(0));
		assertEquals(b1.toRF2(), snapshot.get(1));
		assertEquals(a1.toRF2(), snapshot.get(2));
	}

	@Test
	void export_ShouldWriteExpectedLines_WhenGivenStatedRelationships() {
		// data
		Relationship jan = new Relationship().setRelationshipId("12345").setEffectiveTime("20250101").setActive("1").setModuleId("A").setSourceId("678910").setDestinationId("1112131415").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addStatedRelationship(jan);

		Relationship feb = new Relationship().setRelationshipId("12345").setEffectiveTime("20250201").setActive("0").setModuleId("A").setSourceId("678910").setDestinationId("1112131415").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addStatedRelationship(feb);

		Relationship mar = new Relationship().setRelationshipId("12345").setEffectiveTime("20250301").setActive("1").setModuleId("A").setSourceId("678910").setDestinationId("1112131415").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addStatedRelationship(mar);

		// export
		String exportLocation = createExportLocation();
		String rf2PackageName = exportLocation.split("/")[1];
		boolean success = exportService.export(new ExportConfiguration(exportLocation, "MVN", "20250101", "*", "true", "false", "*"));
		assertTrue(success);

		// unzip
		Map<String, List<String>> unzipped = unzipExportLocation(exportLocation);
		List<String> full = unzipped.get(FileNameService.getStatedRelationship(rf2PackageName, RF2.FULL, "MVN", "20250101"));
		List<String> snapshot = unzipped.get(FileNameService.getStatedRelationship(rf2PackageName, RF2.SNAPSHOT, "MVN", "20250101"));

		// assert
		assertEquals(4, full.size());
		assertEquals(RF2.HEADER_STATED_RELATIONSHIP, full.get(0));
		assertEquals(jan.toRF2(), full.get(3));
		assertEquals(feb.toRF2(), full.get(2));
		assertEquals(mar.toRF2(), full.get(1));

		assertEquals(2, snapshot.size());
		assertEquals(RF2.HEADER_STATED_RELATIONSHIP, snapshot.get(0));
		assertEquals(mar.toRF2(), snapshot.get(1));
	}

	@Test
	void export_ShouldWriteStatedRelationshipsInOrder_WhenSorted() {
		// data
		Relationship a1 = new Relationship().setRelationshipId("12345678910").setEffectiveTime("20250301").setActive("1").setModuleId("A").setSourceId("678910").setDestinationId("1112131415").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addStatedRelationship(a1);

		Relationship a2 = new Relationship().setRelationshipId("12345678910").setEffectiveTime("20250201").setActive("0").setModuleId("A").setSourceId("678910").setDestinationId("1112131415").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addStatedRelationship(a2);

		Relationship a3 = new Relationship().setRelationshipId("12345678910").setEffectiveTime("20250101").setActive("1").setModuleId("A").setSourceId("678910").setDestinationId("1112131415").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addStatedRelationship(a3);

		Relationship b1 = new Relationship().setRelationshipId("12345").setEffectiveTime("20250301").setActive("1").setModuleId("A").setSourceId("678910").setDestinationId("1112131415").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addStatedRelationship(b1);

		Relationship b2 = new Relationship().setRelationshipId("12345").setEffectiveTime("20250201").setActive("0").setModuleId("A").setSourceId("678910").setDestinationId("1112131415").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addStatedRelationship(b2);

		Relationship b3 = new Relationship().setRelationshipId("12345").setEffectiveTime("20250101").setActive("1").setModuleId("A").setSourceId("678910").setDestinationId("1112131415").setRelationshipGroup("0").setTypeId("IS_A").setCharacteristicTypeId("INFERRED").setModifierId("SOME");
		addStatedRelationship(b3);

		// export
		String exportLocation = createExportLocation();
		String rf2PackageName = exportLocation.split("/")[1];
		boolean success = exportService.export(new ExportConfiguration(exportLocation, "MVN", "20250101", "*", "true", "true", "*"));
		assertTrue(success);

		// unzip
		Map<String, List<String>> unzipped = unzipExportLocation(exportLocation);
		List<String> full = unzipped.get(FileNameService.getStatedRelationship(rf2PackageName, RF2.FULL, "MVN", "20250101"));
		List<String> snapshot = unzipped.get(FileNameService.getStatedRelationship(rf2PackageName, RF2.SNAPSHOT, "MVN", "20250101"));

		// assert
		assertEquals(7, full.size());
		assertEquals(RF2.HEADER_STATED_RELATIONSHIP, full.get(0));
		assertEquals(b3.toRF2(), full.get(1));
		assertEquals(b2.toRF2(), full.get(2));
		assertEquals(b1.toRF2(), full.get(3));

		assertEquals(3, snapshot.size());
		assertEquals(RF2.HEADER_STATED_RELATIONSHIP, snapshot.get(0));
		assertEquals(b1.toRF2(), snapshot.get(1));
		assertEquals(a1.toRF2(), snapshot.get(2));
	}

	@Test
	void export_ShouldWriteExpectedLines_WhenGivenTextDefinitions() {
		// data
		Description jan = new Description().setDescriptionId("678910").setEffectiveTime("20250101").setActive("1").setModuleId("A").setConceptId("12345").setLanguageCode("en").setTypeId("FSN").setCaseSignificanceId("CI");
		addTextDefinition(jan);

		Description feb = new Description().setDescriptionId("678910").setEffectiveTime("20250201").setActive("0").setModuleId("A").setConceptId("12345").setLanguageCode("en").setTypeId("FSN").setCaseSignificanceId("CI");
		addTextDefinition(feb);

		Description mar = new Description().setDescriptionId("678910").setEffectiveTime("20250301").setActive("1").setModuleId("A").setConceptId("12345").setLanguageCode("en").setTypeId("FSN").setCaseSignificanceId("CI");
		addTextDefinition(mar);

		// export
		String exportLocation = createExportLocation();
		String rf2PackageName = exportLocation.split("/")[1];
		boolean success = exportService.export(new ExportConfiguration(exportLocation, "MVN", "20250101", "*", "true", "false", "*"));
		assertTrue(success);

		// unzip
		Map<String, List<String>> unzipped = unzipExportLocation(exportLocation);
		List<String> full = unzipped.get(FileNameService.getTextDefinition(rf2PackageName, RF2.FULL, "en", "MVN", "20250101"));
		List<String> snapshot = unzipped.get(FileNameService.getTextDefinition(rf2PackageName, RF2.SNAPSHOT, "en", "MVN", "20250101"));

		// assert
		assertEquals(4, full.size());
		assertEquals(RF2.HEADER_TEXT_DEFINITION, full.get(0));
		assertEquals(jan.toRF2(), full.get(3));
		assertEquals(feb.toRF2(), full.get(2));
		assertEquals(mar.toRF2(), full.get(1));

		assertEquals(2, snapshot.size());
		assertEquals(RF2.HEADER_TEXT_DEFINITION, snapshot.get(0));
		assertEquals(mar.toRF2(), snapshot.get(1));
	}

	@Test
	void export_ShouldWriteTextDefinitionsInOrder_WhenSorted() {
		// data
		Description a1 = new Description().setDescriptionId("6789101112131415").setEffectiveTime("20250301").setActive("1").setModuleId("A").setConceptId("12345").setLanguageCode("en").setTypeId("FSN").setCaseSignificanceId("CI");
		addTextDefinition(a1);

		Description a2 = new Description().setDescriptionId("6789101112131415").setEffectiveTime("20250201").setActive("0").setModuleId("A").setConceptId("12345").setLanguageCode("en").setTypeId("FSN").setCaseSignificanceId("CI");
		addTextDefinition(a2);

		Description a3 = new Description().setDescriptionId("6789101112131415").setEffectiveTime("20250101").setActive("1").setModuleId("A").setConceptId("12345").setLanguageCode("en").setTypeId("FSN").setCaseSignificanceId("CI");
		addTextDefinition(a3);

		Description b1 = new Description().setDescriptionId("678910").setEffectiveTime("20250301").setActive("1").setModuleId("A").setConceptId("12345").setLanguageCode("en").setTypeId("FSN").setCaseSignificanceId("CI");
		addTextDefinition(b1);

		Description b2 = new Description().setDescriptionId("678910").setEffectiveTime("20250201").setActive("0").setModuleId("A").setConceptId("12345").setLanguageCode("en").setTypeId("FSN").setCaseSignificanceId("CI");
		addTextDefinition(b2);

		Description b3 = new Description().setDescriptionId("678910").setEffectiveTime("20250101").setActive("1").setModuleId("A").setConceptId("12345").setLanguageCode("en").setTypeId("FSN").setCaseSignificanceId("CI");
		addTextDefinition(b3);

		// export
		String exportLocation = createExportLocation();
		String rf2PackageName = exportLocation.split("/")[1];
		boolean success = exportService.export(new ExportConfiguration(exportLocation, "MVN", "20250101", "*", "true", "true", "*"));
		assertTrue(success);

		// unzip
		Map<String, List<String>> unzipped = unzipExportLocation(exportLocation);
		List<String> full = unzipped.get(FileNameService.getTextDefinition(rf2PackageName, RF2.FULL, "en", "MVN", "20250101"));
		List<String> snapshot = unzipped.get(FileNameService.getTextDefinition(rf2PackageName, RF2.SNAPSHOT, "en", "MVN", "20250101"));

		// assert
		assertEquals(7, full.size());
		assertEquals(RF2.HEADER_TEXT_DEFINITION, full.get(0));
		assertEquals(b3.toRF2(), full.get(1));
		assertEquals(b2.toRF2(), full.get(2));
		assertEquals(b1.toRF2(), full.get(3));

		assertEquals(3, snapshot.size());
		assertEquals(RF2.HEADER_TEXT_DEFINITION, snapshot.get(0));
		assertEquals(b1.toRF2(), snapshot.get(1));
		assertEquals(a1.toRF2(), snapshot.get(2));
	}

	@Test
	void export_ShouldWriteExpectedLines_WhenGivenSimpleRefset() {
		// data
		cacheFileName("SIMPLE", "Refset/Content/der2_Refset_Simple");
		cacheFileHeader("SIMPLE", RF2.HEADER_REFSET_SIMPLE);

		String uuid = UUID.randomUUID().toString();
		ReferenceSetMember jan = new ReferenceSetMember().setId(uuid).setEffectiveTime("20250101").setActive("1").setModuleId("A").setRefsetId("SIMPLE").setReferencedComponentId("12345");
		addReferenceSetMember(jan);

		ReferenceSetMember feb = new ReferenceSetMember().setId(uuid).setEffectiveTime("20250201").setActive("0").setModuleId("A").setRefsetId("SIMPLE").setReferencedComponentId("12345");
		addReferenceSetMember(feb);

		ReferenceSetMember mar = new ReferenceSetMember().setId(uuid).setEffectiveTime("20250301").setActive("1").setModuleId("A").setRefsetId("SIMPLE").setReferencedComponentId("12345");
		addReferenceSetMember(mar);

		// export
		String exportLocation = createExportLocation();
		String rf2PackageName = exportLocation.split("/")[1];
		boolean success = exportService.export(new ExportConfiguration(exportLocation, "MVN", "20250101", "*", "true", "false", "*"));
		assertTrue(success);

		// unzip
		Map<String, List<String>> unzipped = unzipExportLocation(exportLocation);
		List<String> full = unzipped.get(fileNameService.getReferenceSet(rf2PackageName, RF2.FULL, "MVN", "20250101", "SIMPLE"));
		List<String> snapshot = unzipped.get(fileNameService.getReferenceSet(rf2PackageName, RF2.SNAPSHOT, "MVN", "20250101", "SIMPLE"));

		// assert
		assertEquals(4, full.size());
		assertEquals(RF2.HEADER_REFSET_SIMPLE, full.get(0));
		assertEquals(jan.toRF2(), full.get(3));
		assertEquals(feb.toRF2(), full.get(2));
		assertEquals(mar.toRF2(), full.get(1));

		assertEquals(2, snapshot.size());
		assertEquals(RF2.HEADER_REFSET_SIMPLE, snapshot.get(0));
		assertEquals(mar.toRF2(), snapshot.get(1));
	}

	@Test
	void export_ShouldWriteSimpleRefsetInOrder_WhenSorted() {
		// data
		cacheFileName("SIMPLE", "Refset/Content/der2_Refset_Simple");
		cacheFileHeader("SIMPLE", RF2.HEADER_REFSET_SIMPLE);

		ReferenceSetMember a1 = new ReferenceSetMember().setId("436e81a8-a203-4181-82d2-162fa0f46d46").setEffectiveTime("20250301").setActive("1").setModuleId("A").setRefsetId("SIMPLE").setReferencedComponentId("12345");
		addReferenceSetMember(a1);

		ReferenceSetMember a2 = new ReferenceSetMember().setId("436e81a8-a203-4181-82d2-162fa0f46d46").setEffectiveTime("20250201").setActive("0").setModuleId("A").setRefsetId("SIMPLE").setReferencedComponentId("12345");
		addReferenceSetMember(a2);

		ReferenceSetMember a3 = new ReferenceSetMember().setId("436e81a8-a203-4181-82d2-162fa0f46d46").setEffectiveTime("20250101").setActive("1").setModuleId("A").setRefsetId("SIMPLE").setReferencedComponentId("12345");
		addReferenceSetMember(a3);

		ReferenceSetMember b1 = new ReferenceSetMember().setId("b888b47f-3659-4358-ad14-e3b593b92cb3").setEffectiveTime("20250301").setActive("1").setModuleId("A").setRefsetId("SIMPLE").setReferencedComponentId("12345");
		addReferenceSetMember(b1);

		ReferenceSetMember b2 = new ReferenceSetMember().setId("b888b47f-3659-4358-ad14-e3b593b92cb3").setEffectiveTime("20250201").setActive("0").setModuleId("A").setRefsetId("SIMPLE").setReferencedComponentId("12345");
		addReferenceSetMember(b2);

		ReferenceSetMember b3 = new ReferenceSetMember().setId("b888b47f-3659-4358-ad14-e3b593b92cb3").setEffectiveTime("20250101").setActive("1").setModuleId("A").setRefsetId("SIMPLE").setReferencedComponentId("12345");
		addReferenceSetMember(b3);

		// export
		String exportLocation = createExportLocation();
		String rf2PackageName = exportLocation.split("/")[1];
		boolean success = exportService.export(new ExportConfiguration(exportLocation, "MVN", "20250101", "*", "true", "true", "*"));
		assertTrue(success);

		// unzip
		Map<String, List<String>> unzipped = unzipExportLocation(exportLocation);
		List<String> full = unzipped.get(fileNameService.getReferenceSet(rf2PackageName, RF2.FULL, "MVN", "20250101", "SIMPLE"));
		List<String> snapshot = unzipped.get(fileNameService.getReferenceSet(rf2PackageName, RF2.SNAPSHOT, "MVN", "20250101", "SIMPLE"));

		// assert
		assertEquals(7, full.size());
		assertEquals(RF2.HEADER_REFSET_SIMPLE, full.get(0));
		assertEquals(b3.toRF2(), full.get(1));
		assertEquals(b2.toRF2(), full.get(2));
		assertEquals(b1.toRF2(), full.get(3));

		assertEquals(3, snapshot.size());
		assertEquals(RF2.HEADER_REFSET_SIMPLE, snapshot.get(0));
		assertEquals(b1.toRF2(), snapshot.get(1));
		assertEquals(a1.toRF2(), snapshot.get(2));
	}

	@Test
	void export_ShouldWriteExpectedMetadata() {
		// export
		String exportLocation = createExportLocation();
		String rf2PackageName = exportLocation.split("/")[1];
		boolean success = exportService.export(new ExportConfiguration(exportLocation, "MVN", "20250101", "*", "true", "true", "*"));
		assertTrue(success);

		// unzip
		Map<String, List<String>> unzipped = unzipExportLocation(exportLocation);

		// assert readme
		List<String> readme = unzipped.get(FileNameService.getReadme(rf2PackageName, "20250101"));
		assertFalse(readme.isEmpty());

		// assert json
		List<String> json = unzipped.get(FileNameService.getReleasePackageInformation(rf2PackageName));
		assertFalse(json.isEmpty());
	}

	@Test
	void export_ShouldWriteEmptyFiles() {
		// data
		String fileName = "Terminology/sct2_TextDefinition_Snapshot-xx_MVN_20250101";
		cacheEmptyFile(fileName, RF2.HEADER_TEXT_DEFINITION);

		// export
		String exportLocation = createExportLocation();
		String rf2PackageName = exportLocation.split("/")[1];
		boolean success = exportService.export(new ExportConfiguration(exportLocation, "MVN", "20250101", "*", "true", "true", "*"));
		assertTrue(success);

		// unzip
		Map<String, List<String>> unzipped = unzipExportLocation(exportLocation);

		// assert
		List<String> textDefinition = unzipped.get(rf2PackageName + "/" + fileName + ".txt");
		assertEquals(1, textDefinition.size());
		assertEquals(RF2.HEADER_TEXT_DEFINITION, textDefinition.get(0));
	}

	@Test
	void export_ShouldNotWriteDuplicateHeaders_WhenGivenEmptyKnownFiles() {
		// data
		String fileName = "Snapshot/Terminology/sct2_Identifier_Snapshot_MVN_20250101";
		cacheEmptyFile(fileName, RF2.HEADER_IDENTIFIER);

		// export
		String exportLocation = createExportLocation();
		String rf2PackageName = exportLocation.split("/")[1];
		boolean success = exportService.export(new ExportConfiguration(exportLocation, "MVN", "20250101", "*", "true", "true", "*"));
		assertTrue(success);

		// unzip
		Map<String, List<String>> unzipped = unzipExportLocation(exportLocation);

		// assert
		List<String> identifier = unzipped.get(rf2PackageName + "/" + fileName + ".txt");
		assertEquals(1, identifier.size());
		assertEquals(RF2.HEADER_IDENTIFIER, identifier.get(0));
	}
}