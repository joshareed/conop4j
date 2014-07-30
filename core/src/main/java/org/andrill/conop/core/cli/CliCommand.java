package org.andrill.conop.core.cli;

import java.util.List;

public interface CliCommand {

	/**
	 * Executes the command.
	 *
	 * @param args the args.
	 * @return true if the command succeeded, false otherwise.
	 */
	void execute(List<String> args);

	/**
	 * Gets the description of the command.
	 *
	 * @return the description.
	 */
	String getDescription();

	/**
	 * Gets the help text for the command.
	 *
	 * @return the help text.
	 */
	String getHelp();

	/**
	 * Gets the name of the command.
	 *
	 * @return the name.
	 */
	String getName();
}
