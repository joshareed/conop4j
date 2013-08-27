package org.andrill.conop.search

import spock.lang.Specification

class RunSpec extends Specification {

	def "can load a standard CONOP9 run directory"() {
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
	}
}
