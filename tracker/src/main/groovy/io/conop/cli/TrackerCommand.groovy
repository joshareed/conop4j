package io.conop.cli

import org.andrill.conop.core.cli.CliCommand

import ratpack.groovy.launch.GroovyRatpackMain

class TrackerCommand implements CliCommand {

	@Override
	void execute(List<String> args) {
		if (args) {
			// assume it is the public address so set it
			String address = args[0]
			System.setProperty("ratpack.publicAddress", address)
			args.remove(0i)
		}

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
		"\ttracker [public address] - runs a web interface for distributing and tracking CONOP4J simulations"
	}

	@Override
	String getName() {
		"tracker"
	}

}
