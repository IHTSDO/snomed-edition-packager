package org.snomed.snomededitionpackager.domain.rf2;

import java.util.HashMap;
import java.util.Map;

public class PackageComposition {
	private final Map<String, String> essentialComponents = new HashMap<>();
	private final Map<String, String> optionalComponents = new HashMap<>();

	public Map<String, String> getEssentialComponents() {
		return essentialComponents;
	}

	public void setEssentialComponents(Map<String, String> essentialComponents) {
		if (essentialComponents != null) {
			this.essentialComponents.putAll(essentialComponents);
		}
	}

	public Map<String, String> getOptionalComponents() {
		return optionalComponents;
	}

	public void setOptionalComponents(Map<String, String> optionalComponents) {
		if (optionalComponents != null) {
			this.optionalComponents.putAll(optionalComponents);
		}
	}
}