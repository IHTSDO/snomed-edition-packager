package org.snomed.snomededitionpackager.domain.reporting;

import org.snomed.otf.scheduler.domain.JobRun;
import org.snomed.otf.script.Script;
import org.snomed.otf.script.dao.DataBroker;

public class GoogleSheetScript extends Script {
	private String reportName;
	private String reportEnvironment;

	@Override
	public boolean isOffline() {
		return false;
	}

	@Override
	public JobRun getJobRun() {
		return null;
	}

	@Override
	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName + "_";
	}

	@Override
	public String detectReleaseBranch() {
		return null;
	}

	@Override
	public String getEnv() {
		return reportEnvironment;
	}

	public void setReportEnvironment(String reportEnvironment) {
		this.reportEnvironment = reportEnvironment;
	}

	@Override
	public DataBroker getReportDataUploader() {
		return null;
	}
}
