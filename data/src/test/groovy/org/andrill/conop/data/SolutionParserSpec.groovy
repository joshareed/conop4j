package org.andrill.conop.data

import org.andrill.conop.core.Event
import org.andrill.conop.core.Run;
import org.andrill.conop.core.Section
import org.andrill.conop.core.internal.DefaultEvent;
import org.andrill.conop.core.internal.DefaultObservation;
import org.andrill.conop.core.internal.DefaultSection;

import spock.lang.Specification

class SolutionParserSpec extends Specification {

	private Run getTestRun() {

		// events
		Event e1 = DefaultEvent.createPaired("Fossil 1 LAD", "Fossil 1 FAD")
		Event e2 = e1.beforeConstraint
		Event e3 = DefaultEvent.createPaired("Fossil 2 LAD", "Fossil 2 FAD")
		Event e4 = e3.beforeConstraint
		Event e5 = new DefaultEvent("Ash")

		// section
		Section s1 = new DefaultSection("Section 1", [
			new DefaultObservation(e1, -1, 1.0, 10.0),
			new DefaultObservation(e3, -2, 1.0, 10.0),
			new DefaultObservation(e5, -3, 10.0, 10.0),
			new DefaultObservation(e4, -4, 10.0, 1.0),
			new DefaultObservation(e2, -5, 10.0, 1.0)
		])

		return new DefaultRun([s1])
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
