package org.andrill.conop.core.objectives

import org.andrill.conop.core.RunFixtures

import spock.lang.Specification

class PlacementPenaltySpec extends Specification {

	def "scoring a simple run"() {
		given: 'a possible solution'
		def initial = RunFixtures.simpleRunBest()

		and: 'the objective function'
		def objective = new PlacementPenalty()

		when: 'score the solution'
		def score = objective.score(initial)

		then: 'the score is zero'
		score == 0
	}
}
