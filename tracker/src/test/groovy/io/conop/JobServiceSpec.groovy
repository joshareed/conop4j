package io.conop;

import static org.junit.Assert.*

import org.junit.Before

import spock.lang.Specification

class JobServiceSpec extends Specification {
	def jobService

	@Before
	void setup() {
		jobService = new JobService()
	}

	def "can get a list of jobs"() {
		expect: "empty list"
		assert []== jobService.allJobs
		assert []== jobService.activeJobs

		when: "add a job"
		def job = jobService.add("job source code here")

		then:
		assert [job]== jobService.allJobs
		assert [job]== jobService.activeJobs

		when: "mark as inactive"
		jobService.delete(job.id)

		then:
		assert [job]== jobService.allJobs
		assert []== jobService.activeJobs
	}

	def "can get a job by id"() {
		given: "a job"
		def id = jobService.add("job source code here").id

		when: "get a job with an invalid id"
		def job1 = jobService.get("invalid")

		then: "null"
		assert null == job1

		when: "get a job with a valid id"
		def job2 = jobService.get(id)

		then: "the job"
		job2.id == id
	}

	def "test add creates a new job"() {
		when: "add a new job"
		def job = jobService.add("job source code here")

		then: "the job was created"
		assert job
		assert job.id
		assert job.active
		assert job.source == "job source code here"

		and: "the job was added to the job list"
		assert job == jobService.allJobs[0]

		and: "can get the job back"
		assert job == jobService.get(job.id)
	}
}
