package org.andrill.conop.core.mutators

import org.andrill.conop.core.mutators.AbstractMutator;
import org.andrill.conop.core.Event
import org.andrill.conop.core.Run
import org.andrill.conop.core.Simulation
import org.andrill.conop.core.Solution

import spock.lang.Specification

class AbstractMutatorSpec extends Specification {
	private Run run

	void setup() {
		Event.ID = 0
		run = Run.loadCONOP9Run(new File('src/test/resources/riley'))
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
		def simulation = new Simulation(new Properties(), run)
		simulation.setProperty('mutator.reset', '1')

		when: 'configure'
		mutator.configure(simulation)

		then:
		mutator.reset == 1
	}

	def "test tried"() {
		given: 'a mutator'
		def mutator = new TestMutator()

		and: 'a solution'
		def solution = Solution.initial(run)
		solution.score = 1000

		when: 'tried 1'
		mutator.tried(1000, solution, solution)

		then:
		mutator.temp == 1000
		mutator.local == solution
		mutator.counter == 0

		when: 'tried 2'
		mutator.tried(500, solution, solution)

		then:
		mutator.temp == 500
		mutator.local == solution
		mutator.counter == 1
	}

	def "test reset"() {
		given: 'a mutator'
		def mutator = new TestMutator()
		mutator.reset = 1

		and: 'a solution'
		def solution = Solution.initial(run)
		solution.score = 1000

		when: 'tried 1'
		mutator.tried(1000, solution, solution)

		and: 'mutate 1'
		mutator.mutate(solution)

		then:
		mutator.counter == 0

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
