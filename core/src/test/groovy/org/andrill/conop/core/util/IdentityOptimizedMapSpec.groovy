package org.andrill.conop.core.util;

import static org.junit.Assert.*

import org.andrill.conop.core.internal.DefaultEvent

import spock.lang.Specification

class IdentityOptimizedMapSpec extends Specification {

	def "works as a map"() {
		given: "a multilevelmap"
		def map = new IdentityOptimizedMap()

		and: "an event"
		def event = new DefaultEvent("Event 1")

		when: "add an event"
		map.put(event, event)

		then: "same event"
		assert map.get(event) == event
		assert map.get(event).is(event)

		and: "can get equivalent event"
		assert map.get(new DefaultEvent("Event 1")) == event
		assert map.get(new DefaultEvent("Event 1")).is(event)
	}
}
