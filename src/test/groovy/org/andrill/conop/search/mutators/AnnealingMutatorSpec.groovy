package org.andrill.conop.search.mutators

import org.andrill.conop.search.Event
import org.andrill.conop.search.Run
import org.andrill.conop.search.Solution

import spock.lang.Specification

class AnnealingMutatorSpec extends Specification {
	private Run run;

	void setup() {
		Event.ID = 0
		run = Run.loadCONOP9Run(new File('src/test/resources/riley'))
	}

	def "test mutate"() {
		given: 'a mutator'
		def mutator = new AnnealingMutator()
		mutator.reset = 1

		and: 'a solution'
		def solution = Solution.initial(run)

		when: 'mutate'
		def mutated = mutator.mutate(solution)

		then: 'solution differs'
		mutated != solution
		mutated.hash() != solution.hash()

		when: 'tried'
		mutator.tried(500, solution, solution)

		then: 'delta updated'
		mutator.temp == 500
		mutator.delta == Math.ceil(Math.log(500))
	}
}