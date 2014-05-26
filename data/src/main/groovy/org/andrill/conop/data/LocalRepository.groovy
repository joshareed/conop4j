package org.andrill.conop.data

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LocalRepository {
	private static final PATTERN = ~/.*\.json/

	private Logger log = LoggerFactory.getLogger(LocalRepository)
	private File root
	
	LocalRepository(String path) {
		root = new File(path)
		if (!root.exists()) {
			log.warn("Directory '{}' not found", path)
		}
	}
	
	List getLocations() {
		List locations = []
		
		new File(root, "locations").eachFileMatch(PATTERN) { File file ->
			try {
				log.debug("Parsing Location at '{}'", file.absolutePath)
				locations << new JsonSlurper().parse(file)
			} catch (Exception e) {
				log.warn("Error parsing Location: {}", e.message)
				log.debug("Parsing exception", e)
			}
		}
		
		locations
	}

	Map getLocation(String id) {
		File file = new File(new File(root, "locations"), "${id}.json")
		if (!file.exists()) {
			log.warn("Location '{}' not found", file)
		}
		try {
			log.debug("Parsing Event at '{}'", file.absolutePath)
			return new JsonSlurper().parse(file)
		} catch (Exception e) {
			log.warn("Error parsing Event: {}", e.message)
			log.debug("Parsing exception", e)
		}
	}

	List getEvents() {
		List events = []
		
		new File(root, "events").eachFileMatch(PATTERN) { File file ->
			try {
				log.debug("Parsing Event at '{}'", file.absolutePath)
				events << new JsonSlurper().parse(file)
			} catch (Exception e) {
				log.warn("Error parsing Event: {}", e.message)
				log.debug("Parsing exception", e)
			}
		}
		
		events
	}

	List getObservations(String locationId) {
		List observations = []
		
		def location = getLocation(locationId)
		if (location) {
			events.each { event ->
				def observation = event?.observations[locationId]
				if (observation) {
					observations << ([event: event] + observation)
				}
			}
		}
		
		observations
	}
}
