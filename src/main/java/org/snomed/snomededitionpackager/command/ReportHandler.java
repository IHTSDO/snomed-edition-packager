package org.snomed.snomededitionpackager.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.snomededitionpackager.domain.arguments.Arguments;
import org.snomed.snomededitionpackager.domain.reporting.RF2ZipFile;
import org.snomed.snomededitionpackager.domain.reporting.RF2ZipPackage;
import org.snomed.snomededitionpackager.domain.reporting.ReportingService;
import org.snomed.snomededitionpackager.util.Util;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component
public class ReportHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportHandler.class);
	private static final String INPUT = "input";

	private final ReportingService reportingService;

	public ReportHandler(ReportingService reportingService) {
		this.reportingService = reportingService;
	}

	public boolean createReport(Arguments arguments) {
		if (arguments == null || !arguments.hasArgs()) {
			LOGGER.error("Given arguments or null or invalid.");
			return false;
		}

		// Assert package(s) available
		boolean packagesAvailable = arguments.isWildcard(INPUT) ? Util.hasPackageInCurrentDirectory() : Util.givenPathsArePackages(arguments.getArgList(INPUT));
		if (!packagesAvailable) {
			LOGGER.error("No packages given or found.");
			return false;
		}

		// Collect information from packages
		List<RF2ZipPackage> rf2ZipPackages = getRF2ZipPackagesFromPaths(arguments);
		if (rf2ZipPackages.isEmpty()) {
			LOGGER.error("No package(s) processed.");
			return false;
		}

		// Produce report from packages
		return reportPackages(rf2ZipPackages);
	}

	@SuppressWarnings("java:S5042") // SonarQube ignores logic of Util.isSafe
	private List<RF2ZipPackage> getRF2ZipPackagesFromPaths(Arguments arguments) {
		List<RF2ZipPackage> rf2ZipPackages = new ArrayList<>();
		for (String packagePath : getPackagePaths(arguments)) {
			RF2ZipPackage rf2ZipPackage = new RF2ZipPackage();
			rf2ZipPackage.setPackageName(packagePath.split("/")[packagePath.split("/").length - 1]);
			rf2ZipPackage.setAbsolutePath(packagePath);

			try (ZipFile zipFile = new ZipFile(packagePath)) {
				if (!Util.isSafe(zipFile)) {
					return Collections.emptyList();
				}

				Enumeration<? extends ZipEntry> entries = zipFile.entries();
				while (entries.hasMoreElements()) {
					ZipEntry zipEntry = entries.nextElement();

					if (zipEntry.isDirectory()) {
						continue;
					}

					RF2ZipFile rf2ZipFile = parseIntoRF2ZipFile(zipFile, zipEntry);
					if (rf2ZipFile != null) {
						rf2ZipPackage.addRF2ZipFile(rf2ZipFile);
					}
				}
			} catch (Exception e) {
				LOGGER.error("Failed to expand zip: {}", packagePath, e);
			}

			rf2ZipPackages.add(rf2ZipPackage);
		}

		return rf2ZipPackages;
	}

	private Set<String> getPackagePaths(Arguments arguments) {
		Set<String> packagePaths = new HashSet<>();
		if (arguments.isWildcard(INPUT)) {
			File currentDirectory = new File(".");
			File[] files = currentDirectory.listFiles();
			if (files == null) {
				return Collections.emptySet();
			}

			for (File file : files) {
				boolean isZip = Util.isZip(file);
				if (isZip) {
					packagePaths.add(file.getAbsolutePath());
				}
			}

			return packagePaths;
		} else {
			return new HashSet<>(arguments.getArgList(INPUT));
		}
	}

	private RF2ZipFile parseIntoRF2ZipFile(ZipFile zipFile, ZipEntry zipEntry) {
		try (InputStream inputStream = zipFile.getInputStream(zipEntry); BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
			RF2ZipFile rf2ZipFile = new RF2ZipFile();
			String fileName = zipEntry.getName();
			fileName = fileName.split("/")[fileName.split("/").length - 1];
			rf2ZipFile.addInfo("fileName", fileName);
			rf2ZipFile.addInfo("fileLength", String.valueOf(bufferedReader.lines().count()));

			return rf2ZipFile;
		} catch (Exception e) {
			LOGGER.error("Failed to expand file.", e);
			return null;
		}
	}

	private boolean reportPackages(List<RF2ZipPackage> rf2ZipPackages) {
		boolean openReport = reportingService.openReport("Report", generateTabs(rf2ZipPackages), generateColumns(rf2ZipPackages));
		if (!openReport) {
			return false;
		}

		for (int i = 0; i < rf2ZipPackages.size(); i++) {
			RF2ZipPackage rf2ZipPackage = rf2ZipPackages.get(i);
			List<RF2ZipFile> rf2ZipFiles = rf2ZipPackage.getRf2ZipFiles();
			for (RF2ZipFile rf2ZipFile : rf2ZipFiles) {
				Map<String, String> information = rf2ZipFile.getInformation();
				String fileName = information.get("fileName");
				String fileLength = information.get("fileLength");
				String type = "-"; // Readme, .json etc
				if (fileName.contains("Full")) {
					type = "Full";
				} else if (fileName.contains("Snapshot")) {
					type = "Snapshot";
				}

				reportingService.writeCSV(i, fileName, type, fileLength);
			}
		}

		return reportingService.closeReport();
	}

	private String[] generateTabs(List<RF2ZipPackage> rf2ZipPackages) {
		String[] tabs = new String[rf2ZipPackages.size()];
		for (int i = 0; i < rf2ZipPackages.size(); i++) {
			tabs[i] = rf2ZipPackages.get(i).getPackageName();
		}

		return tabs;
	}

	private String[] generateColumns(List<RF2ZipPackage> rf2ZipPackages) {
		String[] columns = new String[rf2ZipPackages.size()];
		columns[0] = "Command, Arguments";
		for (int i = 0; i < rf2ZipPackages.size(); i++) {
			columns[i] = "File, Type, Length";
		}

		return columns;
	}
}
