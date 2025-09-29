package org.snomed.snomededitionpackager.domain.rf2;

public class RF2 {
	private RF2() {

	}

	// Files
	public static final String LINE_ENDING = "\r\n";

	// Type
	public static final String FULL = "Full";
	public static final String SNAPSHOT = "Snapshot";

	// Headers
	public static final String HEADER_CONCEPT = "id\teffectiveTime\tactive\tmoduleId\tdefinitionStatusId";
	public static final String HEADER_DESCRIPTION = "id\teffectiveTime\tactive\tmoduleId\tconceptId\tlanguageCode\ttypeId\tterm\tcaseSignificanceId";
	public static final String HEADER_TEXT_DEFINITION = "id\teffectiveTime\tactive\tmoduleId\tconceptId\tlanguageCode\ttypeId\tterm\tcaseSignificanceId";
	public static final String HEADER_IDENTIFIER = "alternateIdentifier\teffectiveTime\tactive\tmoduleId\tidentifierSchemeId\treferencedComponentId";
	public static final String HEADER_RELATIONSHIP = "id\teffectiveTime\tactive\tmoduleId\tsourceId\tdestinationId\trelationshipGroup\ttypeId\tcharacteristicTypeId\tmodifierId";
	public static final String HEADER_STATED_RELATIONSHIP = "id\teffectiveTime\tactive\tmoduleId\tsourceId\tdestinationId\trelationshipGroup\ttypeId\tcharacteristicTypeId\tmodifierId";
	public static final String HEADER_CONCRETE_RELATIONSHIP = "id\teffectiveTime\tactive\tmoduleId\tsourceId\tvalue\trelationshipGroup\ttypeId\tcharacteristicTypeId\tmodifierId";
	public static final String HEADER_OWL_AXIOM = "id\teffectiveTime\tactive\tmoduleId\trefsetId\treferencedComponentId\towlExpression";
	public static final String HEADER_REFSET_SIMPLE = "id\teffectiveTime\tactive\tmoduleId\trefsetId\treferencedComponentId";

	// SCT identifiers
	public static final String TEXT_DEFINITION = "900000000000550004";
	public static final String STATED_RELATIONSHIP = "900000000000010007";
	public static final String REFSET_OWL_AXIOM = "733073007";
	public static final String REFSET_OWL_ONTOLOGY = "762103008";
	public static final String REFSET_MODULE_DEPENDENCY = "900000000000534007";
}