package io.conop.cli

import io.conop.Agent

import org.andrill.conop.core.cli.CliCommand

class AgentCommand implements CliCommand {

	@Override
	void execute(List<String> args) {
		if (!args) {
			println "Usage: agent <url> [name]"
			System.exit(0)
		}

		def url = (args[0] - 'api/jobs')
		def name = (args.size() > 1 ? args[1] : InetAddress.localHost.hostName)

		def agent = new Agent(url, name)
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
