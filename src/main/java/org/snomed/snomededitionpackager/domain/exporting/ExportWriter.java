package org.snomed.snomededitionpackager.domain.exporting;

import org.snomed.snomededitionpackager.domain.rf2.RF2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public interface ExportWriter {
	boolean write(ExportConfiguration exportConfiguration);

	default boolean createTextFiles(boolean full, Path fullPath, Path snapshotPath) {
		if (full) {
			return createTextFiles(fullPath, snapshotPath);
		} else {
			return createTextFiles(snapshotPath);
		}
	}

	default boolean createTextFiles(Path... paths) {
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

	default boolean writeHeader(boolean full, String header, Path fullPath, Path snapshotPath) {
		if (full) {
			return writeHeader(header, fullPath, snapshotPath);
		} else {
			return writeHeader(header, snapshotPath);
		}
	}

	default boolean writeHeader(String header, Path... paths) {
		if (header == null || paths == null || paths.length == 0) {
			return false;
		}

		// Check if header exists
		try (BufferedReader reader = Files.newBufferedReader(paths[0], StandardCharsets.UTF_8)) {
			String firstLine = reader.readLine();
			if (header.equals(firstLine)) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}


		for (Path path : paths) {
			try {
				// Append header or create file and write header
				if (!Files.exists(path)) {
					Files.writeString(path, header + RF2.LINE_ENDING, StandardOpenOption.CREATE);
				} else {
					Files.writeString(path, header + RF2.LINE_ENDING, StandardOpenOption.APPEND);
				}
			} catch (Exception e) {
				return false;
			}
		}

		return true;
	}

	default BufferedWriter initBufferedWriter(boolean init, Path path) {
		if (init) {
			try {
				return Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			} catch (Exception e) {
				return null;
			}
		}

		return null;
	}

	default BufferedWriter initBufferedWriter(Path path) {
		return initBufferedWriter(true, path);
	}
}
