package org.snomed.snomededitionpackager.domain.datastore;

import org.ihtsdo.otf.snomedboot.factory.implementation.standard.ComponentStore;
import org.ihtsdo.otf.snomedboot.factory.implementation.standard.ConceptImpl;
import org.snomed.snomededitionpackager.domain.rf2.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Data store populated by Snomed Boot.
 */
public class DataStore extends ComponentStore {
	private final Map<String, Set<Concept>> concepts = new HashMap<>();
	private final Map<String, Set<Description>> descriptions = new HashMap<>();
	private final Set<String> descriptionLanguageCodes = new HashSet<>();
	private final Map<String, Set<Description>> textDefinitions = new HashMap<>();
	private final Set<String> textDefinitionLanguageCodes = new HashSet<>();
	private final Map<String, Set<Relationship>> relationships = new HashMap<>();
	private final Map<String, Set<Relationship>> statedRelationships = new HashMap<>();
	private final Map<String, Set<ConcreteRelationship>> concreteRelationships = new HashMap<>();
	private final Map<String, Set<ReferenceSetMember>> axioms = new HashMap<>();
	private final Map<String, Set<ReferenceSetMember>> referenceSetMembers = new HashMap<>();
	private final Map<String, Set<Identifier>> identifiers = new HashMap<>();
	private final Map<String, String> fileNameByRefsetId = new HashMap<>();
	private final Map<String, String> headersByRefsetId = new HashMap<>();
	private final Map<String, String> emptyFiles = new HashMap<>();
	private final List<ReleasePackageInformation> releasePackageInformations = new ArrayList<>();

	/**
	 * Add Concept to store.
	 *
	 * @param concept Concept to add to store.
	 */
	public void createConcept(Concept concept) {
		if (concept == null) {
			return;
		}

		Set<Concept> value = this.concepts.get(concept.getConceptId());
		if (value == null) {
			// Sorted by newest effectiveTime first
			value = new TreeSet<>((o1, o2) -> o2.getEffectiveTime().compareTo(o1.getEffectiveTime()));
		}

		value.add(concept);
		this.concepts.put(concept.getConceptId(), value);
	}

	/**
	 * Return Concepts from store.
	 *
	 * @return Concepts from store.
	 */
	public Map<String, Set<Concept>> readConcepts(boolean sort) {
		return sort ? sortLong(concepts) : concepts;
	}

	/**
	 * Add Description to store. Note, TextDefinitions are dealt with separately.
	 *
	 * @param description Description to add to store.
	 */
	public void createDescription(Description description) {
		if (description == null) {
			return;
		}

		createDescriptionLanguageCode(description);
		Set<Description> value = this.descriptions.get(description.getDescriptionId());
		if (value == null) {
			// Sorted by newest effectiveTime first
			value = new TreeSet<>((o1, o2) -> o2.getEffectiveTime().compareTo(o1.getEffectiveTime()));
		}

		value.add(description);
		this.descriptions.put(description.getDescriptionId(), value);
	}

	/**
	 * Return Descriptions from store.
	 *
	 * @return Descriptions from store.
	 */
	public Map<String, Set<Description>> readDescriptions(boolean sort) {
		return sort ? sortLong(descriptions) : descriptions;
	}

	/**
	 * Return language codes for stored Descriptions.
	 *
	 * @return Language codes for stored Descriptions.
	 */
	public Set<String> readDescriptionLanguageCodes() {
		return descriptionLanguageCodes;
	}

	/**
	 * Add TextDefinition to store. Note, Descriptions are dealt with separately.
	 *
	 * @param textDefinition TextDefinition to add to store.
	 */
	public void createTextDefinition(Description textDefinition) {
		if (textDefinition == null) {
			return;
		}

		createTextDefinitionLanguageCode(textDefinition);
		Set<Description> value = this.textDefinitions.get(textDefinition.getDescriptionId());
		if (value == null) {
			// Sorted by newest effectiveTime first
			value = new TreeSet<>((o1, o2) -> o2.getEffectiveTime().compareTo(o1.getEffectiveTime()));
		}

		value.add(textDefinition);
		this.textDefinitions.put(textDefinition.getDescriptionId(), value);
	}

	/**
	 * Return TextDefinitions from store.
	 *
	 * @return TextDefinitions from store.
	 */
	public Map<String, Set<Description>> readTextDefinitions(boolean sort) {
		return sort ? sortLong(textDefinitions) : textDefinitions;
	}

	/**
	 * Return language codes for stored TextDefinitions.
	 *
	 * @return Language codes for stored TextDefinitions.
	 */
	public Set<String> readTextDefinitionLanguageCodes() {
		return textDefinitionLanguageCodes;
	}

