package org.andrill.conop.core.mutators

import org.andrill.conop.core.mutators.RandomMutator;
import org.andrill.conop.core.Event
import org.andrill.conop.core.Run
import org.andrill.conop.core.Solution

import spock.lang.Specification

class RandomMutatorSpec extends Specification {
	private Run run;

	void setup() {
		Event.ID = 0
		run = Run.loadCONOP9Run(new File('src/test/resources/riley'))
	}

	def "test mutate"() {
		given: 'a mutator'
		def mutator = new RandomMutator()
		mutator.reset = 1

		and: 'a solution'
		def solution = Solution.initial(run)

		when: 'mutate'
		def mutated = mutator.mutate(solution)

		then: 'solution differs'
		mutated != solution
		mutated.hash() != solution.hash()
	}
}
