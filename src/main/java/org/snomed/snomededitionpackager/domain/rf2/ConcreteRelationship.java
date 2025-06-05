package org.snomed.snomededitionpackager.domain.rf2;

public class ConcreteRelationship {
	private String relationshipId;
	private String effectiveTime;
	private String active;
	private String moduleId;
	private String sourceId;
	private String value;
	private String relationshipGroup;
	private String typeId;
	private String characteristicTypeId;
	private String modifierId;

	public String getRelationshipId() {
		return relationshipId;
	}

	public ConcreteRelationship setRelationshipId(String relationshipId) {
		this.relationshipId = relationshipId;
		return this;
	}

	public String getEffectiveTime() {
		return effectiveTime;
	}

	public ConcreteRelationship setEffectiveTime(String effectiveTime) {
		this.effectiveTime = effectiveTime;
		return this;
	}

	public String getActive() {
		return active;
	}

	public ConcreteRelationship setActive(String active) {
		this.active = active;
		return this;
	}

	public String getModuleId() {
		return moduleId;
	}

	public ConcreteRelationship setModuleId(String moduleId) {
		this.moduleId = moduleId;
		return this;
	}

	public String getSourceId() {
		return sourceId;
	}

	public ConcreteRelationship setSourceId(String sourceId) {
		this.sourceId = sourceId;
		return this;
	}

	public String getValue() {
		return value;
	}

	public ConcreteRelationship setValue(String value) {
		this.value = value;
		return this;
	}

	public String getRelationshipGroup() {
		return relationshipGroup;
	}

	public ConcreteRelationship setRelationshipGroup(String relationshipGroup) {
		this.relationshipGroup = relationshipGroup;
		return this;
	}

	public String getTypeId() {
		return typeId;
	}

	public ConcreteRelationship setTypeId(String typeId) {
		this.typeId = typeId;
		return this;
	}

	public String getCharacteristicTypeId() {
		return characteristicTypeId;
	}

	public ConcreteRelationship setCharacteristicTypeId(String characteristicTypeId) {
		this.characteristicTypeId = characteristicTypeId;
		return this;
	}

	public String getModifierId() {
		return modifierId;
	}

	public ConcreteRelationship setModifierId(String modifierId) {
		this.modifierId = modifierId;
		return this;
	}

	public String toRF2() {
		return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s", relationshipId, effectiveTime, active, moduleId, sourceId, value, relationshipGroup, typeId, characteristicTypeId, modifierId);
	}
}