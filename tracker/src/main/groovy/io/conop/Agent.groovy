package io.conop

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import org.andrill.conop.core.listeners.ConsoleProgressListener
import org.andrill.conop.core.listeners.SnapshotListener
import org.andrill.conop.data.simulation.SimulationDSL

@Slf4j
class Agent extends Thread {
	protected static final long DELAY = 30 * 1000
	protected URL api

	Agent(String path) {
		api = new URL(path)
	}

	protected void runJob(job) {
		log.info "Starting job {}", job.url

		try {
			def dsl = new SimulationDSL()
			dsl.parse(job.source)
			def run = dsl.run

			def config = dsl.solverConfiguration
			config.filterListeners ConsoleProgressListener.class
			config.filterListeners SnapshotListener.class
			config.configureListener(AgentListener.class, [api: job.url])

			def solver = config.solver
			solver.solve(config, run)
		} catch (e) {
			log.info "Halted: {}", e.message
			log.debug "Halted", e
		}
	}

	@Override
	public void run() {
		log.info "Agent starting using tracker url: {}", api
		while (true) {
			try {
				log.debug "Fetching jobs..."

				def jobs = new JsonSlurper().parse(api)
				if (jobs) {
					runJob(jobs.min { it?.stats?.iterations })
				}
			} catch(e) {
				log.info "Error while fetching jobs: {}", e.message
				log.debug "Error while fetching jobs", e
			}
			Thread.sleep(DELAY)
		}
	}

	static main(args) {
		if (!args) {
			println "Error: please specify tracker URL"
			System.exit(0)
		}

		def agent = new Agent(args[0])
		agent.start()
	}
}
