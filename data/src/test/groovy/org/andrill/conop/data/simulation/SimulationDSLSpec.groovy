package org.andrill.conop.data.simulation;

import static org.junit.Assert.*
import spock.lang.Specification

class SimulationDSLSpec extends Specification {

	def "can compile simple simulation file"() {
		given: "a simple simulation file"
		def source = """
		repositories {
			local dir: 'local'
		}

		data {
			include 'and1-1b'
			include 'and2-2a'
		}

		solver {
			mutator 'random'
			constraints 'null'
			schedule 'exponential', initial: 1000, delta: 0.001, stepsPer: 5000
			penalty 'placement'
			listener 'snapshot'
			listener 'stopping', progressTime: 360
		}
		"""

		and: "the parser"
		def parser = new SimulationDSL()

		when: "parse the simulation"
		parser.parse(source)

		then: "null run"
		assert parser.run == null

		and: "null solver config"
		assert parser.solverConfiguration == null
	}
}
