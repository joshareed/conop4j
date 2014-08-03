package org.andrill.conop.core.objectives

import org.andrill.conop.core.internal.DefaultSolverContext
import org.andrill.conop.core.penalties.MatrixPenalty
import org.andrill.conop.core.test.DatasetFixtures

import spock.lang.Specification

class MatrixPenaltySpec extends Specification {

	def "scoring a simple dataset"() {
		given: 'a dataset'
		def dataset = DatasetFixtures.simpleDataset()

		and: 'a possible solution'
		def initial = DatasetFixtures.simpleDatasetBest(dataset)

		and: 'a context'
		def context = new DefaultSolverContext()
		context.dataset = dataset

		and: 'the penalty function'
		def penalty = new MatrixPenalty()
		penalty.context = context

		when: 'score the solution'
		def score = penalty.score(initial)

		then: 'the score is zero'
		score == 0
	}
}
