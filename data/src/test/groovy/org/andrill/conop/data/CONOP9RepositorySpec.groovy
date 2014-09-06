package org.andrill.conop.data

import spock.lang.Specification

class CONOP9RepositorySpec extends Specification {

	def "can load a CONOP9 dataset from a local directory and preserve the weights"() {
		when:
		def dataset = new CONOP9Repository(new File('src/test/resources/repos/conop9/riley')).dataset

		then: 'the dataset was parsed'
		dataset != null

		and: 'all sections were parsed'
		dataset.locations.size() == 7

		and: 'all events were parsed'
		dataset.events.size() == 124

		and: 'did not override weights'
		!dataset.locations.collect { it.observations }.flatten().find { it.weightUp > 1 || it.weightDown > 1 }
	}

	def "can load a CONOP9 dataset from a local directory and override the weights"() {
		when:
		def dataset = new CONOP9Repository(new File('src/test/resources/repos/conop9/riley'), true).dataset

		then: 'the dataset was parsed'
		dataset != null

		and: 'all sections were parsed'
		dataset.locations.size() == 7

		and: 'all events were parsed'
		dataset.events.size() == 124

		and: 'override weights'
		dataset.locations.collect { it.observations }.flatten().find { it.weightUp > 1 || it.weightDown > 1 }
	}

	def "can load a CONOP9 dataset from a remote url and override the weights"() {
		when:
		def dataset = new CONOP9Repository(new URL('https://raw.githubusercontent.com/joshareed/conop4j/master/data/src/test/resources/repos/conop9/riley/'), true).dataset

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
