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

	@Inject
	JobService(LaunchConfig config) {
		String path = config?.getOther("jobs", null)
		if (path) {
			db = new File(path)
			readJobs()
		}
	}

	protected void readJobs() {
		if (db == null) { return }

		try {
			def json = new JsonSlurper().parse(db)
			jobs = json.collect { it }
		} catch (e) {
			log.error "Unable to parse ${db.absolutePath}", e
		}
	}

	protected void writeJobs() {
		if (db == null) { return }

		db.write(JsonOutput.toJson(jobs))
	}

	Map add(String source) {
		def job = [
			id: id(),
			active: true,
			source: source,
			stats: [
				created: null,
				updated: null,
				iterations: 0,
				score: -1
			]
		]
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
				job.stats.score = json.solution.score
			}

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