package org.andrill.conop.core

import org.andrill.conop.core.test.DatasetFixtures

import spock.lang.Specification

class SolutionSpec extends Specification {
	private Dataset dataset;

	void setup() {
		dataset = DatasetFixtures.simpleDataset()
	}

	def "creating an initial solution for a dataset works"() {
		when:
		def solution = Solution.initial(dataset)

		then: 'the solution exists'
		solution != null

		and: 'same number of events'
		dataset.events.size() == solution.events.size()

		and: 'the dataset is set'
		solution.dataset == dataset

		and: 'score is -1'
		solution.score == -1
	}

	def "setScore updates ranks"() {
		given: 'a solution'
		def solution = Solution.initial(dataset)

		when: 'set score'
		solution.setScore(1000.0)

		then: 'the score is saved'
		solution.score == 1000.0

		when: 'get a specific event'
		def event = solution.getEvent(0)

		then: 'the position matches'
		solution.getPosition(event) == 0
	}
}
