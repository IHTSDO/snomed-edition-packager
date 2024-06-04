package org.snomed.snomededitionpackager.rf2;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.ihtsdo.snomed.util.rf2.schema.TableSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.snomededitionpackager.util.ReleasePackageUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.snomed.snomededitionpackager.rf2.RF2Constants.*;
public class Rf2FileExportRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Rf2FileExportRunner.class);

    public final void generateEditionPackage(Set<File> packages) throws IOException {
        if (packages.isEmpty()) throw new IllegalArgumentException("No release packages found.");

        for (File file : packages) {
            if (!file.getName().endsWith(ZIP_FILE_EXTENSION))
                throw new IllegalArgumentException(String.format("Package %s must be ended with zip extension.", file.getName()));
        }
        final File internationalPackage = findInternationalPackage(packages);
        if (internationalPackage == null) throw new IllegalArgumentException("International package must be provided.");
        String internationalEffectiveTime = ReleasePackageUtils.getReleaseDateFromReleasePackage(internationalPackage.getName());
        File inputDirectory = null;
        try {
            inputDirectory = createFolder(INPUT_FOLDER);
            File outputDirectory = createFolder(OUTPUT_FOLDER);

            // Unzip international release
            File unzippedInternationalFolder = new File(inputDirectory.getAbsolutePath() + SLASH + INTERNATIONAL);
            unzipPackage(internationalPackage, unzippedInternationalFolder.getAbsolutePath());

            // Walk through all extension packages
            final Set<File> extensionPackages = findExtensionsPackages(packages, internationalPackage);
            assert !extensionPackages.isEmpty();

            for (File file : extensionPackages) {
                String extensionEffectiveTime = ReleasePackageUtils.getReleaseDateFromReleasePackage(file.getName());
                File unzippedFolder = new File(inputDirectory.getAbsolutePath() + SLASH + EXTENSION + SLASH + file.getName());
                unzipPackage(file, unzippedFolder.getAbsolutePath());
                List<File> files = new ArrayList<>();
                listFiles(unzippedFolder.getAbsolutePath(), files);
                for (File thisFile : files) {
                    if (!isValidRF2FullFile(thisFile)) {
                        continue;
                    }
                    combineRF2File(outputDirectory, thisFile, unzippedInternationalFolder, internationalEffectiveTime, extensionEffectiveTime);
                }
            }
            // Zip the new Edition package
            String zipFilename = extensionPackages.size() == 1 ? extensionPackages.iterator().next().getName() : "Edition.zip";
            zipPackage(outputDirectory, zipFilename);

            LOGGER.info("A new Edition package has been built completely. You can find it in {}", outputDirectory.getAbsolutePath());

            // Delete the folders inside the output
            FileUtils.forceDelete(new File(outputDirectory + SLASH + FULL));
            FileUtils.forceDelete(new File(outputDirectory + SLASH + SNAPSHOT));
        } finally {
            if (inputDirectory != null) {
                FileUtils.forceDelete(inputDirectory);
            }
        }
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

    private static boolean isValidRF2FullFile(File thisFile) {
        return !thisFile.isDirectory()
                && thisFile.getName().endsWith(TXT_FILE_EXTENSION)
                && (thisFile.getName().contains(FULL + FILE_NAME_SEPARATOR) || thisFile.getName().contains(FULL + DASH));
    }

    private void combineRF2File(File output, File extensionRf2File, File internationalFile, String internationalEffectiveTime, String extensionEffectiveTime) {
        RF2TableExportDAO rf2TableDAO = null;
        TableSchema tableSchema = null;
        try {
            // Create table containing transformed input delta
            rf2TableDAO = new RF2TableExportDAOImpl();
            tableSchema = rf2TableDAO.createTable(extensionRf2File.getName(), new FileInputStream(extensionRf2File));

            // Populate data from International Full
            try(InputStream intFullStream = getEquivalentInternationalFull(extensionRf2File.getName(), internationalFile, internationalEffectiveTime)) {
                if (intFullStream != null) {
                    rf2TableDAO.appendData(tableSchema, intFullStream);
                } else {
                    //  RefSet files specific to extensions will not have equivalent files in the international release.
                    LOGGER.info("No equivalent full file found in dependency package for {}", extensionRf2File.getName());
                }
            }

            // Export ordered Snapshot and Full files
            final Rf2FileWriter rf2FileWriter = new Rf2FileWriter();
            final RF2TableResults fullResultSet = rf2TableDAO.selectAllOrdered(tableSchema);
            final String currentSnapshotFileName = constructSnapshotFilename(extensionRf2File.getName());
            final String currentFullFileName = extensionRf2File.getName();
            String sourcePath = SLASH + getSourcePath(extensionRf2File.getAbsolutePath());

            File outputRF2FullFolder = new File(output.getAbsoluteFile() + SLASH + FULL + sourcePath);
            if (!outputRF2FullFolder.exists()) {
                FileUtils.forceMkdir(outputRF2FullFolder); //create directory
            }
            File outputRF2SnapshotFolder = new File(output.getAbsoluteFile() + SLASH + SNAPSHOT + sourcePath);
            if (!outputRF2SnapshotFolder.exists()) {
                FileUtils.forceMkdir(outputRF2SnapshotFolder); //create directory
            }
            try (OutputStream fullFileOutputStream = new FileOutputStream(outputRF2FullFolder + SLASH + currentFullFileName);
                 OutputStream snapshotFileOutputStream = new FileOutputStream(outputRF2SnapshotFolder + SLASH + currentSnapshotFileName)) {
                rf2FileWriter.exportFullAndSnapshot(fullResultSet, tableSchema, extensionEffectiveTime, fullFileOutputStream, snapshotFileOutputStream);
                LOGGER.debug("Completed processing full and snapshot files for {}", tableSchema.getTableName());
            }
        } catch (final Exception e) {
            final String errorMsg = "Failed to generate subsequent full and snapshot release files due to: " + ExceptionUtils.getRootCauseMessage(e);
            LOGGER.error(errorMsg);
        } finally {
            // Clean up time
            if (rf2TableDAO != null) {
                try {
                    rf2TableDAO.closeConnection();
                } catch (final Exception e) {
                    LOGGER.error("Failure while trying to clean up after {}", tableSchema != null ? tableSchema.getTableName() : "No table yet.", e);
                }
            }
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

    private InputStream getEquivalentInternationalFull(String extensionFilename, File internationalPackage, String internationalEffectiveTime) throws IOException {
        String equivalentFullFile = getEquivalentInternationalFile(extensionFilename, internationalEffectiveTime);
        LOGGER.info("Equivalent full file {}", equivalentFullFile);
        List<File> files = new ArrayList<>();
        listFiles(internationalPackage.getAbsolutePath(), files);
        File intFullFile = files.stream().filter(file -> file.isFile() && file.getName().equals(equivalentFullFile)).findFirst().orElse(null);
        if (intFullFile != null) {
            return new FileInputStream(intFullFile);
        }
        return null;
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

    private String constructSnapshotFilename(String fullFilename) {
        return fullFilename.replace(FULL + FILE_NAME_SEPARATOR, SNAPSHOT + FILE_NAME_SEPARATOR)
                .replace( FULL + DASH, SNAPSHOT + DASH);
    }

    private File findInternationalPackage(Set<File> packages) {
        return packages.stream().filter(file -> file.isFile() && file.getName().contains("InternationalRF2") && file.getName().endsWith(ZIP_FILE_EXTENSION)).findFirst().orElse(null);
    }

    private Set<File> findExtensionsPackages(Set<File> packages, File internationalPackage) {
        return packages.stream().filter(file -> file.isFile() && (!file.getName().equals(internationalPackage.getName()))).collect(Collectors.toSet());
    }

    private void unzipPackage(File source, String destination) throws IOException {
        LOGGER.info("Unzipping package {}...", source.getName());
        try (ZipFile zipFile = new ZipFile(source)) {
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            throw new IOException("Unable to unzip the file " + source.getName());
        }
    }

    private void zipPackage(File outputDirectory, String zipFilename) throws IOException {
        LOGGER.info("Zipping the new edition {}...",zipFilename);
        try(ZipFile zipFile = new ZipFile(outputDirectory + SLASH + zipFilename)) {
            ZipParameters parameters = new ZipParameters();
            parameters.setRootFolderNameInZip(zipFilename.replace(ZIP_FILE_EXTENSION, ""));
            zipFile.addFolder(new File(outputDirectory + SLASH + FULL), parameters);
            zipFile.addFolder(new File(outputDirectory + SLASH + SNAPSHOT), parameters);
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
}
