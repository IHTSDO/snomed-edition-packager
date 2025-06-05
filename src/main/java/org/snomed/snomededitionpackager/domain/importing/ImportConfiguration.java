package org.snomed.snomededitionpackager.domain.importing;

public class ImportConfiguration {
	private final String input;
	private final boolean full;
	private final String shortName;
	private final String effectiveTime;

	public ImportConfiguration(String input, String full, String shortName, String effectiveTime) {
		this.input = input;
		this.full = "true".equals(full);
		this.shortName = shortName;
		this.effectiveTime = effectiveTime;
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
}
