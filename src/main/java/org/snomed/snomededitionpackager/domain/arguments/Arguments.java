package org.snomed.snomededitionpackager.domain.arguments;

import org.snomed.snomededitionpackager.domain.command.Command;

import java.util.*;

public class Arguments {
	private static final String WILDCARD = "*";
	private static final String COMMA = ",";

	private final Command command;
	private final Map<String, String> args;

	public Arguments(Command command, Map<String, String> args) {
		this.command = command;
		this.args = args;
	}

	public Command getCommand() {
		return command;
	}

	public Map<String, String> getArgs() {
		return args;
	}

	public String getArg(String key) {
		if (args == null || args.isEmpty()) {
			return null;
		}

		return args.get(key);
	}

	public List<String> getArgList(String key) {
		return getArgList(key, COMMA);
	}

	public List<String> getArgList(String key, String separator) {
		if (args == null || args.isEmpty()) {
			return Collections.emptyList();
		}

		String value = args.get(key);
		if (value == null) {
			return Collections.emptyList();
		}

		return Arrays.asList(value.split(separator));
	}

	public String getArg(String key, String fallback) {
		if (args == null || args.isEmpty()) {
			return null;

		}

		String value = args.get(key);
		if (value == null || WILDCARD.equals(value)) {
			return fallback;
		}

		return value;
	}

	public boolean hasArgs() {
		return args != null && !args.isEmpty();
	}

	public boolean isWildcard(String argument) {
		if (args == null || args.isEmpty()) {
			return false;

		}

		String value = args.get(argument);
		if (value == null) {
			return false;

		}

		return WILDCARD.equals(value);
	}
}
