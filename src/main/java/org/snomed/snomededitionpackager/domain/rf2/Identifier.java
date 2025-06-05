package org.snomed.snomededitionpackager.domain.rf2;

public class Identifier {
	private String alternateIdentifier;
	private String effectiveTime;
	private String active;
	private String moduleId;
	private String identifierSchemeId;
	private String referencedComponentId;

	public String getAlternateIdentifier() {
		return alternateIdentifier;
	}

	public Identifier setAlternateIdentifier(String alternateIdentifier) {
		this.alternateIdentifier = alternateIdentifier;
		return this;
	}

	public String getEffectiveTime() {
		return effectiveTime;
	}

	public Identifier setEffectiveTime(String effectiveTime) {
		this.effectiveTime = effectiveTime;
		return this;
	}

	public String getActive() {
		return active;
	}

	public Identifier setActive(String active) {
		this.active = active;
		return this;
	}

	public String getModuleId() {
		return moduleId;
	}

	public Identifier setModuleId(String moduleId) {
		this.moduleId = moduleId;
		return this;
	}

	public String getIdentifierSchemeId() {
		return identifierSchemeId;
	}

	public Identifier setIdentifierSchemeId(String identifierSchemeId) {
		this.identifierSchemeId = identifierSchemeId;
		return this;
	}

	public String getReferencedComponentId() {
		return referencedComponentId;
	}

	public Identifier setReferencedComponentId(String referencedComponentId) {
		this.referencedComponentId = referencedComponentId;
		return this;
	}

	public String toRF2() {
		return String.format("%s\t%s\t%s\t%s\t%s\t%s", alternateIdentifier, effectiveTime, active, moduleId, identifierSchemeId, referencedComponentId);
	}
}