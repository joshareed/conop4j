package org.andrill.conop.data.tools

import groovy.json.JsonBuilder

import org.andrill.conop.core.Dataset
import org.andrill.conop.data.CONOP9Repository

import com.google.common.base.Charsets
import com.google.common.hash.Hashing

class ConvertRepository {
	static IDS = [:]

	static main(args) {
		Dataset dataset = CONOP9Repository.loadCONOP9Run(new File(args[0]))
		def out = new File(args[1])
		dataset.locations.each { l ->
			def (program, site) = l.name.toLowerCase().split(" ")
			def location = [:]
			location.name = l.name
			location.program = program
			location.id = "${program}-${site}".toString()
			location.observations = []

			l.observations.each { o ->
				def e = o.event
				def name = e.name

				def observation = [:]
				observation.event = [
					id: id(name),
					name: name
				]
				observation.level = o.level
				location.observations << observation
			}

			def dir = new File(out, program)
			dir.mkdirs()
			new File(dir, "${site}.json").write(new JsonBuilder(location).toPrettyString())
		}
	}

	static id(name) {
		def key = name.toLowerCase()

		if (!IDS[name]) {
			IDS[name] = Hashing.murmur3_32().hashString(name, Charsets.UTF_8).toString()
		}
		IDS[name]
	}
}
