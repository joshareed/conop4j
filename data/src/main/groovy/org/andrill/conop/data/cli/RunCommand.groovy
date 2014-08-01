package org.andrill.conop.data.cli

import org.andrill.conop.core.cli.CliCommand
import org.andrill.conop.data.simulation.SimulationDSL

class RunCommand implements CliCommand {

	@Override
	void execute(List<String> args) {
		if (!args) {
			println "Usage: run <simulation file or URL>"
			System.exit(0)
		}

		try {
			def dsl = new SimulationDSL()
			
			// check if URL or file
			def source
			if (args[0].contains('http')) {
				source = new URL(args[0]).text
			} else {
				source = new File(args[0]).text
			}
			dsl.parse(source)

			def run = dsl.run
			def config = dsl.solverConfiguration
			def solver = config.solver
			solver.solve(config, run)
		} catch (e) {
			println "Halted: ${e.message.padRight(80)}"
		}

		System.exit(0)
	}

	@Override
	String getDescription() {
		"runs a CONOP4J simulation"
	}

	@Override
	String getHelp() {
		"""\trun <simulation file or URL> - runs a CONOP4J simulation"""
	}

	@Override
	String getName() {
		"run"
	}

}
