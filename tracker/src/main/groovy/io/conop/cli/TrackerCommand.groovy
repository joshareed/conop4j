package io.conop.cli

import org.andrill.conop.core.cli.CliCommand

import ratpack.groovy.launch.GroovyRatpackMain

class TrackerCommand implements CliCommand {

	@Override
	void execute(List<String> args) {
		GroovyRatpackMain.main(args.toArray(new String[0]))
		while (true) {
			Thread.sleep(60 * 60 * 1000);
		}
	}

	@Override
	String getDescription() {
		"runs a web interface for distributing and tracking CONOP4J simulations"
	}

	@Override
	String getHelp() {
		"\ttracker [db file] - runs a web interface for distributing and tracking CONOP4J simulations"
	}

	@Override
	String getName() {
		"tracker"
	}

}
