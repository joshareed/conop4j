package org.andrill.conop.core.mutators

import org.andrill.conop.core.Run;
import org.andrill.conop.core.Solution
import org.andrill.conop.core.RunFixtures
import org.andrill.conop.core.internal.DefaultEvent;
import org.andrill.conop.core.mutators.internal.AnnealingMutator;

import spock.lang.Specification

class AnnealingMutatorSpec extends Specification {
	private Run run;

	void setup() {
		run = RunFixtures.simpleRun()
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

		when: 'tried'
		mutator.tried(500, solution, solution)

		then: 'delta updated'
		mutator.temp == 500
		mutator.delta == Math.ceil(Math.log(500))
	}
}
