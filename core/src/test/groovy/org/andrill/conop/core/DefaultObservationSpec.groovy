package org.andrill.conop.core

import org.andrill.conop.core.internal.DefaultEvent;
import org.andrill.conop.core.internal.DefaultObservation;

import spock.lang.Specification

class DefaultObservationSpec extends Specification {

	def "create simple observation"() {
		given: "an event"
		def event = new DefaultEvent("Test")

		when: "create an observation"
		def obs = new DefaultObservation(event, 100.0, 10.0, 5.0)

		then: "the observation is initialized correctly"
		obs.event == event
		obs.level == 100.0
		obs.weightUp == 10.0d
		obs.weightDown == 5.0d
		obs.toString() == "Observation[event=Test, level=100.0, weights=(10.0,5.0)]"
	}
}
