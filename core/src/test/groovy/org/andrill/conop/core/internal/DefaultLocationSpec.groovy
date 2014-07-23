package org.andrill.conop.core.internal

import spock.lang.Specification

class DefaultLocationSpec extends Specification {

	def "create location"() {
		given: "two events"
		def event1 = new DefaultEvent("Test 1")
		def event2 = new DefaultEvent("Test 2")

		and: "an observation"
		def obs1 = new DefaultObservation(event1, 100.0, 10.0, 5.0)
		def obs2 = new DefaultObservation(event2, 50.0, 1, 1)

		when: "create a location"
		def location = new DefaultLocation("Section", [obs1, obs2])

		then: "the location is initialized properly"
		location.name == "Section"
		location.toString() == "Section"

		and: "equals and hashCode work as expected"
		def other = new DefaultLocation("Section", [])
		location == other
		location.hashCode() == other.hashCode()

		and: "can get all events"
		location.events == [event1, event2] as Set

		and: "can get all levels"
		location.levels == [50.0, 100.0] as Set

		and: "can get all observations"
		location.observations == [obs1, obs2] as Set

		and: "can get a specific observation for an event"
		location.getObservation(event1) == obs1
	}
}
