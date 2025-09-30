package org.snomed.snomededitionpackager.domain.importing;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ihtsdo.otf.snomedboot.ReleaseImporter;
import org.ihtsdo.otf.snomedboot.factory.LoadingProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.snomededitionpackager.domain.datastore.DataStore;
import org.snomed.snomededitionpackager.domain.exporting.FileNameService;
import org.snomed.snomededitionpackager.domain.rf2.ReferenceSetMember;
import org.snomed.snomededitionpackager.domain.rf2.ReleasePackageInformation;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

@Component
public class ImportService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImportService.class);

	private final ReleaseImporter releaseImporter;
	private final DataStore dataStore;

	public ImportService(ReleaseImporter releaseImporter, DataStore dataStore) {
		this.releaseImporter = releaseImporter;
		this.dataStore = dataStore;
	}

	public boolean importPackages(ImportConfiguration importConfiguration) {
		if (importConfiguration == null) {
			return false;
		}

		// Assert package(s) available
		boolean packagesAvailable = arePackagesAvailable(importConfiguration);
		if (!packagesAvailable) {
			LOGGER.error("No packages given or found.");
			return false;
		}

		// Load package(s) into memory
		boolean packagesLoadedIntoMemory = loadPackagesIntoMemory(importConfiguration);
		if (!packagesLoadedIntoMemory) {
			LOGGER.error("Failed to load packages into memory");
			return false;
		}

		return true;
	}

	private boolean arePackagesAvailable(ImportConfiguration importConfiguration) {
		if (Objects.equals("*", importConfiguration.getInput())) {
			return hasPackageInCurrentDirectory();
		} else {
			return givenPathsArePackages(importConfiguration);
		}
	}

	private boolean hasPackageInCurrentDirectory() {
		File currentDirectory = new File(".");
		File[] files = currentDirectory.listFiles();
		if (files == null) {
			return false;
		}

		for (File file : files) {
			boolean isZip = isZip(file);
			if (isZip) {
				return true;
			}
		}

		return false;
	}

	private boolean givenPathsArePackages(ImportConfiguration importConfiguration) {
		String[] split = importConfiguration.getInput().split(",");
		boolean valid = true;
		for (String s : split) {
			if (!valid) {
				return false;
			}

			File file = new File(s);
			boolean isZip = isZip(file);
			if (!isZip) {
				valid = false;
			}
		}

		return valid;
	}

	private boolean isZip(File file) {
		if (file.isFile() && file.getAbsolutePath().endsWith(".zip")) {
			try (ZipFile zipFile = new ZipFile(file)) {
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		return false;
	}

	private boolean loadPackagesIntoMemory(ImportConfiguration importConfiguration) {
		// Flush previous runs
		dataStore.clear();

		// Convert to files for processing
		Set<String> packagePaths = getPackagePaths(importConfiguration);
		Set<File> files = packagePaths.stream().map(File::new).collect(Collectors.toSet());
		if (files.isEmpty()) {
			return false;
		}

		// Unzip to tmp directory
		Set<String> unzippedPackagePaths = new HashSet<>();
		for (File file : files) {
			String unzippedPackagePath = unzipPackage(file);
			if (unzippedPackagePath == null) {
				return false;
			}

			unzippedPackagePaths.add(unzippedPackagePath);
		}

		// Load content into memory
		for (String unzippedPackagePath : unzippedPackagePaths) {
			try {
				if (importConfiguration.isFull()) {
					releaseImporter.loadFullReleaseFiles(unzippedPackagePath, LoadingProfile.complete, new ComponentFactoryImpl(dataStore), false);
				}
				releaseImporter.loadSnapshotReleaseFiles(unzippedPackagePath, LoadingProfile.complete, new ComponentFactoryImpl(dataStore), false);
			} catch (Exception e) {
				return false;
			}
		}

		// Cache file names
		boolean success = cacheReferenceSets(unzippedPackagePaths);
		if (!success) {
			LOGGER.error("Failed to cache file names");
			return false;
		}

		// Cache release package information
		cacheReleasePackageInformation(unzippedPackagePaths);

		// Cache empty files
		cacheEmptyFiles(unzippedPackagePaths, importConfiguration);

		// Delete tmp data
		for (String unzippedPackagePath : unzippedPackagePaths) {
			deleteDirectoryRecursively(new File(unzippedPackagePath));
		}

		// Verify compatibility
		Map<String, Set<String>> compatibilityIssues = getCompatibilityIssues();
		if (!compatibilityIssues.isEmpty()) {
			LOGGER.error("Compatibility issues detected in MDRS:{}", compatibilityIssues);
			return false;
		}

		return true;
	}

	// Lost file names and headers; cache from given file(s)
	private boolean cacheReferenceSets(Set<String> unzippedPackagePaths) {
		Map<String, String> referenceSetFileNameByRefsetId = new HashMap<>();
		Map<String, String> referenceSetHeaderByRefsetId = new HashMap<>();

		for (String unzippedPackagePath : unzippedPackagePaths) {
			List<File> referenceSetFiles = getReferenceSetFiles(new File(unzippedPackagePath));
			if (referenceSetFiles.isEmpty()) {
				continue;
			}

			for (File referenceSetFile : referenceSetFiles) {
				try (BufferedReader reader = new BufferedReader(new FileReader(referenceSetFile))) {
					String line;
					String header = reader.readLine();
					while ((line = reader.readLine()) != null) {
						String[] columns = line.split("\t");
						if (columns.length > 4) {
							String input = String.format("%s/%s", referenceSetFile.getParent(), referenceSetFile.getName());
							input = FileNameService.removeType(input);
							input = FileNameService.removeShortNameEffectiveTime(input);
							input = FileNameService.removeLeadingSlashes(input, 4);
							input = FileNameService.removeDerivativePrefix(input);
							String refsetId = columns[4];

							referenceSetFileNameByRefsetId.putIfAbsent(refsetId, input);
							referenceSetHeaderByRefsetId.putIfAbsent(refsetId, header);
						}
					}
				} catch (Exception e) {
					LOGGER.error("Failed to read file", e);
					return false;
				}
			}
		}

		dataStore.cacheFileName(referenceSetFileNameByRefsetId);
		dataStore.cacheHeader(referenceSetHeaderByRefsetId);

		return true;
	}

	private void cacheReleasePackageInformation(Set<String> unzippedPackagePaths) {
		for (String unzippedPackagePath : unzippedPackagePaths) {
			ReleasePackageInformation releasePackageInformation = getReleasePackageInformationNullable(unzippedPackagePath);
			if (releasePackageInformation == null) {
				continue;
			}

			dataStore.cacheReleasePackageInformation(releasePackageInformation);
		}
	}

	private void cacheEmptyFiles(Set<String> unzippedPackagePaths, ImportConfiguration importConfiguration) {
		Map<String, String> emptyFiles = new HashMap<>();
		Map<String, String> nonEmptyFiles = new HashMap<>();
		for (String unzippedPackagePath : unzippedPackagePaths) {
			List<File> allFiles = getAllFiles(new File(unzippedPackagePath));
			for (File allFile : allFiles) {
				try (BufferedReader reader = new BufferedReader(new FileReader(allFile))) {
					String firstLine = reader.readLine();
					String secondLine = reader.readLine();
					String input = String.format("%s/%s", allFile.getParent(), allFile.getName());
					input = FileNameService.removeShortNameEffectiveTime(input);
					int numberToKeep = input.contains("Refset/") ? 4 : 3;
					input = FileNameService.removeLeadingSlashes(input, numberToKeep);
					input = input + "_" + importConfiguration.getShortName() + "_" + importConfiguration.getEffectiveTime();
					input = FileNameService.removeDerivativePrefix(input);

					if (secondLine == null) {
						// Only header is present
						emptyFiles.put(input, firstLine);
					} else {
						nonEmptyFiles.put(input, firstLine);
					}
				} catch (Exception e) {
					LOGGER.error("Failed to read file", e);
				}
			}
		}

		// Identify files not present in ANY input
		for (String key : nonEmptyFiles.keySet()) {
			emptyFiles.remove(key);
		}

		if (!importConfiguration.isFull()) {
			emptyFiles.keySet().removeIf(key -> key.contains("Full"));
		}

		dataStore.cacheEmptyFiles(emptyFiles);
	}

	private List<File> getReferenceSetFiles(File dir) {
		List<File> allFiles = getAllFiles(dir);
		if (allFiles == null || allFiles.isEmpty()) {
			return Collections.emptyList();
		}

		List<File> referenceSetFiles = new ArrayList<>();
		for (File file : allFiles) {
			if (file.getName().endsWith(".txt") && file.getName().contains("Refset")) {
				referenceSetFiles.add(file);
			}
		}

		return referenceSetFiles;
	}

	private List<File> getAllFiles(File dir) {
		if (dir == null || !dir.exists()) {
			return Collections.emptyList();
		}

		File[] files = dir.listFiles();
		if (files == null) {
			return Collections.emptyList();
		}

		List<File> allFiles = new ArrayList<>();
		for (File file : files) {
			if (file.isDirectory()) {
				allFiles.addAll(getAllFiles(file));
			} else if (file.getName().endsWith(".txt")) {
				allFiles.add(file);
			}
		}

		return allFiles;
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

	private String unzipPackage(File source) {
		try (net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile(source)) {
			File unzippedFolder = new File("input", String.valueOf(System.currentTimeMillis()));
			if (!unzippedFolder.mkdirs()) {
				return null;
			}

			zipFile.extractAll(unzippedFolder.getAbsolutePath());
			unzippedFolder.deleteOnExit();
			return unzippedFolder.getAbsolutePath();
		} catch (Exception e) {
			return null;
		}
	}

	private Set<String> getPackagePaths(ImportConfiguration importConfiguration) {
		Set<String> packagePaths = new HashSet<>();
		if (Objects.equals("*", importConfiguration.getInput())) {
			File currentDirectory = new File(".");
			File[] files = currentDirectory.listFiles();
			if (files == null) {
				return Collections.emptySet();
			}

			for (File file : files) {
				boolean isZip = isZip(file);
				if (isZip) {
					packagePaths.add(file.getAbsolutePath());
				}
			}

			return packagePaths;
		} else {
			return new HashSet<>(Arrays.asList(importConfiguration.getInput().split(",")));
		}
	}

	private ReleasePackageInformation getReleasePackageInformationNullable(String root) {
		if (root == null || root.isEmpty()) {
			return null;
		}

		try (Stream<Path> stream = Files.walk(Paths.get(root))) {
			Optional<ReleasePackageInformation> releasePackageInformation = stream
					.filter(Files::isRegularFile)
					.filter(path -> path.getFileName().toString().equals("release_package_information.json"))
					.findFirst()
					.map(path -> {
						try {
							ObjectMapper objectMapper = new ObjectMapper();
							return objectMapper.readValue(path.toFile(), ReleasePackageInformation.class);
						} catch (IOException e) {
							return null;
						}
					});
			return releasePackageInformation.orElse(null);
		} catch (Exception e) {
			return null;
		}
	}

	// Cannot depend on multiple versions of the same module
	private Map<String, Set<String>> getCompatibilityIssues() {
		Map<String, Set<ReferenceSetMember>> mdrs = dataStore.readReferenceSetMembersFromMDRSCache();
		if (mdrs.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<String, Set<String>> compatibilityIssues = new HashMap<>();
		for (Map.Entry<String, Set<ReferenceSetMember>> entrySet : mdrs.entrySet()) {
			ReferenceSetMember latest = entrySet.getValue().iterator().next();
			String referencedComponentId = latest.getReferencedComponentId();
			String targetEffectiveTime = latest.getOtherValues()[1];

			Set<String> targetEffectiveTimes = compatibilityIssues.get(referencedComponentId);
			if (targetEffectiveTimes == null) {
				targetEffectiveTimes = new HashSet<>();
			}

			targetEffectiveTimes.add(targetEffectiveTime);
			compatibilityIssues.put(referencedComponentId, targetEffectiveTimes);
		}

		// Not an issue if only one version found
		compatibilityIssues.entrySet().removeIf(entry -> entry.getValue().size() == 1);
		return compatibilityIssues;
	}
}