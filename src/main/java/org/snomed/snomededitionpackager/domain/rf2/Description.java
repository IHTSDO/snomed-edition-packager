package org.snomed.snomededitionpackager.domain.rf2;

import java.util.Objects;

public class Description {
	private String descriptionId;
	private String effectiveTime;
	private String active;
	private String moduleId;
	private String conceptId;
	private String languageCode;
	private String typeId;
	private String term;
	private String caseSignificanceId;

	public String getDescriptionId() {
		return descriptionId;
	}

	public Description setDescriptionId(String descriptionId) {
		this.descriptionId = descriptionId;
		return this;
	}

	public String getEffectiveTime() {
		return effectiveTime;
	}

	public Description setEffectiveTime(String effectiveTime) {
		this.effectiveTime = effectiveTime;
		return this;
	}

	public String getActive() {
		return active;
	}

	public Description setActive(String active) {
		this.active = active;
		return this;
	}

	public String getModuleId() {
		return moduleId;
	}

	public Description setModuleId(String moduleId) {
		this.moduleId = moduleId;
		return this;
	}

	public String getConceptId() {
		return conceptId;
	}

	public Description setConceptId(String conceptId) {
		this.conceptId = conceptId;
		return this;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public Description setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
		return this;
	}

	public String getTypeId() {
		return typeId;
	}

	public Description setTypeId(String typeId) {
		this.typeId = typeId;
		return this;
	}

	public String getTerm() {
		return term;
	}

	public Description setTerm(String term) {
		this.term = term;
		return this;
	}

	public String getCaseSignificanceId() {
		return caseSignificanceId;
	}

	public Description setCaseSignificanceId(String caseSignificanceId) {
		this.caseSignificanceId = caseSignificanceId;
		return this;
	}

	public String toRF2() {
		return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s", descriptionId, effectiveTime, active, moduleId, conceptId, languageCode, typeId, term, caseSignificanceId);
	}

	@Override
	public String toString() {
		return "Description{" +
				"descriptionId='" + descriptionId + '\'' +
				", effectiveTime='" + effectiveTime + '\'' +
				", active='" + active + '\'' +
				", moduleId='" + moduleId + '\'' +
				", conceptId='" + conceptId + '\'' +
				", languageCode='" + languageCode + '\'' +
				", typeId='" + typeId + '\'' +
				", term='" + term + '\'' +
				", caseSignificanceId='" + caseSignificanceId + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Description that = (Description) o;
		return Objects.equals(descriptionId, that.descriptionId) && Objects.equals(effectiveTime, that.effectiveTime) && Objects.equals(active, that.active) && Objects.equals(moduleId, that.moduleId) && Objects.equals(conceptId, that.conceptId) && Objects.equals(languageCode, that.languageCode) && Objects.equals(typeId, that.typeId) && Objects.equals(term, that.term) && Objects.equals(caseSignificanceId, that.caseSignificanceId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(descriptionId, effectiveTime, active, moduleId, conceptId, languageCode, typeId, term, caseSignificanceId);
	}
}