package io.conop

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import java.security.SecureRandom

import ratpack.launch.LaunchConfig

import com.google.inject.Inject

@Slf4j
class JobService {
	protected SecureRandom random = new SecureRandom()
	protected jobs = []
	protected File db = null
	protected String publicAddress = null

	@Inject
	JobService(LaunchConfig config) {
		String path = config?.getOther("jobs", null)
		if (path) {
			db = new File(path)
			readJobs()
		}
		publicAddress = config?.getPublicAddress().toString()
		log.info "Using public address {}", publicAddress
	}

	protected void readJobs() {
		if (db == null) {
			return
		}

		try {
			def json = new JsonSlurper().parse(db)
			jobs = json.collect { it }
		} catch (e) {
			log.error "Unable to parse ${db.absolutePath}"
		}
	}

	protected void writeJobs() {
		if (db == null) {
			return
		}

		db.write(JsonOutput.toJson(jobs))
	}

	Map add(String source) {
		if (!source.trim()) {
			return null
		}

		long now = System.currentTimeMillis()

		def job = [
			id: id(),
			active: true,
			source: source,
			created: now,
			updated: now,
			stats: [
				scored: 0l,
				skipped: 0l,
				total: 0l,
				score: -1,
				temperature: -1,
				constraints: false
			],
			agents: [:]
		]
		job.url = "${publicAddress}/api/jobs/${job.id}".toString()
		jobs << job

		writeJobs()

		job
	}

	Map get(String id) {
		jobs.find { it.id == id }
	}

	List getAllJobs() {
		jobs
	}

	List getActiveJobs() {
		jobs.findAll { it.active } ?: []
	}

	protected updateStats(job) {
		long now = System.currentTimeMillis()
		long since = now - (5 * 60 * 1000)

		try {
			// update timestamps
			job.updated = now

			// update stats
			def stats = job.stats
			def scored = BigInteger.valueOf(0)
			def skipped = BigInteger.valueOf(0)
			def total = BigInteger.valueOf(0)

			stats.score = Double.MAX_VALUE
			stats.temperature = Double.MAX_VALUE
			stats.constraints = false

			job.agents.each { name, agent ->
				scored = scored.add(agent.scored)
				skipped = skipped.add(agent.skipped)
				total = total.add(agent.total)
				stats.score = Math.min(stats.score, agent.score)
				stats.temperature = Math.min(stats.temperature, agent.temperature)
				stats.constraints = stats.constraints | agent.constraints

				agent.active = agent.updated >= since
			}
			stats.scored = scored
			stats.skipped = skipped
			stats.total = total

			// update job status
			if (stats.temperature <= 0.1) {
				job.active = false
			}
		} catch (e) {
			log.error "Error updating stats", e
		}
	}

	def update(id, body) {
		def job = get(id)
		if (job) {

			def json = new JsonSlurper().parse(body.inputStream)

			// handle agent
			if (json.agent && json.stats) {
				job.agents[json.agent] = json.stats
			}

			// update solution
			if (!job.solution || json?.solution?.score <= job.solution.score) {
				job.solution = json.solution
				job.stats.score = json.solution.score
			} else if (json?.solution?.score == job.solution.score) {
				// merge
				json.solution.events.each { e ->
					def existing = job.solution.events.find { it.name == e.name }
					e.positions.min = Math.min(e.positions.min, existing.positions.min)
					e.positions.max = Math.max(e.positions.max, existing.positions.max)
				}
				job.solution = json.solution
			}

			updateStats(job)

			writeJobs()

			return job
		} else {
			return null
		}
	}

	void delete(String id) {
		jobs.findAll { it.id == id }.each { job ->
			job.active = false
		}

		writeJobs()
	}

	protected String id() {
		new BigInteger(30, random).toString(32)
	}
}