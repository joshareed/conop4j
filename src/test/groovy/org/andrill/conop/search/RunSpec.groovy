package org.andrill.conop.search

import spock.lang.Specification

class RunSpec extends Specification {

	def setup() {
		Event.ID = 0;
	}

	def "can load a standard CONOP9 run directory and preserve the weights"() {
		when:
		def run = Run.loadCONOP9Run(new File('src/test/resources/riley'))

		then: 'the run was parsed'
		run != null

		and: 'all sections were parsed'
		run.sections.size() == 7

		and: 'all events were parsed'
		run.events.size() == 124

		and: 'a coexistence matrix'
		run.coexistenceMatrix != null

		and: 'did not override weights'
		!run.sections.collect { it.observations }.flatten().find { it.weightUp > 1 || it.weightDown > 1 }
	}

	def "can load a standard CONOP9 run directory and override the weights"() {
		when:
		def run = Run.loadCONOP9Run(new File('src/test/resources/riley'), true)

		then: 'the run was parsed'
		run != null

		and: 'all sections were parsed'
		run.sections.size() == 7

		and: 'all events were parsed'
		run.events.size() == 124

		and: 'a coexistence matrix'
		run.coexistenceMatrix != null

		and: 'override weights'
		run.sections.collect { it.observations }.flatten().find { it.weightUp > 1 || it.weightDown > 1 }
	}
}
