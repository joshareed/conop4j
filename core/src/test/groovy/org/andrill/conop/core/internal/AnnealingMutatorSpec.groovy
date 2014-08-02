package org.andrill.conop.core.internal

import org.andrill.conop.core.Dataset;
import org.andrill.conop.core.Solution
import org.andrill.conop.core.internal.AnnealingMutator;
import org.andrill.conop.core.internal.DefaultEvent;
import org.andrill.conop.core.test.DatasetFixtures;

import spock.lang.Specification

class AnnealingMutatorSpec extends Specification {
	private Dataset dataset;

	void setup() {
		dataset = DatasetFixtures.simpleDataset()
	}

	def "test mutate"() {
		given: 'a mutator'
		def mutator = new AnnealingMutator()
		mutator.reset = 1

		and: 'a solution'
		def solution = Solution.initial(dataset)

		when: 'mutate'
		def mutated = mutator.mutate(solution)

		then: 'solution differs'
		mutated != solution

		when: 'tried'
		mutator.tried(500, solution, solution)

		then: 'delta updated'
		mutator.temp == 500
		mutator.delta == Math.ceil(Math.log(500))
	}
}
