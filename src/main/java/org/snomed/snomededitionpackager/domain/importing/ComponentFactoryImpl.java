package org.snomed.snomededitionpackager.domain.importing;

import org.ihtsdo.otf.snomedboot.ReleaseImportException;
import org.ihtsdo.otf.snomedboot.factory.HistoryAwareComponentFactory;
import org.ihtsdo.otf.snomedboot.factory.LoadingProfile;
import org.snomed.snomededitionpackager.domain.datastore.DataStore;
import org.snomed.snomededitionpackager.domain.rf2.*;

public class ComponentFactoryImpl implements HistoryAwareComponentFactory {
	private final DataStore componentStore;

	public ComponentFactoryImpl(DataStore componentStore) {
		this.componentStore = componentStore;
	}

	@Override
	public LoadingProfile getLoadingProfile() {
		return null;
	}

	@Override
	public void preprocessingContent() {
		// nothing to do
	}

	@Override
	public void loadingComponentsStarting() {
		// nothing to do
	}

	@Override
	public void loadingComponentsCompleted() throws ReleaseImportException {
		// nothing to do
	}

	@Override
	public void newConceptState(String conceptId, String effectiveTime, String active, String moduleId, String definitionStatusId) {
		componentStore.createConcept(new Concept().setConceptId(conceptId).setEffectiveTime(effectiveTime).setActive(active).setModuleId(moduleId).setDefinitionStatusId(definitionStatusId));
	}

	@Override
	public void newDescriptionState(String id, String effectiveTime, String active, String moduleId, String conceptId, String languageCode, String typeId, String term, String caseSignificanceId) {
		Description description = new Description().setDescriptionId(id).setEffectiveTime(effectiveTime).setActive(active).setModuleId(moduleId).setConceptId(conceptId).setLanguageCode(languageCode).setTypeId(typeId).setTerm(term).setCaseSignificanceId(caseSignificanceId);
		if (RF2.TEXT_DEFINITION.equals(description.getTypeId())) {
			componentStore.createTextDefinition(description);
		} else {
			componentStore.createDescription(description);
		}
	}

	@Override
	public void newRelationshipState(String id, String effectiveTime, String active, String moduleId, String sourceId, String destinationId, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {
		Relationship relationship = new Relationship().setRelationshipId(id).setEffectiveTime(effectiveTime).setActive(active).setModuleId(moduleId).setSourceId(sourceId).setDestinationId(destinationId).setRelationshipGroup(relationshipGroup).setTypeId(typeId).setCharacteristicTypeId(characteristicTypeId).setModifierId(modifierId);
		if (RF2.STATED_RELATIONSHIP.equals(characteristicTypeId)) {
			componentStore.createStatedRelationship(relationship);
		} else {
			componentStore.createRelationship(relationship);
		}
	}

	@Override
	public void newConcreteRelationshipState(String id, String effectiveTime, String active, String moduleId, String sourceId, String value, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {
		componentStore.createConcreteRelationship(new ConcreteRelationship().setRelationshipId(id).setEffectiveTime(effectiveTime).setActive(active).setModuleId(moduleId).setSourceId(sourceId).setValue(value).setRelationshipGroup(relationshipGroup).setTypeId(typeId).setCharacteristicTypeId(characteristicTypeId).setModifierId(modifierId));
	}

	@Override
	public void newReferenceSetMemberState(String[] fieldNames, String id, String effectiveTime, String active, String moduleId, String refsetId, String referencedComponentId, String... otherValues) {
		ReferenceSetMember referenceSetMember = new ReferenceSetMember().setFieldNames(fieldNames).setId(id).setEffectiveTime(effectiveTime).setActive(active).setModuleId(moduleId).setRefsetId(refsetId).setReferencedComponentId(referencedComponentId).setOtherValues(otherValues);
		if (RF2.REFSET_OWL_AXIOM.equals(refsetId) || RF2.REFSET_OWL_ONTOLOGY.equals(refsetId)) {
			componentStore.createAxiom(referenceSetMember);
		} else {
			componentStore.createReferenceSetMember(referenceSetMember);
		}
	}

	@Override
	public void newIdentifierState(String alternateIdentifier, String effectiveTime, String active, String moduleId, String identifierSchemeId, String referencedComponentId) {
		componentStore.createIdentifier(new Identifier().setAlternateIdentifier(alternateIdentifier).setEffectiveTime(effectiveTime).setActive(active).setModuleId(moduleId).setIdentifierSchemeId(identifierSchemeId).setReferencedComponentId(referencedComponentId));
	}

	@Override
	public void loadingReleaseDeltaStarting(String releaseVersion) {
		// nothing to do
	}

	@Override
	public void loadingReleaseDeltaFinished(String releaseVersion) {
		// nothing to do
	}
}