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
		def source = new File("src/test/resources/test.simulation").text

		and: "the parser"
		def parser = new SimulationDSL()

		when: "parse the simulation"
		parser.parse(source)

		then: "dataset"
		assert parser.dataset
		assert parser.dataset.events.size() == 296
		assert parser.dataset.locations.size() == 29
		assert parser.dataset.locations.find { it.name == 'ANDRILL 1B' }
		assert parser.dataset.locations.find { it.name == 'ANDRILL 2A' }

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
