package org.andrill.conop.core.objectives

import org.andrill.conop.core.penalties.RelativeOrderingPenalty;
import org.andrill.conop.core.test.DatasetFixtures;

import spock.lang.Specification

class RelativeOrderingPenaltySpec extends Specification {

	def "scoring a simple dataset"() {
		given: 'a possible solution'
		def initial = DatasetFixtures.simpleDatasetBest()

		and: 'the objective function'
		def objective = new RelativeOrderingPenalty()

		when: 'score the solution'
		def score = objective.score(initial)

		then: 'the score is zero'
		score == 0
	}
}
