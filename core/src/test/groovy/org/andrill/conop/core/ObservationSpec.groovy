package org.andrill.conop.core

import org.andrill.conop.core.Event;
import org.andrill.conop.core.Observation;

import spock.lang.Specification

class ObservationSpec extends Specification {

	def "create simple observation"() {
		given: "an event"
		def event = new Event("Test")

		when: "create an observation"
		def obs = new Observation(event, 100.0, 10.0, 5.0)

		then: "the observation is initialized correctly"
		obs.event == event
		obs.level == 100.0
		obs.weightUp == 10.0d
		obs.weightDown == 5.0d
		obs.toString() == "Observation[event=Test, level=100.0, weights=(10.0,5.0)]"
	}
}
