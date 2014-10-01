package org.andrill.conop.core.cli;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class Conop4jMain {
	private static final Logger log = LoggerFactory.getLogger(Conop4jMain.class);

	private static final String[] COMMANDS = { "org.andrill.conop.core.cli.HelpCommand",
			"org.andrill.conop.data.cli.RunCommand", "io.conop.cli.AgentCommand", "io.conop.cli.TrackerCommand",
			"org.andrill.conop.analysis.charts.RangeChartCommand",
			"org.andrill.conop.analysis.charts.CrossPlotChartCommand" };

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
				log.error("Foo", e);
			} catch (InstantiationException e) {
				// ignore
				log.error("Foo", e);
			} catch (IllegalAccessException e) {
				// ignore
				log.error("Foo", e);
			}
		}

		return commands;
	}

	protected static String getVersion() {
		return "0.12.1";
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
