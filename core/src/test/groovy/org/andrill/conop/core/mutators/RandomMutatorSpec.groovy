package org.andrill.conop.core.mutators

import org.andrill.conop.core.Dataset
import org.andrill.conop.core.Solution
import org.andrill.conop.core.test.DatasetFixtures

import spock.lang.Specification

class RandomMutatorSpec extends Specification {
	private Dataset dataset;

	void setup() {
		dataset = DatasetFixtures.simpleDataset()
	}

	def "test mutate"() {
		given: 'a mutator'
		def mutator = new RandomMutator()
		mutator.reset = 1

		and: 'a solution'
		def solution = Solution.initial(dataset)

		when: 'mutate'
		def mutated = mutator.mutate(solution)

		then: 'solution differs'
		mutated != solution
		mutated.events != solution.events
	}
}
