package org.andrill.conop.data

import spock.lang.Specification

class CONOP9RepositorySpec extends Specification {

	def "can load a standard CONOP9 run directory and preserve the weights"() {
		when:
		def run = CONOP9Repository.loadCONOP9Run(new File('src/test/resources/repos/conop9/riley'))

		then: 'the run was parsed'
		run != null

		and: 'all sections were parsed'
		run.locations.size() == 7

		and: 'all events were parsed'
		run.events.size() == 124

		and: 'did not override weights'
		!run.locations.collect { it.observations }.flatten().find { it.weightUp > 1 || it.weightDown > 1 }
	}

	def "can load a standard CONOP9 run directory and override the weights"() {
		when:
		def run = CONOP9Repository.loadCONOP9Run(new File('src/test/resources/repos/conop9/riley'), true)

		then: 'the run was parsed'
		run != null

		and: 'all sections were parsed'
		run.locations.size() == 7

		and: 'all events were parsed'
		run.events.size() == 124

		and: 'override weights'
		run.locations.collect { it.observations }.flatten().find { it.weightUp > 1 || it.weightDown > 1 }
	}
}
