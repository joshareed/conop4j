package org.andrill.conop.data

import org.andrill.conop.core.Event
import org.andrill.conop.core.Observation
import org.andrill.conop.core.Run
import org.andrill.conop.core.Section

import spock.lang.Specification

class SolutionParserSpec extends Specification {

	private Run getTestRun() {
		Event.ID = 0

		// events
		Event e1 = Event.createPaired("Fossil 1 LAD", "Fossil 1 FAD")
		Event e2 = e1.beforeConstraint
		Event e3 = Event.createPaired("Fossil 2 LAD", "Fossil 2 FAD")
		Event e4 = e3.beforeConstraint
		Event e5 = new Event("Ash")

		// section
		Section s1 = new Section("Section 1", [
			new Observation(e1, -1, 1.0, 10.0),
			new Observation(e3, -2, 1.0, 10.0),
			new Observation(e5, -3, 10.0, 10.0),
			new Observation(e4, -4, 10.0, 1.0),
			new Observation(e2, -5, 10.0, 1.0)
		])

		return new Run([s1])
	}

	def "can parse a solution"() {
		given: "a run"
		def run = testRun

		and: 'a solution parser'
		def parser = new SolutionParser(new File('src/test/resources/solution.csv'))

		when: 'parse the solution'
		def solution = parser.parse(run)

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
		solution.getRank(event) == 1
	}
}
