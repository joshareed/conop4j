package org.andrill.conop.core.constraints

import org.andrill.conop.core.*
import org.andrill.conop.core.internal.DefaultDataset
import org.andrill.conop.core.internal.DefaultEvent
import org.andrill.conop.core.internal.DefaultLocation
import org.andrill.conop.core.internal.DefaultObservation
import org.andrill.conop.core.internal.DefaultSolverContext

import spock.lang.Specification

class EventConstraintsSpec extends Specification {

	def "checks constraints"() {
		given: "events"
		def pair1 = new DefaultEvent("Taxon LAD")
		def pair2 = new DefaultEvent("Taxon FAD")
		def other = new DefaultEvent("Other")

		and: "a mock dataset"
		def dataset = new DefaultDataset([
			new DefaultLocation("Test", [
				new DefaultObservation(pair1, 5, 1, 1),
				new DefaultObservation(other, 10, 1, 1),
				new DefaultObservation(pair2, 15, 1, 1)
			])
		])

		and: "a context"
		def context = new DefaultSolverContext()
		context.dataset = dataset

		and: "an EventChecker"
		def checker = new EventConstraints()
		checker.configure(new Configuration([:]))
		checker.context = context

		when: "a valid solution"
		def solution1 = new Solution([pair1, pair2, other])

		then: 'valid'
		checker.isValid(solution1)

		when: 'an invalid solution'
		def solution2 = new Solution([other, pair2, pair1])

		then: 'not valid'
		!checker.isValid(solution2)
	}

	def "toString returns as expected"() {
		expect:
		new EventConstraints().toString() == "Event Constraints"
	}
}
