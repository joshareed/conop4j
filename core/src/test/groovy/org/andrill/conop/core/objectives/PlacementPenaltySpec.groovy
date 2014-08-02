package org.andrill.conop.core.objectives

import org.andrill.conop.core.penalties.PlacementPenalty;
import org.andrill.conop.core.test.DatasetFixtures;

import spock.lang.Specification

class PlacementPenaltySpec extends Specification {

	def "scoring a simple dataset"() {
		given: 'a possible solution'
		def initial = DatasetFixtures.simpleDatasetBest()

		and: 'the objective function'
		def objective = new PlacementPenalty()

		when: 'score the solution'
		def score = objective.score(initial)

		then: 'the score is zero'
		score == 0
	}
}
