package org.snomed.snomededitionpackager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.snomededitionpackager.rf2.RF2Constants;
import org.snomed.snomededitionpackager.rf2.Rf2FileExportRunner;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.StringUtils;
import us.monoid.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@ShellComponent
public class PackageCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(PackageCommand.class);

    @ShellMethod(key = "package", value = "Create package by joining/merging given packages.")
    public void createPackage(@ShellOption(defaultValue = "*") String... arguments) throws IOException, JSONException {
        LOGGER.info("Running 'package' with argument '{}'", (arguments.length != 0 ? String.join(", ", arguments) : ""));
        Set<String> filenames = getFilenames(arguments);
        Set<File> files = new HashSet<>();
        filenames.forEach(filename -> files.add(new File(filename)));
        File configFile = getConfigFile(arguments);

        Rf2FileExportRunner exportRunner = new Rf2FileExportRunner();
        exportRunner.generateEditionPackage(configFile, files);
        System.exit(0);
    }

    private Set<String> getFilenames(String... arguments) {
        Set<String> filenames = new HashSet<>();

        if (arguments.length == 1 && "*".equals(arguments[0])) {
            File dir = new File(".");
            File[] filesList = dir.listFiles();
            assert filesList != null;
            for (File file : filesList) {
                if (file.isFile() && validSnomedRelease(file.getName())) {
                    filenames.add(file.getName());
                }
            }
        } else {
            filenames = Arrays.stream(arguments).filter(this::validSnomedRelease).collect(Collectors.toSet());
        }
        return filenames;
    }

    private boolean validSnomedRelease(String filename) {
        return (filename.startsWith(RF2Constants.SNOMEDCT) || filename.startsWith(RF2Constants.BETA_RELEASE_PREFIX + RF2Constants.SNOMEDCT))
                && filename.endsWith(RF2Constants.ZIP_FILE_EXTENSION);
    }

    private File getConfigFile(String... arguments) {
        if (arguments.length == 1 && "*".equals(arguments[0])) {
            File dir = new File(".");
            File[] filesList = dir.listFiles();
            assert filesList != null;
            for (File file : filesList) {
                if (file.isFile() && file.getName().equals("config.json")) {
                    return file;
                }
            }
        } else {
            String configFilename = Arrays.stream(arguments).filter(filename -> filename.equals("config.json")).findFirst().orElse(null);
            return configFilename == null ? null : new File(configFilename);
        }
        return null;
    }

}
