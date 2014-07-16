package org.andrill.conop.data.simulation.internal

import org.andrill.conop.core.Run
import org.andrill.conop.core.solver.SolverConfiguration

class SimulationScript extends Script {
	def repos = []
	def locs = []
	def config

	def run() {
		// do nothing
	}

	Run getRun() {
		return null
	}

	SolverConfiguration getSolverConfiguration() {
		return config
	}

	protected repositories(Closure closure) {
		// setup our delegate
		def delegate = new RepositoriesDelegate()
		closure.delegate = delegate
		closure()

		// add all parsed repositories
		repos.addAll(delegate.repositories)
	}

	protected data(Closure closure) {
		// setup our delegate
		def delegate = new DataDelegate()
		closure.delegate = delegate
		closure()

		// add all parsed locations
		locs.addAll(delegate.locations)
	}

	protected solver(Closure closure) {
		// setup our delegate
		def delegate = new SolverDelegate()
		closure.delegate = delegate
		closure()

		// grab parsed solver config
		config = delegate.config
	}
}
