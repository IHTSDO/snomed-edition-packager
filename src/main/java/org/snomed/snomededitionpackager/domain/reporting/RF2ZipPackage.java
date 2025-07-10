package org.snomed.snomededitionpackager.domain.reporting;

import java.util.*;

public class RF2ZipPackage {
	private String packageName;
	private String absolutePath;
	private List<RF2ZipFile> rf2ZipFiles = new ArrayList<>();

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public List<RF2ZipFile> getRf2ZipFiles() {
		if (rf2ZipFiles == null || rf2ZipFiles.isEmpty()) {
			return Collections.emptyList();
		}

		return rf2ZipFiles.stream().sorted(Comparator.comparing(RF2ZipFile::getFileName, Comparator.nullsLast(String::compareToIgnoreCase))).toList();
	}

	public void setRf2ZipFiles(List<RF2ZipFile> rf2ZipFiles) {
		if (rf2ZipFiles != null) {
			this.rf2ZipFiles = rf2ZipFiles;
		}
	}

	public void addRF2ZipFile(RF2ZipFile rf2ZipFile) {
		if (rf2ZipFile != null) {
			rf2ZipFiles.add(rf2ZipFile);
		}
	}
}
