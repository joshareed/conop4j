package org.andrill.conop.search.constraints

import org.andrill.conop.search.Event
import org.andrill.conop.search.Solution

import spock.lang.Specification

class EventCheckerSpec extends Specification {

	def "checks constraints"() {
		given: "events"
		def pair1 = Event.createPaired("First", "Last")
		def pair2 = pair1.beforeConstraint
		def other = new Event("Other")

		and: "an EventChecker"
		def checker = new EventChecker()

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