	/**
	 * Add Relationship to store. Note, stated Relationships are dealt with separately.
	 *
	 * @param relationship Relationship to add to store.
	 */
	public void createRelationship(Relationship relationship) {
		if (relationship == null) {
			return;
		}

		Set<Relationship> value = this.relationships.get(relationship.getRelationshipId());
		if (value == null) {
			// Sorted by newest effectiveTime first
			value = new TreeSet<>((o1, o2) -> o2.getEffectiveTime().compareTo(o1.getEffectiveTime()));
		}

		value.add(relationship);
		this.relationships.put(relationship.getRelationshipId(), value);
	}

	/**
	 * Return Relationships from store.
	 *
	 * @return Relationships from store.
	 */
	public Map<String, Set<Relationship>> readRelationships(boolean sort) {
		return sort ? sortLong(relationships) : relationships;
	}

	/**
	 * Add stated Relationship to store. Note, stated Relationships are dealt with separately.
	 *
	 * @param statedRelationship Relationship to add to store.
	 */
	public void createStatedRelationship(Relationship statedRelationship) {
		if (statedRelationship == null) {
			return;
		}

		Set<Relationship> value = this.statedRelationships.get(statedRelationship.getRelationshipId());
		if (value == null) {
			// Sorted by newest effectiveTime first
			value = new TreeSet<>((o1, o2) -> o2.getEffectiveTime().compareTo(o1.getEffectiveTime()));
		}

		value.add(statedRelationship);
		this.statedRelationships.put(statedRelationship.getRelationshipId(), value);
	}

	/**
	 * Return stated Relationships from store.
	 *
	 * @return Stated Relationships from store.
	 */
	public Map<String, Set<Relationship>> readStatedRelationships(boolean sort) {
		return sort ? sortLong(statedRelationships) : statedRelationships;
	}

	/**
	 * Add concrete Relationship to store.
	 *
	 * @param concreteRelationship Concrete Relationship to add to store.
	 */
	public void createConcreteRelationship(ConcreteRelationship concreteRelationship) {
		if (concreteRelationship == null) {
			return;
		}

		Set<ConcreteRelationship> value = this.concreteRelationships.get(concreteRelationship.getRelationshipId());
		if (value == null) {
			// Sorted by newest effectiveTime first
			value = new TreeSet<>((o1, o2) -> o2.getEffectiveTime().compareTo(o1.getEffectiveTime()));
		}

		value.add(concreteRelationship);
		this.concreteRelationships.put(concreteRelationship.getRelationshipId(), value);
	}

	/**
	 * Return concrete Relationships from store.
	 *
	 * @return Concrete Relationships from store.
	 */
	public Map<String, Set<ConcreteRelationship>> readConcreteRelationships(boolean sort) {
		return sort ? sortLong(concreteRelationships) : concreteRelationships;
	}

	/**
	 * Add ReferenceSetMember to store. Note, Axioms are dealt with separately.
	 *
	 * @param referenceSetMember ReferenceSetMember to add to store.
	 */
	public void createReferenceSetMember(ReferenceSetMember referenceSetMember) {
		if (referenceSetMember == null) {
			return;
		}

		Set<ReferenceSetMember> value = this.referenceSetMembers.get(referenceSetMember.getId());
		if (value == null) {
			// Sorted by newest effectiveTime first
			value = new TreeSet<>((o1, o2) -> o2.getEffectiveTime().compareTo(o1.getEffectiveTime()));
		}

		value.add(referenceSetMember);
		this.referenceSetMembers.put(referenceSetMember.getId(), value);
	}

	/**
	 * Return ReferenceSetMembers from store.
	 *
	 * @return ReferenceSetMembers from store.
	 */
	public Map<String, Set<ReferenceSetMember>> readReferenceSetMembers() {
		return referenceSetMembers;
	}

	/**
	 * Add Axiom to store. Note, non-Axiom ReferenceSetMembers are dealt with separately.
	 *
	 * @param axiom Axiom to add to store.
	 */
	public void createAxiom(ReferenceSetMember axiom) {
		if (axiom == null) {
			return;
		}

		Set<ReferenceSetMember> value = this.axioms.get(axiom.getId());
		if (value == null) {
			// Sorted by newest effectiveTime first
			value = new TreeSet<>((o1, o2) -> o2.getEffectiveTime().compareTo(o1.getEffectiveTime()));
		}

		value.add(axiom);
		this.axioms.put(axiom.getId(), value);
	}

	/**
	 * Return Axioms from store.
	 *
	 * @return Axioms from store.
	 */
	public Map<String, Set<ReferenceSetMember>> readAxioms(boolean sort) {
		return sort ? sortUUID(axioms) : axioms;
	}

