package org.snomed.snomededitionpackager.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.snomededitionpackager.domain.arguments.Arguments;
import org.snomed.snomededitionpackager.domain.command.Command;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.Map;

@ShellComponent
public class ReportCommand {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportCommand.class);

	private final ReportHandler reportHandler;

	public ReportCommand(ReportHandler reportHandler) {
		this.reportHandler = reportHandler;
	}

	@ShellMethod(key = "report", value = "Create report on given packages.")
	public void createReport(@ShellOption(defaultValue = "*") String input) {
		long start = System.currentTimeMillis();
		LOGGER.info("Creating report... input={}", input);
		boolean success = reportHandler.createReport(new Arguments(Command.REPORT, Map.of("input", input)));

		long end = System.currentTimeMillis();
		long total = (end - start) / 1_000;
		if (!success) {
			LOGGER.error("Failed to create report after {} seconds.", total);
		} else {
			LOGGER.info("Report successfully created after {} seconds.", total);
		}
	}
}
