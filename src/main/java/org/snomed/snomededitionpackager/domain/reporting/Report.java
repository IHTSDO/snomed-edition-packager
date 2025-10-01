package org.snomed.snomededitionpackager.domain.reporting;

public interface Report {
	boolean isInitialised();

	boolean createSpreadsheet(String reportName, String reportEnvironment, String[] tabs, String[] columns);

	boolean writeLine(int index, String line);

	boolean flush();
}
