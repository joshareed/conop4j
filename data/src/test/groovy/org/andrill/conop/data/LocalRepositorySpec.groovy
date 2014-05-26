package org.andrill.conop.data

import spock.lang.Specification

class LocalRepositorySpec extends Specification {

	def "get all locations"() {
		given: 'a local repository'
		def repo = new LocalRepository('src/test/resources/repo')
		
		when: 'get locations'
		def locations = repo.locations
		
		then: 'found all locations'
		assert locations
		assert 1 == locations.size()
		assert 'and1-1b' == locations[0].id
	}

	def "get a location"() {
		given: 'a local repository'
		def repo = new LocalRepository('src/test/resources/repo')
		
		when: 'get and1-1b'
		def location = repo.getLocation("and1-1b")
		
		then: 'found and1-1b'
		assert location
		assert 'and1-1b' == location.id
	}

	def "get all events"() {
		given: 'a local repository'
		def repo = new LocalRepository('src/test/resources/repo')
		
		when: 'get events'
		def events = repo.events
		
		then: 'found all events'
		assert events
		assert 2 == events.size()
		assert events.find { it.id == 'fragilariopsis-curta' }
		assert events.find { it.id == 'fragilariopsis-fossilis' }
	}
	
	def "get observations"() {
		given: 'a local repository'
		def repo = new LocalRepository('src/test/resources/repo')
		
		when: 'get observations'
		def observations = repo.getObservations("and1-1b")
		
		then: 'found all observations'
		assert observations
		assert 2 == observations.size()
		assert observations.find { it.event.id == 'fragilariopsis-curta' }
		assert observations.find { it.event.id == 'fragilariopsis-fossilis' }
		
	}
}
