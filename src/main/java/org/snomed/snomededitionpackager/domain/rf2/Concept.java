package org.snomed.snomededitionpackager.domain.rf2;

import java.util.Objects;

public class Concept {
	private String conceptId;
	private String effectiveTime;
	private String active;
	private String moduleId;
	private String definitionStatusId;

	public String getConceptId() {
		return conceptId;
	}

	public Concept setConceptId(String conceptId) {
		this.conceptId = conceptId;
		return this;
	}

	public String getEffectiveTime() {
		return effectiveTime;
	}

	public Concept setEffectiveTime(String effectiveTime) {
		this.effectiveTime = effectiveTime;
		return this;
	}

	public String getActive() {
		return active;
	}

	public Concept setActive(String active) {
		this.active = active;
		return this;
	}

	public String getModuleId() {
		return moduleId;
	}

	public Concept setModuleId(String moduleId) {
		this.moduleId = moduleId;
		return this;
	}

	public String getDefinitionStatusId() {
		return definitionStatusId;
	}

	public Concept setDefinitionStatusId(String definitionStatusId) {
		this.definitionStatusId = definitionStatusId;
		return this;
	}

	public String toRF2() {
		return String.format("%s\t%s\t%s\t%s\t%s", conceptId, effectiveTime, active, moduleId, definitionStatusId);
	}

	@Override
	public String toString() {
		return "Concept{" +
				"conceptId='" + conceptId + '\'' +
				", effectiveTime='" + effectiveTime + '\'' +
				", active='" + active + '\'' +
				", moduleId='" + moduleId + '\'' +
				", definitionStatusId='" + definitionStatusId + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Concept concept = (Concept) o;
		return Objects.equals(conceptId, concept.conceptId) && Objects.equals(effectiveTime, concept.effectiveTime) && Objects.equals(active, concept.active) && Objects.equals(moduleId, concept.moduleId) && Objects.equals(definitionStatusId, concept.definitionStatusId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(conceptId, effectiveTime, active, moduleId, definitionStatusId);
	}
}