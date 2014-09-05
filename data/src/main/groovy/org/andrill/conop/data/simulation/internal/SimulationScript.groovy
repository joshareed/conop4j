package org.andrill.conop.data.simulation.internal

import groovy.util.logging.Slf4j

import org.andrill.conop.core.Dataset
import org.andrill.conop.core.internal.DefaultDataset
import org.andrill.conop.core.solver.SolverConfiguration
import org.andrill.conop.data.Repository

@Slf4j
class SimulationScript extends Script {
	def repoCtx
	def dataCtx
	def config

	def run() {
		// do nothing
	}

	Dataset getDataset() {
		def locations = dataCtx.locations
		if (locations) {
			return new DefaultDataset(locations.collect { getLocation(it) })
		} else if (dataCtx.all) {
			return new DefaultDataset(repoCtx.repositories.collect { it.locations }.flatten())
		}
	}

	protected getLocation(id) {
		for (Repository repo : repoCtx.repositories) {
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
		repoCtx = new RepositoriesDelegate()
		closure.delegate = repoCtx
		closure()
	}

	protected data(Closure closure) {
		// setup our delegate
		dataCtx = new DataDelegate()
		closure.delegate = dataCtx
		closure()
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
