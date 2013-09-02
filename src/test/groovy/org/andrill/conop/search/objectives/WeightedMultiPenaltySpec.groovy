package org.andrill.conop.search.objectives

import org.andrill.conop.search.Simulation
import org.andrill.conop.search.Solution

import spock.lang.Specification

class WeightedMultiPenaltySpec extends Specification {

	def "scores using all specified objective functions"() {
		given: 'a multi objective'
		def objective = new WeightedMultiPenalty()

		and: 'a simulation'
		def simulation = new Simulation(new Properties(), null)
		simulation.setProperty("multi.objectives", "org.andrill.conop.search.objectives.TestPenalty, org.andrill.conop.search.objectives.TestPenalty, org.andrill.conop.search.objectives.TestPenalty")
		objective.configure(simulation)

		when: 'score a soution'
		def score = objective.score(null)

		then: 'score is as expected'
		score == 21
	}
}

class TestPenalty implements ObjectiveFunction {
	int score = 7;

	@Override
	public double score(Solution solution) {
		return score;
	}
}
