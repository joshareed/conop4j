package org.andrill.conop.data.internal

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import org.andrill.conop.core.Event
import org.andrill.conop.core.Location
import org.andrill.conop.core.internal.DefaultEvent
import org.andrill.conop.core.internal.DefaultLocation
import org.andrill.conop.core.internal.DefaultObservation
import org.andrill.conop.data.Repository

@Slf4j
abstract class AbstractJsonRepository implements Repository {

	protected abstract String fetch(String id)

	@Override
	public Location getLocation(String id) {
		// fetch the content
		def content = fetch(id)
		if (!content) {
			return null
		}

		// parse the content as JSON
		def json = new JsonSlurper().parseText(content)
		if (!json.name) {
			throw new RuntimeException("Invalid Location JSON: missing 'name' property")
		}
		if (!json.observations) {
			throw new RuntimeException("Invalid Location JSON: missing 'observations' property")
		}

		def observations = []
		json.observations.each { o ->
			def event = new DefaultEvent(o.event.name)
			def up = getWeightUp(event)
			def dn = getWeightDown(event)
			observations << new DefaultObservation(event, o.level, up, dn)
		}

		return new DefaultLocation(json.name, observations)
	}

	@Override
	public List<Location> getLocations() {
		throw new UnsupportedOperationException("Unable to enumerate locations");
	}

	protected double getWeightUp(Event e) {
		if (e.name.endsWith("AGE")) {
			return 1000000
		} else if (e.name.endsWith("ASH")) {
			return 1000000
		} else if (e.name.endsWith("FAD")) {
			return 1000000
		} else {
			return 1
		}
	}

	protected double getWeightDown(Event e) {
		if (e.name.endsWith("AGE")) {
			return 1000000
		} else if (e.name.endsWith("ASH")) {
			return 1000000
		} else if (e.name.endsWith("LAD")) {
			return 1000000
		} else {
			return 1
		}
	}
}
