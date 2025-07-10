package org.snomed.snomededitionpackager.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Utility class with various methods.
 */
public class Util {
	private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);

	private Util() {

	}

	/**
	 * Return whether the given ZipFile is safe to open. Safety is defined as not being exported into an unexpected
	 * location (zip slip) or not being exported with an unexpected size (zip bomb).
	 *
	 * @param zipFile ZipFile to check is safe to open.
	 * @return Whether the given ZipFile is safe to open.
	 */
	public static boolean isSafe(ZipFile zipFile) {
		long maxTotalSize = 10L * 1024 * 1024 * 1024; // 10 GB
		long currentTotalSize = 0;

		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry zipEntry = entries.nextElement();
			if (zipEntry.isDirectory()) {
				continue;
			}

			// Prevent zip slip, i.e. expanding zip into unexpected location
			String zipEntryName = zipEntry.getName();
			if (zipEntryName.contains("..") || zipEntryName.startsWith("/") || zipEntryName.startsWith("\\")) {
				LOGGER.error("Zip slip detected in entry: {}", zipEntryName);
				return false;
			}

			// Prevent zip bomb, i.e. expanding zip with unlimited size
			try (InputStream in = new BufferedInputStream(zipFile.getInputStream(zipEntry))) {
				byte[] buffer = new byte[4096]; // 4kb
				int bytesRead;

				while ((bytesRead = in.read(buffer)) != -1) {
					currentTotalSize += bytesRead;

					if (currentTotalSize > maxTotalSize) {
						LOGGER.error("Uncompressed archive size exceeds 10 GB");
						return false;
					}
				}

			} catch (Exception e) {
				LOGGER.error("Error reading zip entry: {}", zipEntryName, e);
				return false;
			}
		}

		return true;
	}

	/**
	 * Return whether the given File is a type of ZipFile.
	 *
	 * @param file File to check is a type of ZipFile.
	 * @return Whether the given File is a type of ZipFile.
	 */
	public static boolean isZip(File file) {
		if (file.isFile() && file.getAbsolutePath().endsWith(".zip")) {
			try (ZipFile zipFile = new ZipFile(file)) {
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		return false;
	}

	/**
	 * Return whether there is a ZipFile is the current working directory.
	 *
	 * @return Whether there is a ZipFile is the current working directory.
	 */
	public static boolean hasPackageInCurrentDirectory() {
		File currentDirectory = new File(".");
		File[] files = currentDirectory.listFiles();
		if (files == null) {
			return false;
		}

		for (File file : files) {
			boolean isZip = Util.isZip(file);
			if (isZip) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Return whether all given paths are of type ZipFile.
	 *
	 * @param paths Paths to check are of type ZipFile.
	 * @return whether all given paths are of type ZipFile.
	 */
	public static boolean givenPathsArePackages(List<String> paths) {
		for (String path : paths) {
			File file = new File(path);
			boolean isZip = Util.isZip(file);
			if (!isZip) {
				return false;
			}
		}

		return true;
	}
}
