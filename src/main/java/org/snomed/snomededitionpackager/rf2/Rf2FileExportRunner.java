package org.snomed.snomededitionpackager.rf2;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.ihtsdo.snomed.util.rf2.schema.TableSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.snomededitionpackager.util.PackageInformationGenerator;
import org.snomed.snomededitionpackager.util.ReadmeGenerator;
import org.snomed.snomededitionpackager.util.ReleasePackageUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.snomed.snomededitionpackager.rf2.RF2Constants.*;

public class Rf2FileExportRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Rf2FileExportRunner.class);

    private record IdAndTerm(String id, String term) {
    }

    public final void generateEditionPackage(File configFile, Set<File> packages) throws IOException {
        validateInputPackages(packages);

        final File internationalPackage = findInternationalPackage(packages);
        if (internationalPackage == null) throw new IllegalArgumentException("International package must be provided.");
        final Set<File> extensionPackages = findExtensionsPackages(packages, internationalPackage);
        if (extensionPackages.isEmpty()) throw new IllegalArgumentException("Extension package must be provided.");

        String intEffectiveTime = ReleasePackageUtils.getReleaseDateFromReleasePackage(internationalPackage.getName());
        String extensionEffectiveTime = null;
        String extensionNamespace = null;
        boolean betaRelease = false;
        File inputDirectory = null;
        File outputDirectory = null;
        File releasePackageInformationFile = null;

        try {
            inputDirectory = createFolder(INPUT_FOLDER);
            outputDirectory = createFolder(OUTPUT_FOLDER);

            // Unzip international release
            File unzippedInternationalFolder = unzipPackage(internationalPackage, inputDirectory.getAbsolutePath() + SLASH + INTERNATIONAL);
            List<String> internationalFilesUsedByExtension = new ArrayList<>();

            // Walk through all extension packages
            for (File file : extensionPackages) {
                if (extensionEffectiveTime == null) {
                    extensionEffectiveTime = ReleasePackageUtils.getReleaseDateFromReleasePackage(file.getName());
                }
                File unzippedExtensionFolder = unzipPackage(file, inputDirectory.getAbsolutePath() + SLASH + EXTENSION + SLASH + file.getName());
                List<File> files = new ArrayList<>();
                listFiles(unzippedExtensionFolder.getAbsolutePath(), files);
                for (File thisFile : files) {
                    if (thisFile.isFile() && thisFile.getName().equals("release_package_information.json")) {
                        releasePackageInformationFile = thisFile;
                        continue;
                    }
                    if (!isValidRF2FullFile(thisFile)) {
                        continue;
                    }
                    if (extensionNamespace == null) {
                        extensionNamespace = ReleasePackageUtils.getNamespaceFromExtensionRf2File(thisFile.getName());
                        betaRelease = file.getName().startsWith(BETA_RELEASE_PREFIX);
                    }
                    combineRF2File(outputDirectory, thisFile, unzippedInternationalFolder, intEffectiveTime, extensionEffectiveTime, internationalFilesUsedByExtension);
                }
            }

            // Copy all INT Full files to the new edition if they are not being used by the extension
            copyMissingFilesFromInternationalPackage(outputDirectory, unzippedInternationalFolder, internationalFilesUsedByExtension, intEffectiveTime, extensionEffectiveTime, extensionNamespace, betaRelease);

            // Zip the new Edition package
            String releasePackageFilename = extensionPackages.size() == 1 ? extensionPackages.iterator().next().getName() : "Edition.zip";
            ZipFile releasePackage = zipPackage(outputDirectory, releasePackageFilename);

            generateReadmeAndReleaseInforFile(configFile, outputDirectory, extensionEffectiveTime, releasePackage, releasePackageFilename, releasePackageInformationFile);
            LOGGER.info("A new Edition package has been built completely. You can find it in {}", releasePackage.getFile().getAbsolutePath());
        } finally {
            cleanupTemporaryFolders(inputDirectory, outputDirectory);
        }
    }

    private void validateInputPackages(Set<File> packages) {
        if (packages.isEmpty()) throw new IllegalArgumentException("No release packages found.");

        for (File file : packages) {
            if (!file.getName().endsWith(ZIP_FILE_EXTENSION))
                throw new IllegalArgumentException(String.format("Package %s must be ended with zip extension.", file.getName()));
        }
    }

    private void generateReadmeAndReleaseInforFile(File configFile, File outputDirectory, String extensionEffectiveTime, ZipFile releasePackage, String releasePackageFilename, File releaseInforFile) throws IOException {
        // Add Readme and Release package information files
        if (configFile != null) {
            Map<String, Object> config = readConfigFile(configFile);
            String releaseInforFilename = generateReleaseInformation(extensionEffectiveTime, outputDirectory, config, releasePackage, releasePackageFilename, releaseInforFile);
            generateReadme(outputDirectory, config, extensionEffectiveTime, releasePackage, releasePackageFilename, releaseInforFilename);
        }
    }

    private void combineRF2File(File output, File extensionRf2File, File internationalFile, String internationalEffectiveTime, String extensionEffectiveTime, List<String> internationalFilesUsedByExtension) {
        try (RF2TableExportDAO rf2TableDAO = new RF2TableExportDAOImpl()) {
            TableSchema tableSchema = rf2TableDAO.createTable(extensionRf2File.getName(), new FileInputStream(extensionRf2File));

            // Populate data from International Full
            File intFullFile = getEquivalentInternationalFull(extensionRf2File.getName(), internationalFile, internationalEffectiveTime);
            if (intFullFile != null) {
                internationalFilesUsedByExtension.add(intFullFile.getName());
                try (InputStream intFullStream = new FileInputStream(intFullFile);) {
                    rf2TableDAO.appendData(tableSchema, intFullStream);
                }
            } else {
                //  RefSet files specific to extensions will not have equivalent files in the international release.
                LOGGER.info("No equivalent full file found in dependency package for {}", extensionRf2File.getName());
            }

            // Export ordered Snapshot and Full files
            exportFullAndSnapshot(output, extensionRf2File.getAbsolutePath(), extensionRf2File.getName(), extensionEffectiveTime, rf2TableDAO, tableSchema);
        } catch (final Exception e) {
            final String errorMsg = "Failed to generate subsequent full and snapshot release files due to: " + ExceptionUtils.getRootCauseMessage(e);
            LOGGER.error(errorMsg);
        }
    }

    private void copyMissingFilesFromInternationalPackage(File output, File internationalPackage, List<String> internationalFilesUsedByExtension, String intEffectiveTime, String extensionEffectiveTime, String extensionNamespace, boolean betaRelease) {
        List<File> allIntFiles = new ArrayList<>();
        listFiles(internationalPackage.getAbsolutePath(), allIntFiles);
        List<File> missingFullFiles = allIntFiles.stream().filter(file -> isValidRF2FullFile(file) && !internationalFilesUsedByExtension.contains(file.getName())).toList();
        for (File file : missingFullFiles) {
            LOGGER.info("Copying the missing INT full file to the new Edition: {}", file.getName());
            try (RF2TableExportDAO rf2TableDAO = new RF2TableExportDAOImpl()) {
                TableSchema tableSchema = rf2TableDAO.createTable(file.getName(), new FileInputStream(file));
                String fullFileName = file.getName().replace(INT_NAMESPACE, FILE_NAME_SEPARATOR + extensionNamespace + FILE_NAME_SEPARATOR).replace(intEffectiveTime, extensionEffectiveTime);
                if (betaRelease) {
                    fullFileName = BETA_RELEASE_PREFIX + fullFileName;
                }
                // Export ordered Snapshot and Full files
                exportFullAndSnapshot(output, file.getAbsolutePath(), fullFileName, extensionEffectiveTime, rf2TableDAO, tableSchema);
            } catch (final Exception e) {
                final String errorMsg = "Failed to generate subsequent full and snapshot release files due to: " + ExceptionUtils.getRootCauseMessage(e);
                LOGGER.error(errorMsg);
            }
        }
    }

    private void exportFullAndSnapshot(File output, String rf2FilePath, String fullFileName, String extensionEffectiveTime, RF2TableExportDAO rf2TableDAO, TableSchema tableSchema) throws IOException {
        final Rf2FileWriter rf2FileWriter = new Rf2FileWriter();
        final RF2TableResults fullResultSet = rf2TableDAO.selectAllOrdered(tableSchema);
        final String currentSnapshotFileName = constructSnapshotFilename(fullFileName);
        String sourcePath = SLASH + getSourcePath(rf2FilePath);

        File outputRF2FullFolder = new File(output.getAbsoluteFile() + SLASH + FULL + sourcePath);
        if (!outputRF2FullFolder.exists()) {
            FileUtils.forceMkdir(outputRF2FullFolder); //create directory
        }
        File outputRF2SnapshotFolder = new File(output.getAbsoluteFile() + SLASH + SNAPSHOT + sourcePath);
        if (!outputRF2SnapshotFolder.exists()) {
            FileUtils.forceMkdir(outputRF2SnapshotFolder); //create directory
        }
        try (OutputStream fullFileOutputStream = new FileOutputStream(outputRF2FullFolder + SLASH + fullFileName);
             OutputStream snapshotFileOutputStream = new FileOutputStream(outputRF2SnapshotFolder + SLASH + currentSnapshotFileName)) {
            rf2FileWriter.exportFullAndSnapshot(fullResultSet, tableSchema, extensionEffectiveTime, fullFileOutputStream, snapshotFileOutputStream);
            LOGGER.debug("Completed processing full and snapshot files for {}", tableSchema.getTableName());
        }
    }

    private File getEquivalentInternationalFull(String extensionFilename, File internationalPackage, String internationalEffectiveTime) {
        String equivalentFullFile = getEquivalentInternationalFile(extensionFilename, internationalEffectiveTime);
        LOGGER.info("Equivalent full file {}", equivalentFullFile);
        List<File> files = new ArrayList<>();
        listFiles(internationalPackage.getAbsolutePath(), files);
        return files.stream().filter(file -> file.isFile() && file.getName().equals(equivalentFullFile)).findFirst().orElse(null);
    }

    private String getEquivalentInternationalFile(String extensionFilename, String internationalEffectiveTime) {
        if (extensionFilename.startsWith(BETA_RELEASE_PREFIX)) {
            extensionFilename = extensionFilename.replaceFirst(BETA_RELEASE_PREFIX, "");
        }
        String[] splits = extensionFilename.split(FILE_NAME_SEPARATOR);
        StringBuilder equivalentBuilder = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            equivalentBuilder.append(splits[i]);
            equivalentBuilder.append(FILE_NAME_SEPARATOR);
        }
        equivalentBuilder.append(INT);
        equivalentBuilder.append(FILE_NAME_SEPARATOR);
        equivalentBuilder.append(internationalEffectiveTime);
        equivalentBuilder.append(TXT_FILE_EXTENSION);
        LOGGER.info("The equivalent file for {} in international package is {}", extensionFilename, equivalentBuilder);
        return equivalentBuilder.toString();
    }

    private File createFolder(String folderName) throws IOException {
        File folder = (new File(folderName));
        if (folder.exists()) {
            FileUtils.cleanDirectory(folder); //clean out directory
            FileUtils.forceDelete(folder); //delete directory
        }
        FileUtils.forceMkdir(folder); //create directory
        return folder;
    }

    private void cleanupTemporaryFolders(File inputDirectory, File outputDirectory) throws IOException {
        if (inputDirectory != null) {
            FileUtils.forceDelete(inputDirectory);
        }
        // Delete the folders inside the output
        if (outputDirectory != null) {
            File outputFull = new File(outputDirectory + SLASH + FULL);
            if (outputFull.exists()) FileUtils.forceDelete(new File(outputDirectory + SLASH + FULL));
            File outputSnapshot = new File(outputDirectory + SLASH + SNAPSHOT);
            if (outputSnapshot.exists()) FileUtils.forceDelete(new File(outputDirectory + SLASH + SNAPSHOT));
        }
    }

    private String getSourcePath(String absolutePath) {
        if (System.getProperty("os.name").contains("Windows")) {
            absolutePath = FilenameUtils.separatorsToUnix(absolutePath);
        }
        if (absolutePath.contains(TERMINOLOGY_SOURCE)) {
            return absolutePath.substring(absolutePath.indexOf(TERMINOLOGY_SOURCE), absolutePath.lastIndexOf(SLASH));
        } else if (absolutePath.contains(REFSET_SOURCE)) {
            return absolutePath.substring(absolutePath.indexOf(REFSET_SOURCE), absolutePath.lastIndexOf(SLASH));
        } else {
            LOGGER.warn("No source file found.");
            return "";
        }
    }

    private boolean isValidRF2FullFile(File thisFile) {
        return !thisFile.isDirectory()
                && thisFile.getName().endsWith(TXT_FILE_EXTENSION)
                && (thisFile.getName().contains(FULL + FILE_NAME_SEPARATOR) || thisFile.getName().contains(FULL + DASH));
    }

    private String constructSnapshotFilename(String fullFilename) {
        return fullFilename.replace(FULL + FILE_NAME_SEPARATOR, SNAPSHOT + FILE_NAME_SEPARATOR)
                .replace(FULL + DASH, SNAPSHOT + DASH);
    }

    private File findInternationalPackage(Set<File> packages) {
        return packages.stream().filter(file -> file.isFile() && file.getName().contains("InternationalRF2") && file.getName().endsWith(ZIP_FILE_EXTENSION)).findFirst().orElse(null);
    }

    private Set<File> findExtensionsPackages(Set<File> packages, File internationalPackage) {
        return packages.stream().filter(file -> file.isFile() && (!file.getName().equals(internationalPackage.getName()))).collect(Collectors.toSet());
    }

    private File unzipPackage(File source, String destination) throws IOException {
        LOGGER.info("Unzipping the package {}...", source.getName());
        File unzippedFolder = new File(destination);
        try (ZipFile zipFile = new ZipFile(source)) {
            zipFile.extractAll(unzippedFolder.getAbsolutePath());
        } catch (ZipException e) {
            throw new IOException("Unable to unzip the file " + source.getName());
        }
        return unzippedFolder;
    }

    private ZipFile zipPackage(File outputDirectory, String zipFilename) throws IOException {
        LOGGER.info("Zipping the new edition {}...", zipFilename);
        try (ZipFile zipFile = new ZipFile(outputDirectory + SLASH + zipFilename)) {
            ZipParameters parameters = new ZipParameters();
            parameters.setRootFolderNameInZip(zipFilename.replace(ZIP_FILE_EXTENSION, ""));
            zipFile.addFolder(new File(outputDirectory + SLASH + FULL), parameters);
            zipFile.addFolder(new File(outputDirectory + SLASH + SNAPSHOT), parameters);
            return zipFile;
        }
    }

    private void listFiles(String directoryName, List<File> files) {
        File directory = new File(directoryName);

        // Get all files from a directory.
        File[] fList = directory.listFiles();
        if (fList != null) {
            for (File file : fList) {
                if (file.isFile()) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    listFiles(file.getAbsolutePath(), files);
                }
            }
        }
    }

    private Map<String, Object> readConfigFile(File configFile) throws IOException {
        return new ObjectMapper().readValue(configFile, LinkedHashMap.class);
    }

    private void generateReadme(File outputDirectory, Map<String, Object> config, String effectiveTime, ZipFile releasePackage, String zipFilename, String releaseInforFilename) throws IOException {
        Map<String, Object> readmeConfig = (HashMap) config.get("readmeConfig");
        String readmeFilename = readmeConfig.get("filename").toString().replace("{effectiveTime}", effectiveTime);

        String readmeFilePath = outputDirectory + SLASH + readmeFilename;
        try (OutputStream readmeOutputStream = new FileOutputStream(readmeFilePath)) {
            ReadmeGenerator.generate(readmeFilename, releaseInforFilename == null ? "" : releaseInforFilename, readmeConfig.get("header").toString(), readmeConfig.get("endDate").toString(), new java.util.zip.ZipFile(releasePackage.getFile()), readmeOutputStream);

            ZipParameters parameters = new ZipParameters();
            parameters.setRootFolderNameInZip(zipFilename.replace(ZIP_FILE_EXTENSION, ""));
            releasePackage.addFile(new File(readmeFilePath), parameters);
        } finally {
            File readmeFile = new File(readmeFilePath);
            if (readmeFile.exists()) {
                FileUtils.forceDelete(readmeFile);
            }
        }
    }

    private String generateReleaseInformation(String effectiveTime, File outputDirectory, Map<String, Object> config, ZipFile releasePackage, String zipFilename, File releaseInforFile) throws IOException {
        Map<String, Object> releaseInformationConfig = (LinkedHashMap) config.get("releaseInformationConfig");
        if (releaseInformationConfig != null && !releaseInformationConfig.isEmpty()) {
            String releaseInformationFilePath = outputDirectory + SLASH + releaseInformationConfig.get("filename");
            try (OutputStream releaseInformationOutputStream = new FileOutputStream(releaseInformationFilePath)) {
                Map fields = (LinkedHashMap) releaseInformationConfig.get("fields");
                PackageInformationGenerator.generate(effectiveTime, fields, releaseInformationOutputStream);
                ZipParameters parameters = new ZipParameters();
                parameters.setRootFolderNameInZip(zipFilename.replace(ZIP_FILE_EXTENSION, ""));
                releasePackage.addFile(new File(releaseInformationFilePath), parameters);
                return releaseInformationConfig.get("filename").toString();
            } finally {
                File releaseInformationFile = new File(releaseInformationFilePath);
                if (releaseInformationFile.exists()) {
                    FileUtils.forceDelete(releaseInformationFile);
                }
            }
        } else if (releaseInforFile != null) {
            ZipParameters parameters = new ZipParameters();
            parameters.setRootFolderNameInZip(zipFilename.replace(ZIP_FILE_EXTENSION, ""));
            releasePackage.addFile(releaseInforFile, parameters);
            return releaseInforFile.getName();
        } else {
            LOGGER.info("The release information file could not be found in the extension package or configured yet.");
            return null;
        }
    }
}
