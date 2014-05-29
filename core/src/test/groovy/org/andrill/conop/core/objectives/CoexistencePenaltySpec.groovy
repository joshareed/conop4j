package org.andrill.conop.core.objectives

import org.andrill.conop.core.Simulation
import org.andrill.conop.core.RunFixtures

import spock.lang.Specification

class CoexistencePenaltySpec extends Specification {

	def "scoring a simple run"() {
		given: 'a run'
		def run = RunFixtures.simpleRun()

		and: 'a simulation'
		def simulation = new Simulation(new Properties(), run)

		and: 'the placement penalty'
		def objective = new CoexistencePenalty()
		objective.configure(simulation)

		and: 'a possible solution'
		def initial = RunFixtures.simpleRunBest(run)

		when: 'score the solution'
		def score = objective.score(initial)

		then: 'the score is zero'
		score == 0
	}
}
