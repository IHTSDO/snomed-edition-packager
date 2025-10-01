package org.snomed.snomededitionpackager.domain.reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.snomededitionpackager.domain.rf2.RF2;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@Component
public class CSVReport implements Report {
	private static final Logger LOGGER = LoggerFactory.getLogger(CSVReport.class);

	private boolean initialised = false;
	private List<Path> tabs = new ArrayList<>();

	@Override
	public boolean isInitialised() {
		return this.initialised;
	}

	@Override
	public boolean createSpreadsheet(String reportName, String reportEnvironment, String[] tabs, String[] columns) {
		if (reportName == null || reportName.isEmpty() || reportEnvironment == null || reportEnvironment.isEmpty() || tabs == null || tabs.length == 0 || columns == null || columns.length == 0) {
			LOGGER.trace("Cannot create spreadsheet: invalid parameters.");
			return false;
		}

		this.tabs = new ArrayList<>();
		long currentTimeMillis = System.currentTimeMillis();
		for (int i = 0; i < tabs.length; i++) {
			String tab = tabs[i];
			Path path = Paths.get(getTabPath(reportEnvironment, currentTimeMillis, tab));
			this.tabs.add(path);

			// Create text file
			createTextFiles(path);

			// Write to text file
			try (BufferedWriter pathWriter = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
				pathWriter.write(columns[i] + RF2.LINE_ENDING);
			} catch (Exception e) {
				return false;
			}
		}

		this.initialised = true;
		return true;
	}

	@Override
	public boolean writeLine(int index, String line) {
		if (!initialised || line == null || line.isEmpty()) {
			LOGGER.trace("Cannot create spreadsheet: invalid parameters.");
			return false;
		}

		try (BufferedWriter pathWriter = Files.newBufferedWriter(this.tabs.get(index), StandardOpenOption.CREATE, StandardOpenOption.APPEND);) {
			pathWriter.write(line + RF2.LINE_ENDING);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	@Override
	public boolean flush() {
		this.initialised = false;
		this.tabs = new ArrayList<>();

		return true;
	}

	private boolean createTextFiles(Path... paths) {
		for (Path path : paths) {
			try {
				Files.createDirectories(path.getParent());

				if (!Files.exists(path)) {
					Files.createFile(path);
				}
			} catch (Exception e) {
				return false;
			}
		}

		return true;
	}

	private String getTabPath(String reportEnvironment, long currentTimeMillis, String tab) {
		return String.format("output/report/%s/%s/%s.csv", reportEnvironment, currentTimeMillis, tab);
	}
}
