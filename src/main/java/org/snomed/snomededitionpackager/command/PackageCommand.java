package org.snomed.snomededitionpackager.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.snomededitionpackager.domain.arguments.Arguments;
import org.snomed.snomededitionpackager.domain.command.Command;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.*;

@ShellComponent
public class PackageCommand {
	private static final Logger LOGGER = LoggerFactory.getLogger(PackageCommand.class);
	private final PackageHandler packageHandler;

	public PackageCommand(PackageHandler packageHandler) {
		this.packageHandler = packageHandler;
	}

	@ShellMethod(key = "package", value = "Create package by joining/merging given packages.")
	public void createPackage(
			@ShellOption(defaultValue = "*") String shortName,
			@ShellOption(defaultValue = "*") String input,
			@ShellOption(defaultValue = "*") String output,
			@ShellOption(defaultValue = "*") String effectiveTime,
			@ShellOption(defaultValue = "false") String full,
			@ShellOption(defaultValue = "*") String releasePackageInformation,
			@ShellOption(defaultValue = "false") String sort
	) {
		long start = System.currentTimeMillis();
		LOGGER.info("Creating package... shortName={} input={} output={} effectiveTime={} full={} releasePackageInformation={} sort={}", shortName, input, output, effectiveTime, full, releasePackageInformation, sort);
		boolean success = packageHandler.combine(new Arguments(Command.PACKAGE,
				Map.of("shortName", shortName,
						"input", input,
						"output", output,
						"effectiveTime", effectiveTime,
						"full", full,
						"releasePackageInformation", releasePackageInformation,
						"sort", sort
				))
		);

		long end = System.currentTimeMillis();
		long total = (end - start) / 1_000;
		if (!success) {
			LOGGER.error("Failed to create package after {} seconds.", total);
		} else {
			LOGGER.info("Package successfully created after {} seconds.", total);
		}
	}
}