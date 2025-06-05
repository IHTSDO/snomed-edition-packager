package org.snomed.snomededitionpackager.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.snomededitionpackager.domain.arguments.Arguments;
import org.snomed.snomededitionpackager.domain.exporting.ExportConfiguration;
import org.snomed.snomededitionpackager.domain.exporting.ExportService;
import org.snomed.snomededitionpackager.domain.importing.ImportConfiguration;
import org.snomed.snomededitionpackager.domain.importing.ImportService;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class CombineHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(CombineHandler.class);
	private static final String OUTPUT = "output";

	private final ImportService importService;
	private final ExportService exportService;

	public CombineHandler(ImportService importService, ExportService exportService) {
		this.importService = importService;
		this.exportService = exportService;
	}

	public boolean combine(Arguments arguments) {
		if (arguments == null || !arguments.hasArgs()) {
			LOGGER.error("Given arguments or null or invalid.");
			return false;
		}

		// Load package(s) into memory
		boolean packagesImported = importPackages(arguments);
		if (!packagesImported) {
			LOGGER.error("Failed to load packages into memory");
			return false;
		}

		// Write memory to new package
		boolean packageExported = exportPackage(arguments);
		if (!packageExported) {
			LOGGER.error("Failed to write memory to package.");
			return false;
		}

		return true;
	}

	private boolean importPackages(Arguments arguments) {
		LOGGER.info("Import starting...");
		long start = System.currentTimeMillis();
		String shortName = arguments.getArg("shortName", "XX");
		String effectiveTime = arguments.getArg("effectiveTime", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
		ImportConfiguration importConfiguration = new ImportConfiguration(arguments.getArg("input"), arguments.getArg("full"), shortName, effectiveTime);
		boolean success = importService.importPackages(importConfiguration);

		long end = System.currentTimeMillis();
		long total = (end - start) / 1_000;
		LOGGER.info("...finished after {} seconds.", total);
		return success;
	}

	private boolean exportPackage(Arguments arguments) {
		LOGGER.info("Export starting...");
		long start = System.currentTimeMillis();

		createOutputDirectory();
		String rf2Package = "./output/" + (arguments.isWildcard(OUTPUT) ? String.valueOf(System.currentTimeMillis()) : arguments.getArg(OUTPUT));
		String shortName = arguments.getArg("shortName", "XX");
		String effectiveTime = arguments.getArg("effectiveTime", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
		String releasePackageInformation = arguments.getArg("releasePackageInformation");
		String full = arguments.getArg("full");
		String sort = arguments.getArg("sort");
		String readMe = arguments.getArg("readMe");
		ExportConfiguration exportConfiguration = new ExportConfiguration(rf2Package, shortName, effectiveTime, releasePackageInformation, full, sort, readMe);
		boolean success = exportService.export(exportConfiguration);

		long end = System.currentTimeMillis();
		long total = (end - start) / 1_000;
		LOGGER.info("...finished after {} seconds.", total);
		return success;
	}

	private void createOutputDirectory() {
		File output = new File(OUTPUT);
		if (!output.exists() && !output.mkdirs()) {
			LOGGER.error("Failed to create output directory.");
		}
	}
}