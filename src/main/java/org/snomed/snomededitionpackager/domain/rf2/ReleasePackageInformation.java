package org.snomed.snomededitionpackager.domain.rf2;

import java.util.HashSet;
import java.util.Set;

public class ReleasePackageInformation {
	private String effectiveTime;
	private String previousPublishedPackage;
	private String licenceStatement;
	private Set<LanguageReferenceSet> languageRefsets = new HashSet<>();
	private PackageComposition packageComposition = new PackageComposition();

	public String getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(String effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public String getPreviousPublishedPackage() {
		return previousPublishedPackage;
	}

	public void setPreviousPublishedPackage(String previousPublishedPackage) {
		this.previousPublishedPackage = previousPublishedPackage;
	}

	public String getLicenceStatement() {
		return licenceStatement;
	}

	public void setLicenceStatement(String licenceStatement) {
		this.licenceStatement = licenceStatement;
	}

	public Set<LanguageReferenceSet> getLanguageRefsets() {
		return languageRefsets;
	}

	public void setLanguageRefsets(Set<LanguageReferenceSet> languageRefsets) {
		this.languageRefsets = languageRefsets;
	}

	public PackageComposition getPackageComposition() {
		return packageComposition;
	}

	public void setPackageComposition(PackageComposition packageComposition) {
		this.packageComposition = packageComposition;
	}

	public void addPackageComposition(PackageComposition packageComposition) {
		if (packageComposition != null) {
			this.packageComposition.setEssentialComponents(packageComposition.getEssentialComponents());
			this.packageComposition.setOptionalComponents(packageComposition.getOptionalComponents());
		}
	}
}