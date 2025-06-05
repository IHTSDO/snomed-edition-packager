package org.snomed.snomededitionpackager.domain.exporting;

public class ExportConfiguration {
	private final String rf2Package;
	private final String shortName;
	private final String effectiveTime;
	private final String releasePackageInformation;
	private final boolean full;
	private final boolean sort;

	public ExportConfiguration(String rf2Package, String shortName, String effectiveTime, String releasePackageInformation, String full, String sort) {
		this.rf2Package = rf2Package;
		this.shortName = shortName;
		this.effectiveTime = effectiveTime;
		this.releasePackageInformation = releasePackageInformation;
		this.full = "true".equals(full);
		this.sort = "true".equals(sort);
	}

	public String getRf2Package() {
		return rf2Package;
	}

	public String getShortName() {
		return shortName;
	}

	public String getEffectiveTime() {
		return effectiveTime;
	}

	public String getReleasePackageInformation() {
		return releasePackageInformation;
	}

	public boolean isFull() {
		return full;
	}

	public boolean isSort() {
		return sort;
	}
}