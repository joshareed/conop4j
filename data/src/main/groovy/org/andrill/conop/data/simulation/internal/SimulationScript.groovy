package org.andrill.conop.data.simulation.internal

import groovy.util.logging.Slf4j

import org.andrill.conop.core.Dataset
import org.andrill.conop.core.internal.DefaultDataset
import org.andrill.conop.core.solver.SolverConfiguration
import org.andrill.conop.data.Repository

@Slf4j
class SimulationScript extends Script {
	def repos = []
	def locs = []
	def config

	def run() {
		// do nothing
	}

	Dataset getDataset() {
		def locations = locs.collect { getLocation(it) }
		new DefaultDataset(locations)
	}

	protected getLocation(id) {
		for (Repository repo : repos) {
			def location = repo.getLocation(id)
			if (location) {
				return location
			}
		}
		throw new RuntimeException("No location with id '${id}' found in any configured repository")
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