	/**
	 * Add alternate Identifier to store.
	 *
	 * @param identifier Alternate Identifier to add to store.
	 */
	public void createIdentifier(Identifier identifier) {
		if (identifier == null) {
			return;
		}

		Set<Identifier> value = this.identifiers.get(identifier.getAlternateIdentifier());
		if (value == null) {
			// Sorted by newest effectiveTime first
			value = new TreeSet<>((o1, o2) -> o2.getEffectiveTime().compareTo(o1.getEffectiveTime()));
		}

		value.add(identifier);
		this.identifiers.put(identifier.getAlternateIdentifier(), value);
	}

	/**
	 * Return alternate Identifiers from store.
	 *
	 * @return Alternate Identifiers from store.
	 */
	public Map<String, Set<Identifier>> readIdentifiers(boolean sort) {
		return sort ? sortLong(identifiers) : identifiers;
	}

	/**
	 * Add file names of ReferenceSets to store.
	 *
	 * @param referenceSetFileNames File names of ReferenceSets to add to store.
	 */
	public void cacheFileName(Map<String, String> referenceSetFileNames) {
		if (referenceSetFileNames == null || referenceSetFileNames.isEmpty()) {
			return;
		}

		this.fileNameByRefsetId.putAll(referenceSetFileNames);
	}

	/**
	 * Add file headers of ReferenceSets to store.
	 *
	 * @param headers Headers of ReferenceSets to add to store.
	 */
	public void cacheHeader(Map<String, String> headers) {
		if (headers == null || headers.isEmpty()) {
			return;
		}

		this.headersByRefsetId.putAll(headers);
	}

	/**
	 * Add empty files to store.
	 *
	 * @param emptyFiles Empty files to add to store.
	 */
	public void cacheEmptyFiles(Map<String, String> emptyFiles) {
		if (emptyFiles == null || emptyFiles.isEmpty()) {
			return;
		}

		this.emptyFiles.putAll(emptyFiles);
	}

	/**
	 * Return empty files from store.
	 *
	 * @return Empty files from store.
	 */
	public Map<String, String> readEmptyFiles() {
		return this.emptyFiles;
	}

	/**
	 * Add json file to store.
	 *
	 * @param releasePackageInformation Converted json file to add to store.
	 */
	public void cacheReleasePackageInformation(ReleasePackageInformation releasePackageInformation) {
		if (releasePackageInformation == null) {
			return;
		}

		this.releasePackageInformations.add(releasePackageInformation);
	}

	/**
	 * Return file name of ReferenceSet from store.
	 *
	 * @param refsetId The refsetId to identify the ReferenceSet.
	 * @return File name of ReferenceSet from store.
	 */
	public String getFileName(String refsetId) {
		if (fileNameByRefsetId.isEmpty()) {
			return null;
		}

		return fileNameByRefsetId.get(refsetId);
	}

	/**
	 * Return file header of ReferenceSet from store.
	 *
	 * @param refsetId The refsetId to identify the ReferenceSet.
	 * @return File header of ReferenceSet from store.
	 */
	public String getHeader(String refsetId) {
		if (this.headersByRefsetId.isEmpty()) {
			return null;
		}

		return headersByRefsetId.get(refsetId);
	}

	public List<ReleasePackageInformation> getReleasePackageInformations() {
		return releasePackageInformations;
	}

	/**
	 * Remove all Components from store.
	 */
	public void clear() {
		this.concepts.clear();
		this.descriptions.clear();
		this.textDefinitions.clear();
		this.relationships.clear();
		this.statedRelationships.clear();
		this.concreteRelationships.clear();
		this.axioms.clear();
		this.referenceSetMembers.clear();
		this.identifiers.clear();
		this.fileNameByRefsetId.clear();
		this.headersByRefsetId.clear();
		this.releasePackageInformations.clear();
		this.emptyFiles.clear();
	}

	@Override
	public Map<Long, ConceptImpl> getConcepts() {
		throw new UnsupportedOperationException("Not implemented");
	}

	// Store data separately rather than re-processing.
	private void createDescriptionLanguageCode(Description description) {
		this.descriptionLanguageCodes.add(description.getLanguageCode());
	}

	// Store data separately rather than re-processing.
	private void createTextDefinitionLanguageCode(Description description) {
		this.textDefinitionLanguageCodes.add(description.getLanguageCode());
	}

	// Sort long identifiers
	private <T> Map<String, Set<T>> sortLong(Map<String, Set<T>> input) {
		return input.entrySet().stream().sorted(Comparator.comparingLong(e -> Long.parseLong(e.getKey()))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
	}

	// Sort uuid identifiers
	private Map<String, Set<ReferenceSetMember>> sortUUID(Map<String, Set<ReferenceSetMember>> input) {
		return input.entrySet().stream().sorted(Comparator.comparing(entry -> UUID.fromString(entry.getValue().iterator().next().getId()))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, newVal) -> oldVal, LinkedHashMap::new));
	}
}
