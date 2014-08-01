package org.andrill.conop.core.objectives

import org.andrill.conop.core.DatasetFixtures
import org.andrill.conop.core.penalties.MatrixPenalty;

import spock.lang.Specification

class MatrixPenaltySpec extends Specification {

	def "scoring a simple dataset"() {
		given: 'a possible solution'
		def initial = DatasetFixtures.simpleDatasetBest()

		and: 'the objective function'
		def objective = new MatrixPenalty()

		when: 'score the solution'
		def score = objective.score(initial)

		then: 'the score is zero'
		score == 0
	}
}
