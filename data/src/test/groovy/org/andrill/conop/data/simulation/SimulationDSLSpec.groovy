package org.andrill.conop.data.simulation;

import static org.junit.Assert.*

import org.andrill.conop.core.constraints.NullConstraints
import org.andrill.conop.core.listeners.SnapshotListener
import org.andrill.conop.core.listeners.StoppingListener
import org.andrill.conop.core.mutators.RandomMutator
import org.andrill.conop.core.penalties.PlacementPenalty
import org.andrill.conop.core.schedules.ExponentialSchedule

import spock.lang.Specification

class SimulationDSLSpec extends Specification {

	def "can compile simulation file"() {
		given: "a  simulation file"
		def source = """
		repositories {
			local dir: 'src/test/resources/repos/local/'
		}

		data {
			location 'andrill:1b'
			location 'andrill:2a'

			location 'ciros:2'

			location 'dsdp:266'
			location 'dsdp:269'
			location 'dsdp:274'

			location 'dvdp:10'
			location 'dvdp:11'

			location 'odp:689b'
			location 'odp:690b'
			location 'odp:693'
			location 'odp:695a'
			location 'odp:696'
			location 'odp:699a'
			location 'odp:701c'
			location 'odp:736'
			location 'odp:737a'
			location 'odp:744b'
			location 'odp:745b'
			location 'odp:746a'
			location 'odp:747a'
			location 'odp:748b'
			location 'odp:751a'
			location 'odp:1093'
			location 'odp:1094'
			location 'odp:1096'
			location 'odp:1101a'
			location 'odp:1138a'
			location 'odp:1165b'
		}

		solver {
			mutator 'random'
			constraints 'null'
			schedule 'exponential', initial: 1, delta: 2, steps: 3
			penalty 'placement'
			listener 'snapshot'
			listener 'stopping', progressTime: 4
		}
		"""

		and: "the parser"
		def parser = new SimulationDSL()

		when: "parse the simulation"
		parser.parse(source)

		then: "run"
		assert parser.run
		assert parser.run.events.size() == 296
		assert parser.run.locations.size() == 29
		assert parser.run.locations.find { it.name == 'ANDRILL 1B' }
		assert parser.run.locations.find { it.name == 'ANDRILL 2A' }

		and: "solver configuration"
		assert parser.solverConfiguration
		assert parser.solverConfiguration.mutator instanceof RandomMutator
		assert parser.solverConfiguration.constraints instanceof NullConstraints
		assert parser.solverConfiguration.schedule instanceof ExponentialSchedule
		assert parser.solverConfiguration.schedule.initial == 1
		assert parser.solverConfiguration.schedule.delta == 2
		assert parser.solverConfiguration.schedule.steps == 3
		assert parser.solverConfiguration.penalty instanceof PlacementPenalty
		assert parser.solverConfiguration.listeners.size() == 2
		assert parser.solverConfiguration.listeners.find { it instanceof SnapshotListener }
		assert parser.solverConfiguration.listeners.find { it instanceof StoppingListener }
	}
}
