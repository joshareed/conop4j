package org.andrill.conop.data

import spock.lang.Specification

class CONOP9RepositorySpec extends Specification {

	def "can load a standard CONOP9 dataset directory and preserve the weights"() {
		when:
		def dataset = CONOP9Repository.loadCONOP9Run(new File('src/test/resources/repos/conop9/riley'))

		then: 'the dataset was parsed'
		dataset != null

		and: 'all sections were parsed'
		dataset.locations.size() == 7

		and: 'all events were parsed'
		dataset.events.size() == 124

		and: 'did not override weights'
		!dataset.locations.collect { it.observations }.flatten().find { it.weightUp > 1 || it.weightDown > 1 }
	}

	def "can load a standard CONOP9 run directory and override the weights"() {
		when:
		def dataset = CONOP9Repository.loadCONOP9Run(new File('src/test/resources/repos/conop9/riley'), true)

		then: 'the dataset was parsed'
		dataset != null

		and: 'all sections were parsed'
		dataset.locations.size() == 7

		and: 'all events were parsed'
		dataset.events.size() == 124

		and: 'override weights'
		dataset.locations.collect { it.observations }.flatten().find { it.weightUp > 1 || it.weightDown > 1 }
	}
}
