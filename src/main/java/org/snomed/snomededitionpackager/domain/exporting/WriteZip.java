package org.snomed.snomededitionpackager.domain.exporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class WriteZip implements ExportWriter {
	private static final Logger LOGGER = LoggerFactory.getLogger(WriteZip.class);

	@Override
	public boolean write(ExportConfiguration exportConfiguration) {
		if (exportConfiguration == null) {
			return false;
		}

		String rf2Package = exportConfiguration.getRf2Package();
		Path sourceDirPath = Path.of(rf2Package);
		Path zipFilePath = Path.of(rf2Package + ".zip");
		try (
				ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(zipFilePath));
				Stream<Path> paths = Files.walk(sourceDirPath)
		) {
			String rootFolderName = zipFilePath.getFileName().toString().replaceFirst("\\.zip$", "");
			exportRootFolderName(rootFolderName, zs);

			paths.filter(path -> !Files.isDirectory(path)).forEach(path -> {
				String entryName = rootFolderName + "/" + sourceDirPath.relativize(path).toString().replace("\\", "/");
				ZipEntry zipEntry = new ZipEntry(entryName);
				try {
					zs.putNextEntry(zipEntry);
					Files.copy(path, zs);
					zs.closeEntry();
				} catch (IOException e) {
					LOGGER.error("Error zipping file {}", path, e);
				}
			});
		} catch (IOException e) {
			return false;
		}

		// Delete uncompressed
		deleteDirectoryRecursively(new File(rf2Package));

		return true;
	}

	private void exportRootFolderName(String rootFolderName, ZipOutputStream zs) throws IOException {
		ZipEntry rootDirEntry = new ZipEntry(rootFolderName + "/");
		zs.putNextEntry(rootDirEntry);
		zs.closeEntry();
	}

	private void deleteDirectoryRecursively(File file) {
		try {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				if (files != null) {
					for (File child : files) {
						deleteDirectoryRecursively(child);
					}
				}
			}
			Files.delete(file.toPath());
		} catch (Exception e) {
			// ignore
		}
	}
}
