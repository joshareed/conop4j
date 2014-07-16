package org.andrill.conop.data.tools

import groovy.json.JsonBuilder

import org.andrill.conop.core.Run
import org.andrill.conop.data.CONOP9Repository

import com.google.common.base.Charsets
import com.google.common.hash.Hashing

class ConvertRepository {
	static IDS = [:]

	static main(args) {
		Run run = CONOP9Repository.loadCONOP9Run(new File(args[0]))
		def out = new File(args[1])
		run.locations.each { l ->
			def (program, site) = l.name.toLowerCase().split(" ")
			def location = [:]
			location.name = l.name
			location.program = program
			location.id = "${program}-${site}".toString()
			location.observations = []

			l.observations.each { o ->
				def e = o.event
				def name = strip(e.name)

				def observation = [:]
				observation.event = [
					id: id(name),
					name: name
				]

				if (!e.beforeConstraint && !e.afterConstraint) {
					observation.top = o.level
					observation.base = o.level

					location.observations << observation
				} else if (e.beforeConstraint && !e.afterConstraint) {
					observation.top = o.level
					observation.base = l.getObservation(e.beforeConstraint).level

					location.observations << observation
				}
			}

			def dir = new File(out, program)
			dir.mkdirs()
			new File(dir, "${site}.json").write(new JsonBuilder(location).toPrettyString())
		}
	}

	static strip(name) {
		(name - 'AGE' - 'ASH' - 'MID' - 'LAD' - 'FAD').trim()
	}

	static id(name) {
		def key = name.toLowerCase()

		if (!IDS[name]) {
			IDS[name] = Hashing.murmur3_32().hashString(name, Charsets.UTF_8).toString()
		}
		IDS[name]
	}
}
