package org.andrill.conop.data.internal

import groovy.json.JsonSlurper

import org.andrill.conop.core.Location
import org.andrill.conop.core.internal.DefaultEvent
import org.andrill.conop.core.internal.DefaultLocation
import org.andrill.conop.core.internal.DefaultObservation
import org.andrill.conop.data.Repository

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
			observations << new DefaultObservation(event, o.level, 1, 1)
		}

		return new DefaultLocation(json.name, observations)
	}
}
