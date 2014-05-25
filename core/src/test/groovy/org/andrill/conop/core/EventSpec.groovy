package org.andrill.conop.core

import org.andrill.conop.core.Event;

import spock.lang.*

class EventSpec extends Specification {

	def "create simple event"() {
		given: 'an event'
		def event = new Event('Test')

		expect: 'the event is initialized properly'
		event.name == 'Test'
		event.toString() == 'Test'
		!event.beforeConstraint
		!event.afterConstraint
		event.internalId >= 0

		and: 'equals and hashCode work as expected'
		def other = new Event('Test')
		other == event
		other.hashCode() == event.hashCode()
	}

	def "create paired events"() {
		given: 'a paried event'
		def first = Event.createPaired('First', 'Last')

		expect: 'the first event is initialize properly'
		first.name == 'First'
		first.toString() == 'First'
		first.beforeConstraint
		!first.afterConstraint

		when: 'we grab the paired event'
		def last = first.beforeConstraint

		then: 'the paired event is initialized properly'
		last.name == 'Last'
		last.toString() == 'Last'
		!last.beforeConstraint
		last.afterConstraint == first
	}
}