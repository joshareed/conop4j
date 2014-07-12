package org.andrill.conop.core

import org.andrill.conop.core.internal.DefaultEvent;

import spock.lang.*

class DefaultEventSpec extends Specification {

	def "create simple event"() {
		given: 'an event'
		def event = new DefaultEvent('Test')

		expect: 'the event is initialized properly'
		event.name == 'Test'
		event.toString() == 'Test'
		!event.beforeConstraint
		!event.afterConstraint

		and: 'equals and hashCode work as expected'
		def other = new DefaultEvent('Test')
		other == event
		other.hashCode() == event.hashCode()
	}

	def "create paired events"() {
		given: 'a paried event'
		def first = DefaultEvent.createPaired('First', 'Last')

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