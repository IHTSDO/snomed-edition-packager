package org.snomed.snomededitionpackager.domain.exporting;

import org.junit.jupiter.api.Test;

import org.snomed.snomededitionpackager.IntegrationTest;
import org.snomed.snomededitionpackager.domain.rf2.RF2;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileNameServiceTest extends IntegrationTest {
	private static final String SHORT_NAME = "MVN";
	private static final String EFFECTIVE_TIME = "20250101";
	private static final String LANGUAGE_CODE = "en";

	@Test
	void fileNames_ShouldBeRecognised_WhenGivenConcept() {
		// Full
		String fullFileName = isolateFileName(FileNameService.getConcept(RF2.FULL, SHORT_NAME, EFFECTIVE_TIME));
		assertTrue(recognise(fullFileName, RF2.FULL));
		assertEquals("sct2_Concept_Full_MVN_20250101.txt", fullFileName);

		// Snapshot
		String snapshotFileName = isolateFileName(FileNameService.getConcept(RF2.SNAPSHOT, SHORT_NAME, EFFECTIVE_TIME));
		assertTrue(recognise(snapshotFileName, RF2.SNAPSHOT));
		assertEquals("sct2_Concept_Snapshot_MVN_20250101.txt", snapshotFileName);
	}

	@Test
	void fileNames_ShouldBeRecognised_WhenGivenDescription() {
		// Full
		String fullFileName = isolateFileName(FileNameService.getDescription(RF2.FULL, LANGUAGE_CODE, SHORT_NAME, EFFECTIVE_TIME));
		assertTrue(recognise(fullFileName, RF2.FULL));
		assertEquals("sct2_Description_Full-en_MVN_20250101.txt", fullFileName);

		// Snapshot
		String snapshotFileName = isolateFileName(FileNameService.getDescription(RF2.SNAPSHOT, LANGUAGE_CODE, SHORT_NAME, EFFECTIVE_TIME));
		assertTrue(recognise(snapshotFileName, RF2.SNAPSHOT));
		assertEquals("sct2_Description_Snapshot-en_MVN_20250101.txt", snapshotFileName);
	}

	@Test
	void fileNames_ShouldBeRecognised_WhenGivenIdentifier() {
		// Full
		String fullFileName = isolateFileName(FileNameService.getIdentifier(RF2.FULL, SHORT_NAME, EFFECTIVE_TIME));
		assertTrue(recognise(fullFileName, RF2.FULL));
		assertEquals("sct2_Identifier_Full_MVN_20250101.txt", fullFileName);

		// Snapshot
		String snapshotFileName = isolateFileName(FileNameService.getIdentifier(RF2.SNAPSHOT, SHORT_NAME, EFFECTIVE_TIME));
		assertTrue(recognise(snapshotFileName, RF2.SNAPSHOT));
		assertEquals("sct2_Identifier_Snapshot_MVN_20250101.txt", snapshotFileName);
	}

	@Test
	void fileNames_ShouldBeRecognised_WhenGivenRelationship() {
		// Full
		String fullFileName = isolateFileName(FileNameService.getRelationship(RF2.FULL, SHORT_NAME, EFFECTIVE_TIME));
		assertTrue(recognise(fullFileName, RF2.FULL));
		assertEquals("sct2_Relationship_Full_MVN_20250101.txt", fullFileName);

		// Snapshot
		String snapshotFileName = isolateFileName(FileNameService.getRelationship(RF2.SNAPSHOT, SHORT_NAME, EFFECTIVE_TIME));
		assertTrue(recognise(snapshotFileName, RF2.SNAPSHOT));
		assertEquals("sct2_Relationship_Snapshot_MVN_20250101.txt", snapshotFileName);
	}

	@Test
	void fileNames_ShouldBeRecognised_WhenGivenConcreteRelationship() {
		// Full
		String fullFileName = isolateFileName(FileNameService.getConcreteRelationship(RF2.FULL, SHORT_NAME, EFFECTIVE_TIME));
		assertTrue(recognise(fullFileName, RF2.FULL));
		assertEquals("sct2_RelationshipConcreteValues_Full_MVN_20250101.txt", fullFileName);

		// Snapshot
		String snapshotFileName = isolateFileName(FileNameService.getConcreteRelationship(RF2.SNAPSHOT, SHORT_NAME, EFFECTIVE_TIME));
		assertTrue(recognise(snapshotFileName, RF2.SNAPSHOT));
		assertEquals("sct2_RelationshipConcreteValues_Snapshot_MVN_20250101.txt", snapshotFileName);
	}

	@Test
	void fileNames_ShouldBeRecognised_WhenGivenAxiom() {
		// Full
		String fullFileName = isolateFileName(FileNameService.getAxiom(RF2.FULL, SHORT_NAME, EFFECTIVE_TIME));
		assertTrue(recognise(fullFileName, RF2.FULL));
		assertEquals("sct2_sRefset_OWLExpressionFull_MVN_20250101.txt", fullFileName);

		// Snapshot
		String snapshotFileName = isolateFileName(FileNameService.getAxiom(RF2.SNAPSHOT, SHORT_NAME, EFFECTIVE_TIME));
		assertTrue(recognise(snapshotFileName, RF2.SNAPSHOT));
		assertEquals("sct2_sRefset_OWLExpressionSnapshot_MVN_20250101.txt", snapshotFileName);
	}

	@Test
	void fileNames_ShouldBeRecognised_WhenGivenStatedRelationship() {
		// Full
		String fullFileName = isolateFileName(FileNameService.getStatedRelationship(RF2.FULL, SHORT_NAME, EFFECTIVE_TIME));
		assertTrue(recognise(fullFileName, RF2.FULL));
		assertEquals("sct2_StatedRelationship_Full_MVN_20250101.txt", fullFileName);

		// Snapshot
		String snapshotFileName = isolateFileName(FileNameService.getStatedRelationship(RF2.SNAPSHOT, SHORT_NAME, EFFECTIVE_TIME));
		assertTrue(recognise(snapshotFileName, RF2.SNAPSHOT));
		assertEquals("sct2_StatedRelationship_Snapshot_MVN_20250101.txt", snapshotFileName);
	}

	@Test
	void fileNames_ShouldBeRecognised_WhenGivenTextDefinitions() {
		// Full
		String fullFileName = isolateFileName(FileNameService.getTextDefinition(RF2.FULL, LANGUAGE_CODE, SHORT_NAME, EFFECTIVE_TIME));
		assertTrue(recognise(fullFileName, RF2.FULL));
		assertEquals("sct2_TextDefinition_Full-en_MVN_20250101.txt", fullFileName);

		// Snapshot
		String snapshotFileName = isolateFileName(FileNameService.getTextDefinition(RF2.SNAPSHOT, LANGUAGE_CODE, SHORT_NAME, EFFECTIVE_TIME));
		assertTrue(recognise(snapshotFileName, RF2.SNAPSHOT));
		assertEquals("sct2_TextDefinition_Snapshot-en_MVN_20250101.txt", snapshotFileName);
	}

	@Test
	void fileNames_ShouldBeRecognised_WhenGivenReferenceSets() {
		// Cache file name
		cacheFileName("LANGUAGE", "Refset/Language/der2_cRefset_Language-nl");
		cacheFileName("LANGUAGES", "Refset/Language/der2_cRefset_Language-fr-be-gp");
		cacheFileName("ASSOCIATION", "Refset/Content/der2_cRefset_Association");

		// Language
		String fullFileName = isolateFileName(fileNameService.getReferenceSet(RF2.FULL, SHORT_NAME, EFFECTIVE_TIME, "LANGUAGE"));
		assertEquals("der2_cRefset_LanguageFull-nl_MVN_20250101.txt", fullFileName);
		String snapshotFileName = isolateFileName(fileNameService.getReferenceSet(RF2.SNAPSHOT, SHORT_NAME, EFFECTIVE_TIME, "LANGUAGE"));
		assertEquals("der2_cRefset_LanguageSnapshot-nl_MVN_20250101.txt", snapshotFileName);

		// Languages
		fullFileName = isolateFileName(fileNameService.getReferenceSet(RF2.FULL, SHORT_NAME, EFFECTIVE_TIME, "LANGUAGES"));
		assertEquals("der2_cRefset_LanguageFull-fr-be-gp_MVN_20250101.txt", fullFileName);
		snapshotFileName = isolateFileName(fileNameService.getReferenceSet(RF2.SNAPSHOT, SHORT_NAME, EFFECTIVE_TIME, "LANGUAGES"));
		assertEquals("der2_cRefset_LanguageSnapshot-fr-be-gp_MVN_20250101.txt", snapshotFileName);

		// Association
		fullFileName = isolateFileName(fileNameService.getReferenceSet(RF2.FULL, SHORT_NAME, EFFECTIVE_TIME, "ASSOCIATION"));
		assertEquals("der2_cRefset_AssociationFull_MVN_20250101.txt", fullFileName);
		snapshotFileName = isolateFileName(fileNameService.getReferenceSet(RF2.SNAPSHOT, SHORT_NAME, EFFECTIVE_TIME, "ASSOCIATION"));
		assertEquals("der2_cRefset_AssociationSnapshot_MVN_20250101.txt", snapshotFileName);
	}

	@Test
	void removeDerivativePrefix_ShouldDoExpected_WhenGivenSeveral() {
		// given
		List<String> input = List.of(
				"fileName",
				"Snapshot/Refset/Map/der2_sRefset_XXXXSimpleMapSnapshot_XX_20250930",
				"Snapshot/Refset/Map/der2_sRefset_XXXXSimpleMapSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_scsRefset_XXXXComponentAnnotationStringValueSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_scsRefset_XXXXComponentAnnotationStringValueSnapshot_XX_20250930",
				"Snapshot/Terminology/sct2_StatedRelationship_XXXXSnapshot_XX_20250930",
				"Snapshot/Terminology/sct2_StatedRelationship_XXXXSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_cissccRefset_XXXXMRCMAttributeDomainSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_cissccRefset_XXXXMRCMAttributeDomainSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_sssssssRefset_XXXXMRCMDomainSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_sssssssRefset_XXXXMRCMDomainSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_ssccRefset_XXXXMRCMAttributeRangeSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_ssccRefset_XXXXMRCMAttributeRangeSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_sscsRefset_MemberAnnotationStringValueSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_sscsRefset_MemberAnnotationStringValueSnapshot_XX_20250930",
				"Snapshot/Terminology/sct2_TextDefinition_XXXXSnapshot-en_XX_20250930",
				"Snapshot/Terminology/sct2_TextDefinition_XXXXSnapshot-en_XX_20250930",
				"Snapshot/Terminology/sct2_Identifier_XXXXSnapshot_XX_20250930",
				"Snapshot/Terminology/sct2_Identifier_XXXXSnapshot_XX_20250930",
				"Snapshot/Terminology/sct2_RelationshipConcreteValues_XXXXSnapshot_XX_20250930",
				"Snapshot/Terminology/sct2_RelationshipConcreteValues_XXXXSnapshot_XX_20250930",
				"Snapshot/Refset/Content/der2_cRefset_XXXXAssociationReferenceSnapshot_XX_20250930",
				"Snapshot/Refset/Content/der2_cRefset_XXXXAssociationReferenceSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_sscsRefset_XXXXMemberAnnotationStringValueSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_sscsRefset_XXXXMemberAnnotationStringValueSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_cRefset_XXXXMRCMModuleScopeSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_cRefset_XXXXMRCMModuleScopeSnapshot_XX_20250930",
				"Snapshot/Terminology/sct2_Identifier_Snapshot_XX_20250930",
				"Snapshot/Terminology/sct2_Identifier_Snapshot_XX_20250930",
				"Snapshot/Refset/Content/der2_cRefset_XXXXAttributeValueSnapshot_XX_20250930",
				"Snapshot/Refset/Content/der2_cRefset_XXXXAttributeValueSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_ciRefset_XXXXDescriptionTypeSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_ciRefset_XXXXDescriptionTypeSnapshot_XX_20250930"
		);

		List<String> output = List.of(
				"fileName",
				"Snapshot/Refset/Map/der2_sRefset_SimpleMapSnapshot_XX_20250930",
				"Snapshot/Refset/Map/der2_sRefset_SimpleMapSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_scsRefset_ComponentAnnotationStringValueSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_scsRefset_ComponentAnnotationStringValueSnapshot_XX_20250930",
				"Snapshot/Terminology/sct2_StatedRelationship_Snapshot_XX_20250930",
				"Snapshot/Terminology/sct2_StatedRelationship_Snapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_cissccRefset_MRCMAttributeDomainSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_cissccRefset_MRCMAttributeDomainSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_sssssssRefset_MRCMDomainSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_sssssssRefset_MRCMDomainSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_ssccRefset_MRCMAttributeRangeSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_ssccRefset_MRCMAttributeRangeSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_sscsRefset_MemberAnnotationStringValueSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_sscsRefset_MemberAnnotationStringValueSnapshot_XX_20250930",
				"Snapshot/Terminology/sct2_TextDefinition_Snapshot-en_XX_20250930",
				"Snapshot/Terminology/sct2_TextDefinition_Snapshot-en_XX_20250930",
				"Snapshot/Terminology/sct2_Identifier_Snapshot_XX_20250930",
				"Snapshot/Terminology/sct2_Identifier_Snapshot_XX_20250930",
				"Snapshot/Terminology/sct2_RelationshipConcreteValues_Snapshot_XX_20250930",
				"Snapshot/Terminology/sct2_RelationshipConcreteValues_Snapshot_XX_20250930",
				"Snapshot/Refset/Content/der2_cRefset_AssociationReferenceSnapshot_XX_20250930",
				"Snapshot/Refset/Content/der2_cRefset_AssociationReferenceSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_sscsRefset_MemberAnnotationStringValueSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_sscsRefset_MemberAnnotationStringValueSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_cRefset_MRCMModuleScopeSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_cRefset_MRCMModuleScopeSnapshot_XX_20250930",
				"Snapshot/Terminology/sct2_Identifier_Snapshot_XX_20250930",
				"Snapshot/Terminology/sct2_Identifier_Snapshot_XX_20250930",
				"Snapshot/Refset/Content/der2_cRefset_AttributeValueSnapshot_XX_20250930",
				"Snapshot/Refset/Content/der2_cRefset_AttributeValueSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_ciRefset_DescriptionTypeSnapshot_XX_20250930",
				"Snapshot/Refset/Metadata/der2_ciRefset_DescriptionTypeSnapshot_XX_20250930"
		);

		// when
		for (int i = 0; i < input.size(); i++) {
			// then
			assertEquals(output.get(i), FileNameService.removeDerivativePrefix(input.get(i)));
		}
	}

	// Adapted from Snomed Boot
	// https://github.com/IHTSDO/snomed-boot/blob/8e31003bf21c97f8d2bea6ef72a417bfc01e4216/src/main/java/org/ihtsdo/otf/snomedboot/ReleaseImporter.java#L440
	private boolean recognise(String fileName, String fileType) {
		boolean concept = fileName.matches("x?(sct|rel)2_Concept_[^_]*" + fileType + "_.*");
		boolean description = fileName.matches("x?(sct|rel)2_Description_[^_]*" + fileType + "(-[a-zA-Z\\-]*)?_.*");
		boolean textDefinition = fileName.matches("x?(sct|rel)2_TextDefinition_[^_]*" + fileType + "(-[a-zA-Z\\-]*)?_.*");
		boolean relationship = fileName.matches("x?(sct|rel)2_Relationship_[^_]*" + fileType + "_.*");
		boolean concreteRelationship = fileName.matches("x?(sct|rel)2_RelationshipConcreteValues_[^_]*" + fileType + "_.*");
		boolean statedRelationship = fileName.matches("x?(sct|rel)2_StatedRelationship_[^_]*" + fileType + "_.*");
		boolean identifier = fileName.matches("x?(sct|rel)2_Identifier_[^_]*" + fileType + "_.*");
		boolean axiom = fileName.matches("x?(sct|rel)2_sRefset_.*OWL.*[^_]*" + fileType + "_.*");
		boolean refset = fileName.matches("x?(der|rel)2_[sci]*Refset_[^_]*" + fileType + "(-[a-zA-Z\\-]*)?_.*");

		return concept || description || textDefinition || relationship || concreteRelationship || statedRelationship || identifier || axiom || refset;
	}

	private String isolateFileName(String input) {
		String[] split = input.split("/");
		return split[split.length - 1];
	}
}