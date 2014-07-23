package org.andrill.conop.data.simulation

import org.andrill.conop.core.Run
import org.andrill.conop.core.solver.SolverConfiguration
import org.andrill.conop.data.simulation.internal.SimulationScript
import org.codehaus.groovy.control.CompilerConfiguration

class SimulationDSL {
	protected script

	void parse(String source) {
		def config = new CompilerConfiguration()
		config.scriptBaseClass = SimulationScript.class.canonicalName
		// add some security
		def classLoader = new GroovyClassLoader(this.class.classLoader, config)
		script = classLoader.parseClass(source).newInstance()
		script.run()
	}

	Run getRun() {
		script.run
	}

	SolverConfiguration getSolverConfiguration() {
		script.solverConfiguration
	}

	static main(args) {
		if (!args) {
			println "Usage: <simulation file>"
			System.exit(0)
		}

		try {
			def dsl = new SimulationDSL()
			dsl.parse(new File(args[0]).text)
			def run = dsl.run
			def config = dsl.solverConfiguration
			def solver = config.solver
			solver.solve(config, run)
		} catch (e) {
			println "Halted: ${e.message.padRight(80)}"
		}

		System.exit(0)
	}
}
