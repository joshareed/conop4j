package org.andrill.conop.core

import spock.lang.Specification

class SolutionSpec extends Specification {
	private Run run;

	void setup() {
		run = RunFixtures.simpleRun()
	}

	def "creating an initial solution for a run works"() {
		when:
		def solution = Solution.initial(run)

		then: 'the solution exists'
		solution != null

		and: 'same number of events'
		run.events.size() == solution.events.size()

		and: 'the run is set'
		solution.run == run

		and: 'score is 0'
		solution.score == 0.0

		and: 'the hash is not null'
		solution.hash() != null
	}

	def "setScore updates ranks"() {
		given: 'a solution'
		def solution = Solution.initial(run)

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
