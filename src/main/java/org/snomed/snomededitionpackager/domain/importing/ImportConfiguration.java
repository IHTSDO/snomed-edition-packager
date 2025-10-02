package org.snomed.snomededitionpackager.domain.importing;

public class ImportConfiguration {
	private final String input;
	private final boolean full;
	private final String shortName;
	private final String effectiveTime;
	private final String referenceSetsByType;

	public ImportConfiguration(String input, String full, String shortName, String effectiveTime, String referenceSetsByType) {
		this.input = input;
		this.full = "true".equals(full);
		this.shortName = shortName;
		this.effectiveTime = effectiveTime;
		this.referenceSetsByType = referenceSetsByType;
	}

	public String getInput() {
		return input;
	}

	public boolean isFull() {
		return full;
	}

	public String getShortName() {
		return shortName;
	}

	public String getEffectiveTime() {
		return effectiveTime;
	}

	public String getReferenceSetsByType() {
		return referenceSetsByType;
	}
}
