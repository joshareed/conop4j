package org.andrill.conop.data

import org.andrill.conop.core.Dataset
import org.andrill.conop.core.Event
import org.andrill.conop.core.Location
import org.andrill.conop.core.internal.DefaultDataset
import org.andrill.conop.core.internal.DefaultEvent
import org.andrill.conop.core.internal.DefaultLocation
import org.andrill.conop.core.internal.DefaultObservation

import spock.lang.Specification

class SolutionParserSpec extends Specification {

	private Dataset getTestDataset() {

		// events
		Event e1 = new DefaultEvent("Fossil 1 LAD")
		Event e2 = new DefaultEvent("Fossil 1 FAD")
		Event e3 = new DefaultEvent("Fossil 2 LAD")
		Event e4 = new DefaultEvent("Fossil 2 FAD")
		Event e5 = new DefaultEvent("Ash")

		// section
		Location s1 = new DefaultLocation("Section 1", [
			new DefaultObservation(e1, -1, 1.0, 10.0),
			new DefaultObservation(e3, -2, 1.0, 10.0),
			new DefaultObservation(e5, -3, 10.0, 10.0),
			new DefaultObservation(e4, -4, 10.0, 1.0),
			new DefaultObservation(e2, -5, 10.0, 1.0)
		])

		return new DefaultDataset([s1])
	}

	def "can parse a solution"() {
		given: "a dataset"
		def dataset = testDataset

		and: 'a solution parser'
		def parser = new SolutionParser(new File('src/test/resources/solution.csv'))

		when: 'parse the solution'
		def solution = parser.parse(dataset)

		then: 'the solution was parsed'
		solution != null

		and: 'expected number of events'
		solution.events.size() == 5

		when: 'find specific event'
		def event = solution.events.find { it.name == 'Fossil 2 LAD' }

		then: 'event was found'
		event != null

		and: 'position and rank is correct'
		solution.getPosition(event) == 4
	}
}
