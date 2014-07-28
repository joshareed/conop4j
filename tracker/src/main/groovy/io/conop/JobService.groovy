package io.conop

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import java.security.SecureRandom

@Slf4j
class JobService {
	protected SecureRandom random = new SecureRandom()
	protected jobs = []

	Map add(String source) {
		def job = [
			id: id(),
			active: true,
			source: source,
			stats: [
				created: null,
				updated: null,
				iterations: 0
			]
		]
		jobs << job
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

	boolean update(id, body) {
		def job = get(id)
		if (job) {
			def json = new JsonSlurper().parse(body.inputStream)

			// update
			job.stats.updated = System.currentTimeMillis()
			if (!job.stats.created) {
				job.stats.created = job.stats.updated
			}
			if (json.iterations) {
				job.stats.iterations += json.iterations
			}
			if (!job.solution || json.solution.score <= job.solution.score) {
				job.solution = json.solution
			}

			return job
		} else {
			return null
		}
	}

	void delete(String id) {
		jobs.findAll { it.id == id }.each { job ->
			job.active = false
		}
	}

	protected String id() {
		new BigInteger(30, random).toString(32)
	}
}