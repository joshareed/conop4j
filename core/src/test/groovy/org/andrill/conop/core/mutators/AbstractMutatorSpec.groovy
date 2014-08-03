package org.andrill.conop.core.mutators

import org.andrill.conop.core.Configuration
import org.andrill.conop.core.Dataset
import org.andrill.conop.core.Solution
import org.andrill.conop.core.internal.DefaultSolverContext
import org.andrill.conop.core.test.DatasetFixtures;

import spock.lang.Specification

class AbstractMutatorSpec extends Specification {
	private Dataset dataset

	void setup() {
		dataset = DatasetFixtures.simpleDataset()
	}

	def "test initial setup"() {
		given: 'a mutator'
		def mutator = new TestMutator()

		expect:
		mutator.toString() == "Test"
		mutator.reset == -1
	}

	def "test configure"() {
		given: 'a mutator'
		def mutator = new TestMutator()

		and: 'a simulation'
		def config = new Configuration('reset': 1)

		when: 'configure'
		mutator.configure(config)

		then:
		mutator.reset == 1
	}

	def "test tried"() {
		given: 'a mutator'
		def mutator = new TestMutator()

		and: 'a solution'
		def solution = Solution.initial(dataset)
		solution.score = 1000

		when: 'tried 1'
		mutator.tried(1000, solution, solution)

		then:
		mutator.counter == 1

		when: 'tried 2'
		mutator.tried(500, solution, solution)

		then:
		mutator.counter == 2
	}

	def "test reset"() {
		given: 'a mutator'
		def mutator = new TestMutator()
		mutator.reset = 1
		mutator.context = new DefaultSolverContext()

		and: 'a solution'
		def solution = Solution.initial(dataset)
		solution.score = 1000

		when: 'tried 1'
		mutator.tried(1000, solution, solution)

		and: 'mutate 1'
		mutator.mutate(solution)

		then:
		mutator.counter == 1

		and:
		mutator.called == 1

		when: 'mutate 2'
		mutator.counter = 10
		mutator.mutate(solution)

		then:
		mutator.counter == 0

		and:
		mutator.called == 2
	}
}

class TestMutator extends AbstractMutator {
	int called = 0

	TestMutator() {
		super("Test")
	}

	Solution internalMutate(final Solution solution) {
		called++
		solution
	}
}
