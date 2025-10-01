package org.snomed.snomededitionpackager.domain.reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.snomededitionpackager.configuration.ApplicationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Component
public class ReportingService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportingService.class);

	private final ApplicationProperties applicationProperties;
	private final GoogleSheetReport googleSheetReport;
	private final CSVReport csvReport;
	private boolean createReport = false;

	public ReportingService(ApplicationProperties applicationProperties, GoogleSheetReport googleSheetReport, CSVReport csvReport) {
		this.applicationProperties = applicationProperties;
		this.googleSheetReport = googleSheetReport;
		this.csvReport = csvReport;
	}

	public boolean openReport(String reportName, String[] tabs, String[] columns) {
		if (reportName == null || reportName.isEmpty() || tabs == null || tabs.length == 0 || columns == null || columns.length == 0) {
			LOGGER.trace("Cannot open report: invalid parameters.");
			return false;
		}

		if (tabs.length != columns.length) {
			// e.g. trying to add a column to a non-existent tab
			LOGGER.error("Cannot open report: tabs and columns differ in size.");
			return false;
		}

		boolean success = googleSheetReport.createSpreadsheet(reportName, applicationProperties.getAppEnvironment(), tabs, columns);
		if (success) {
			LOGGER.info("Created remote Google Sheet.");
		} else {
			success = csvReport.createSpreadsheet(reportName, applicationProperties.getAppEnvironment(), tabs, columns);
			if (success) {
				LOGGER.info("Created local CSV file.");
			}
		}

		this.createReport = success;
		return success;
	}

	public boolean writeLine(int index, String line) {
		if (!createReport) {
			LOGGER.error("Cannot write line: no report has been created.");
			return false;
		}

		if (line == null) {
			LOGGER.error("Cannot write line: line is null");
			return false;
		}

		return getReportImpl().writeLine(index, line);
	}

	public boolean writeCSV(int index, String... line) {
		return writeLine(index, String.join(",", Arrays.asList(line)));
	}

	public boolean closeReport() {
		if (!createReport) {
			LOGGER.error("Cannot close report: no report has been created.");
			return false;
		}

		return getReportImpl().flush();
	}

	private Report getReportImpl() {
		return googleSheetReport.isInitialised() ? googleSheetReport : csvReport;
	}
}
