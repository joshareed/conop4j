package io.conop.cli

import io.conop.Agent;

import org.andrill.conop.core.cli.CliCommand

class AgentCommand implements CliCommand {

	@Override
	void execute(List<String> args) {
		if (!args) {
			println "Usage: agent <url>"
			System.exit(0)
		}

		def agent = new Agent(args[0])
		agent.run()
	}

	@Override
	String getDescription() {
		"starts a computation agent"
	}

	@Override
	String getHelp() {
		"""\tagent <url> - starts a computation agent that fetches and runs jobs from the specified tracker"""
	}

	@Override
	String getName() {
		"agent"
	}

}
