package org.andrill.conop.core.objectives

import org.andrill.conop.core.Simulation
import org.andrill.conop.core.Solution

import spock.lang.Specification

class MultiPenaltySpec extends Specification {

	def "scores using all specified objective functions"() {
		given: 'a multi objective'
		def objective = new MultiPenalty()

		and: 'a simulation'
		def simulation = new Simulation(new Properties(), null)
		simulation.setProperty("multi.objectives", "org.andrill.conop.core.objectives.TestPenalty, org.andrill.conop.core.objectives.TestPenalty, org.andrill.conop.core.objectives.TestPenalty")
		objective.configure(simulation)

		when: 'score a soution'
		def score = objective.score(null)

		then: 'score is as expected'
		score == 21
	}
}

class TestPenalty implements ObjectiveFunction {

	@Override
	double score(Solution solution) {
		7
	}
}
