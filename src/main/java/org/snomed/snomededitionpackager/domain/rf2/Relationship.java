package org.snomed.snomededitionpackager.domain.rf2;

public class Relationship {
	private String relationshipId;
	private String effectiveTime;
	private String active;
	private String moduleId;
	private String sourceId;
	private String destinationId;
	private String relationshipGroup;
	private String typeId;
	private String characteristicTypeId;
	private String modifierId;

	public String getRelationshipId() {
		return relationshipId;
	}

	public Relationship setRelationshipId(String relationshipId) {
		this.relationshipId = relationshipId;
		return this;
	}

	public String getEffectiveTime() {
		return effectiveTime;
	}

	public Relationship setEffectiveTime(String effectiveTime) {
		this.effectiveTime = effectiveTime;
		return this;
	}

	public String getActive() {
		return active;
	}

	public Relationship setActive(String active) {
		this.active = active;
		return this;
	}

	public String getModuleId() {
		return moduleId;
	}

	public Relationship setModuleId(String moduleId) {
		this.moduleId = moduleId;
		return this;
	}

	public String getSourceId() {
		return sourceId;
	}

	public Relationship setSourceId(String sourceId) {
		this.sourceId = sourceId;
		return this;
	}

	public String getDestinationId() {
		return destinationId;
	}

	public Relationship setDestinationId(String destinationId) {
		this.destinationId = destinationId;
		return this;
	}

	public String getRelationshipGroup() {
		return relationshipGroup;
	}

	public Relationship setRelationshipGroup(String relationshipGroup) {
		this.relationshipGroup = relationshipGroup;
		return this;
	}

	public String getTypeId() {
		return typeId;
	}

	public Relationship setTypeId(String typeId) {
		this.typeId = typeId;
		return this;
	}

	public String getCharacteristicTypeId() {
		return characteristicTypeId;
	}

	public Relationship setCharacteristicTypeId(String characteristicTypeId) {
		this.characteristicTypeId = characteristicTypeId;
		return this;
	}

	public String getModifierId() {
		return modifierId;
	}

	public Relationship setModifierId(String modifierId) {
		this.modifierId = modifierId;
		return this;
	}

	public String toRF2() {
		return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s", relationshipId, effectiveTime, active, moduleId, sourceId, destinationId, relationshipGroup, typeId, characteristicTypeId, modifierId);
	}
}