package org.snomed.snomededitionpackager.domain.reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.otf.script.dao.ReportConfiguration;
import org.snomed.otf.script.dao.ReportManager;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class GoogleSheetReport implements Report {
	private static final Logger LOGGER = LoggerFactory.getLogger(GoogleSheetReport.class);
	private static final Set<ReportConfiguration.ReportOutputType> OUTPUT_TYPES = Set.of(ReportConfiguration.ReportOutputType.GOOGLE);
	private static final Set<ReportConfiguration.ReportFormatType> FORMAT_TYPES = Set.of(ReportConfiguration.ReportFormatType.CSV);
	private static final ReportConfiguration REPORT_CONFIGURATION = new ReportConfiguration(OUTPUT_TYPES, FORMAT_TYPES);
	private static final GoogleSheetScript GOOGLE_SHEET_SCRIPT = new GoogleSheetScript();

	private ReportManager reportManager;
	private boolean initialised;

	@Override
	public boolean isInitialised() {
		return initialised;
	}

	@Override
	public boolean createSpreadsheet(String reportName, String reportEnvironment, String[] tabs, String[] columns) {
		if (reportName == null || reportName.isEmpty() || reportEnvironment == null || reportEnvironment.isEmpty() || tabs == null || tabs.length == 0 || columns == null || columns.length == 0) {
			LOGGER.trace("Cannot create spreadsheet: invalid parameters.");
			return false;
		}

		initReportManager(reportName, reportEnvironment, tabs, columns);
		return this.reportManager != null && this.initialised;
	}

	@Override
	public boolean writeLine(int index, String line) {
		if (this.reportManager == null || line == null || line.isEmpty()) {
			LOGGER.trace("Cannot create spreadsheet: invalid parameters.");
			return false;
		}

		return doWriteToReportFile(index, line);
	}

	@Override
	public boolean flush() {
		return doFlushFiles();
	}

	private void initReportManager(String reportName, String reportEnvironment, String[] tabs, String[] columns) {
		try {
			GOOGLE_SHEET_SCRIPT.setReportName(reportName);
			GOOGLE_SHEET_SCRIPT.setReportEnvironment(reportEnvironment);

			this.reportManager = ReportManager.create(GOOGLE_SHEET_SCRIPT, REPORT_CONFIGURATION);
			this.reportManager.setWriteToSheet(true);
			this.reportManager.setTabNames(tabs);
			this.reportManager.initialiseReportFiles(columns);
			this.initialised = true;
		} catch (Exception e) {
			this.initialised = false;
			LOGGER.error("Failed to instantiate ReportManager: {}", e.getMessage());
		}
	}

	private boolean doWriteToReportFile(int index, String line) {
		try {
			this.reportManager.writeToReportFile(index, line);
			return true;
		} catch (Exception e) {
			LOGGER.error("Failed to write to report file.");
			return false;
		}
	}

	private boolean doFlushFiles() {
		try {
			this.reportManager.flushFiles(true);
			return true;
		} catch (Exception e) {
			LOGGER.error("Failed to flush files.");
			return false;
		}
	}
}
