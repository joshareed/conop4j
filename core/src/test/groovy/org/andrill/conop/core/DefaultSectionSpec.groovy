package org.andrill.conop.core

import org.andrill.conop.core.DefaultEvent;
import org.andrill.conop.core.DefaultObservation;
import org.andrill.conop.core.DefaultSection;

import spock.lang.Specification

class DefaultSectionSpec extends Specification {

	def "create section"() {
		given: "two events"
		def event1 = new DefaultEvent("Test 1")
		def event2 = new DefaultEvent("Test 2")

		and: "an observation"
		def obs1 = new DefaultObservation(event1, 100.0, 10.0, 5.0)
		def obs2 = new DefaultObservation(event2, 50.0, 1, 1)

		when: "create a section"
		def section = new DefaultSection("Section", [obs1, obs2])

		then: "the section is initialized properly"
		section.name == "Section"
		section.toString() == "Section"

		and: "equals and hashCode work as expected"
		def other = new DefaultSection("Section", [])
		section == other
		section.hashCode() == other.hashCode()

		and: "can get all events"
		section.events == [event1, event2] as Set

		and: "can get all levels"
		section.levels == [50.0, 100.0] as Set

		and: "can get all observations"
		section.observations == [obs1, obs2] as Set

		and: "can get a specific observation for an event"
		section.getObservation(event1) == obs1

		and: "can get a set of observations for a level"
		section.getObservations(100.0) == [obs1] as Set
	}
}
