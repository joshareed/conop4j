package org.andrill.conop.core.cli;

import java.util.List;

public class HelpCommand implements CliCommand {

	@Override
	public void execute(final List<String> args) {
		if (args.size() == 0) {
			printAvailableCommands();
			return;
		}

		String name = args.get(0);
		CliCommand cmd = null;
		for (CliCommand c : Conop4jMain.getCommands()) {
			if (name.equals(c.getName())) {
				cmd = c;
				break;
			}
		}

		if (cmd == null) {
			System.out.println("Unknown command: " + name);
			return;
		}

		System.out.println("NAME");
		System.out.println("\t" + name);
		System.out.println("");
		System.out.println("SYNOPSIS");
		System.out.println("\t" + cmd.getDescription());
		System.out.println("");
		System.out.println("DESCRIPTION");
		System.out.println(cmd.getHelp());
		System.out.println("");
	}

	@Override
	public String getDescription() {
		return "displays help info for a command";
	}

	@Override
	public String getHelp() {
		return "\thelp <command> - displays all help info for <command>";
	}

	@Override
	public String getName() {
		return "help";
	}

	protected void printAvailableCommands() {
		String version = Conop4jMain.getVersion();
		List<? extends CliCommand> commands = Conop4jMain.getCommands();
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
		return;
	}

}
