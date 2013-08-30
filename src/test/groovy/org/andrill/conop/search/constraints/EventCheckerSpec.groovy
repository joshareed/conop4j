package org.andrill.conop.search.constraints

import org.andrill.conop.search.*

import spock.lang.Specification

class EventCheckerSpec extends Specification {

	def "checks constraints"() {
		given: "events"
		def pair1 = Event.createPaired("First", "Last")
		def pair2 = pair1.beforeConstraint
		def other = new Event("Other")

		and: "a mock run"
		def run = new Run([
			new Section("Test", [
				new Observation(pair1, 5, 1, 1),
				new Observation(other, 10, 1, 1),
				new Observation(pair2, 15, 1, 1)
			])
		])

		and: "an EventChecker"
		def checker = new EventChecker()
		checker.configure(new Properties(), run)

		when: "a valid solution"
		def solution1 = new Solution(null, [pair1, pair2, other])

		then: 'valid'
		checker.isValid(solution1)

		when: 'an invalid solution'
		def solution2 = new Solution(null, [other, pair2, pair1])

		then: 'not valid'
		!checker.isValid(solution2)
	}

	def "toString returns as expected"() {
		expect:
		new EventChecker().toString() == "Event"
	}
}
