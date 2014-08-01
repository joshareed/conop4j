package org.andrill.conop.data.simulation

import org.andrill.conop.core.Dataset
import org.andrill.conop.core.solver.SolverConfiguration
import org.andrill.conop.data.simulation.internal.SimulationScript
import org.codehaus.groovy.control.CompilerConfiguration

class SimulationDSL {
	protected script

	void parse(String source) {
		def config = new CompilerConfiguration()
		config.scriptBaseClass = SimulationScript.class.canonicalName
		// TODO: add some security
		def classLoader = new GroovyClassLoader(this.class.classLoader, config)
		script = classLoader.parseClass(source).newInstance()
		script.run()
	}

	Dataset getDataset() {
		script.dataset
	}

	SolverConfiguration getSolverConfiguration() {
		script.solverConfiguration
	}
}
