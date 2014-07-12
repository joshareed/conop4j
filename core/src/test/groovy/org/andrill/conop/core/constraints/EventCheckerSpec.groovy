package org.andrill.conop.core.constraints

import org.andrill.conop.core.*
import org.andrill.conop.core.internal.DefaultEvent;
import org.andrill.conop.core.internal.DefaultObservation;
import org.andrill.conop.core.internal.DefaultRun;
import org.andrill.conop.core.internal.DefaultSection;

import spock.lang.Specification

class EventCheckerSpec extends Specification {

	def "checks constraints"() {
		given: "events"
		def pair1 = DefaultEvent.createPaired("First", "Last")
		def pair2 = pair1.beforeConstraint
		def other = new DefaultEvent("Other")

		and: "a mock run"
		def run = new DefaultRun([
			new DefaultSection("Test", [
				new DefaultObservation(pair1, 5, 1, 1),
				new DefaultObservation(other, 10, 1, 1),
				new DefaultObservation(pair2, 15, 1, 1)
			])
		])

		and: "an EventChecker"
		def checker = new EventConstraints()
		checker.configure(new Configuration())

		when: "a valid solution"
		def solution1 = new Solution(run, [pair1, pair2, other])

		then: 'valid'
		checker.isValid(solution1)

		when: 'an invalid solution'
		def solution2 = new Solution(run, [other, pair2, pair1])

		then: 'not valid'
		!checker.isValid(solution2)
	}

	def "toString returns as expected"() {
		expect:
		new EventConstraints().toString() == "Event"
	}
}
