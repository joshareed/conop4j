package io.conop

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import org.andrill.conop.core.Solution
import org.andrill.conop.core.listeners.SnapshotListener
import org.andrill.conop.core.util.TimerUtils
import org.andrill.conop.data.simulation.SimulationDSL

@Slf4j
class Agent extends Thread {
	protected static final long DELAY = 30 * 1000
	protected URL api
	protected String name

	Agent(String path, String name) {
		api = new URL(path)
		this.name = name
	}

	protected void runJob(job) {
		log.info "Starting job {}", job.url

		try {
			TimerUtils.reset()

			def dsl = new SimulationDSL()
			dsl.parse(job.source)
			def dataset = dsl.dataset

			def config = dsl.solverConfiguration

			if (job?.stats?.temperature > 0) {
				log.info "Resuming existing job, using initial temperature {}C", job.stats.temperature
				config.updateSchedule(initial: job.stats.temperature, true)
			}
			if (job?.solution?.events) {
				log.info "Resuming existing job, overriding initial solution"
				def events = []
				job?.solution?.events.each { e ->
					events << dataset.events.find { it.name == e.name }
				}
				config.configureInitialSolution(new Solution(events))
			}

			config.filterListeners SnapshotListener.class
			config.configureListener(AgentListener.class, [api: job.url, name: name, frequency: 60])

			def solver = config.solver
			def context = solver.solve(config, dataset)
			log.info "Finished job"
		} catch (e) {
			log.info "Halted: {}", e.message
			log.debug "Halted", e
		}
	}

	@Override
	public void run() {
		log.info "Agent '{}' starting using tracker url {}", name, api
		while (true) {
			try {
				log.debug "Fetching jobs..."

				def jobs = new JsonSlurper().parse(new URL(api, 'api/jobs'))
				if (jobs) {
					def active = jobs.findAll { it?.active }
					if (active) {
						runJob(active.min { it?.stats?.iterations })
					}
				}
			} catch(e) {
				log.info "Error while fetching jobs: {}", e.message
				log.debug "Error while fetching jobs", e
			}
			Thread.sleep(DELAY)
		}
	}
}
