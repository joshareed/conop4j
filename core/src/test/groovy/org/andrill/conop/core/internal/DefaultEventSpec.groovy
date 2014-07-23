package org.andrill.conop.core.internal

import spock.lang.*

class DefaultEventSpec extends Specification {

	def "create simple event"() {
		given: 'an event'
		def event = new DefaultEvent('Test')

		expect: 'the event is initialized properly'
		event.name == 'Test'
		event.toString() == 'Test'

		and: 'equals and hashCode work as expected'
		def other = new DefaultEvent('Test')
		other == event
		other.hashCode() == event.hashCode()
	}
}