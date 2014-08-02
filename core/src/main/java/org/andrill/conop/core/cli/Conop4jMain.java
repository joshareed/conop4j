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
		return "0.11.0-SNAPSHOT";
	}

	public static void main(final String[] args) {
		String name = null;
		if ((args == null) || (args.length == 0)) {
			name = "help";
		} else {
			name = args[0];
		}

		List<String> params = Lists.newArrayList();
		for (int i = 1; i < args.length; i++) {
			params.add(args[i]);
		}
		execute(name, params);
	}
}
