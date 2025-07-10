package org.snomed.snomededitionpackager.domain.reporting;

import java.util.HashMap;
import java.util.Map;

public class RF2ZipFile {
	private Map<String, String> information = new HashMap<>();

	public Map<String, String> getInformation() {
		return information;
	}

	public void setInformation(Map<String, String> information) {
		if (information != null) {
			this.information = information;
		}
	}

	public void addInfo(String key, String value) {
		if (key != null && value != null) {
			information.put(key, value);
		}
	}

	public String getFileName() {
		return information.get("fileName");
	}
}
