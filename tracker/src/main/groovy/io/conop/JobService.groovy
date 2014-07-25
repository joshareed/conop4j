package io.conop

import java.security.SecureRandom

class JobService {
	protected SecureRandom random = new SecureRandom()
	protected jobs = []

	Map add(String source) {
		def job = [
			id: id(),
			active: true,
			source: source
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

	void delete(String id) {
		jobs.findAll { it.id == id }.each { job ->
			job.active = false
		}
	}

	protected String id() {
		new BigInteger(30, random).toString(32)
	}
}