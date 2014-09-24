package org.andrill.conop.analysis

import static org.andrill.conop.analysis.AnnotatedEvent.*
import groovy.json.JsonSlurper

import org.andrill.conop.core.Dataset
import org.andrill.conop.data.simulation.SimulationDSL

class Conop4jOutput {
	protected Dataset dataset
	protected AnnotatedSolution solution

	Conop4jOutput(String source) {
		def json = new JsonSlurper().parseText(source)
		if (json.source) {
			def dsl = new SimulationDSL()
			dsl.parse(json.source)
			dataset = dsl.dataset
		}
		if (json.solution) {
			def events = json.solution.events.collect { e ->
				def event = new AnnotatedEvent(e.name)
				if (e.positions) {
					event.setAnnotation(POS, e.positions."final")
					event.setAnnotation(MAX_POS, e.positions.max)
					event.setAnnotation(MIN_POS, e.positions.min)
				}
				event
			}
			solution = new AnnotatedSolution(events)
			solution.score = json.solution.score
		}
	}

	Dataset getDataset() {
		dataset
	}

	AnnotatedSolution getAnnotatedSolution() {
		solution
	}
}
