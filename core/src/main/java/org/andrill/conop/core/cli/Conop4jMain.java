package org.andrill.conop.core.cli;

import java.util.List;

import com.google.common.collect.Lists;

public class Conop4jMain {

	private static final String[] COMMANDS = {
		"org.andrill.conop.core.cli.HelpCommand",
		"org.andrill.conop.data.cli.RunCommand",
		"io.conop.cli.AgentCommand",
		"io.conop.cli.TrackerCommand"
	};

	protected static void execute(final String name, final List<String> args) {
		CliCommand cmd = null;
		for (CliCommand c : getCommands()) {
			if (name.equals(c.getName())) {
				cmd = c;
				break;
			}
		}

		if (cmd == null) {
			System.out.println("Unknown command: " + name);
		} else {
			cmd.execute(args);
		}
		System.exit(0);
	}

	@SuppressWarnings("unchecked")
	public static List<CliCommand> getCommands() {
		List<CliCommand> commands = Lists.newArrayList();

		for (String className : COMMANDS) {
			try {
				Class<? extends CliCommand> clazz = (Class<? extends CliCommand>) Class.forName(className);
				commands.add(clazz.newInstance());
			} catch (ClassNotFoundException e) {
				// ignore
			} catch (InstantiationException e) {
				// ignore
			} catch (IllegalAccessException e) {
				// ignore
			}
		}

		return commands;
	}

	protected static String getVersion() {
		return "0.10.3";
	}

	public static void main(final String[] args) {
		if ((args == null) || (args.length == 0)) {
			printAvailableCommands();
		}

		String name = args[0];
		List<String> params = Lists.newArrayList();
		for (int i = 1; i < args.length; i++) {
			params.add(args[i]);
		}
		execute(name, params);
	}

	protected static void printAvailableCommands() {
		String version = getVersion();
		List<? extends CliCommand> commands = getCommands();
		System.out.println("");
		System.out.println("CONOP4J - " + version);
		System.out.println("");
		System.out.println("Usage: java -jar conop4j-all-" + version + ".jar <command> [args...]");
		System.out.println("");
		System.out.println("Available commands:");
		for (CliCommand cmd : commands) {
			System.out.println(String.format("       %-10s %s", cmd.getName(), cmd.getDescription()));
		}
		System.out.println("");
		System.exit(0);
	}
}
