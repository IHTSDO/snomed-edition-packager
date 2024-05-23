package org.snomed.snomededitionpackager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class PackageCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(PackageCommand.class);

    @ShellMethod(key = "package", value = "Create package by joining/merging given packages.")
    public void createPackage(@ShellOption(defaultValue = "*") String argument) {
        LOGGER.info("Running 'package' with argument '{}'", argument);
    }
}
