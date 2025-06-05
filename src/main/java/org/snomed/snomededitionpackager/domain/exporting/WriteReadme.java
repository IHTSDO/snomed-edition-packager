package org.snomed.snomededitionpackager.domain.exporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.snomededitionpackager.domain.rf2.RF2;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Comparator;

@Component
public class WriteReadme implements ExportWriter {
	private static final Logger LOGGER = LoggerFactory.getLogger(WriteReadme.class);
	private static final String INDENTATION = "    ";

	@Override
	public boolean write(ExportConfiguration exportConfiguration) {
		if (exportConfiguration == null) {
			return false;
		}

		// Prepare text file(s)
		String rf2Package = exportConfiguration.getRf2Package();
		String effectiveTime = exportConfiguration.getEffectiveTime();
		Path path = Paths.get(FileNameService.getReadme(rf2Package, effectiveTime));
		String introduction = formatReadMeIntroduction(rf2Package, effectiveTime);

		// Create text file
		if (!createTextFiles(path)) {
			return false;
		}

		// Write to text file
		try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
			bufferedWriter.write(introduction + RF2.LINE_ENDING);

			writeDirectoryRecursive(new File(rf2Package), 0, bufferedWriter);
		} catch (Exception e) {
			LOGGER.error("Failed to write to Readme file.", e);
			return false;
		}

		return true;
	}

	private void writeDirectoryRecursive(File file, int depth, BufferedWriter writer) throws IOException {
		if (file == null || !file.exists()) {
			return;
		}

		for (int a = 0; a < depth; a++) {
			writer.write(INDENTATION);
		}

		writer.write(file.getName() + RF2.LINE_ENDING);

		if (file.isDirectory()) {
			File[] children = file.listFiles();
			if (children != null) {
				Arrays.sort(children, Comparator.comparing(File::getName));
				for (File child : children) {
					writeDirectoryRecursive(child, depth + 1, writer);
				}
			}
		}
	}

	private String formatReadMeIntroduction(String rf2Package, String effectiveTime) {
		String alias = formatRf2PackageAlias(rf2Package);
		effectiveTime = effectiveTime.substring(0, 4); // Take year from effectiveTime for copyright notice
		return String.format("The %s release is provided in UTF-8 encoded tab-delimited flat files which can be imported into any database or other software application.  SNOMED CT is not software.%n%nThe SNOMED CT files are designed as relational tables with each line in the file representing a row in the table. The first row of each table contains column headings. All other rows contain data.%n%nThe %s release is delivered to IHTSDO Member National Centers and authorized Affiliate Licensees via Internet download.  %n%n© International Health Terminology Standards Development Organisation 2002-%s.  All rights reserved.  SNOMED CT® was originally created by the College of American Pathologists.  \"SNOMED\" and \"SNOMED CT\" are registered trademarks of International Health Terminology Standards Development Organisation, trading as SNOMED International.%n%nSNOMED CT has been created by combining SNOMED RT and a computer based nomenclature and classification known as Clinical Terms Version 3, formerly known as Read Codes Version 3, which was created on behalf of the UK Department of Health and is Crown copyright.%n%nThis document forms part of the International Edition release of SNOMED CT® distributed by SNOMED International, which is subject to the SNOMED CT® Affiliate License, details of which may be found at  https://www.snomed.org/snomed-ct/get-snomed.%n%nDirectory listing:%n", alias, alias, effectiveTime);
	}

	private String formatRf2PackageAlias(String input) {
		if (!input.startsWith("./output/SnomedCT")) {
			input = input.replaceAll("./output/", "") + ".zip";
			return input;
		}

		String[] parts = input.split("_");
		if (parts.length < 2) {
			return "";
		}

		// SnomedCT -> SNOMED CT
		String snomed = parts[0].replaceAll("(?i)SnomedCT", "SNOMED CT");

		// ManagedServiceXX -> Managed Service XX
		String managedService = parts[1].replaceAll("([a-z])([A-Z])", "$1 $2");
		managedService = managedService.replaceAll("(?<=\\D)(\\d)", " $1");

		return snomed + " " + managedService;
	}
}