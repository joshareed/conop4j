package org.andrill.conop.search

import spock.lang.Specification

class SolutionSpec extends Specification {
	private Run run;

	void setup() {
		run = Run.loadCONOP9Run(new File('src/test/resources/riley'))
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

		and: 'the rank is opposite of the position'
		solution.getRank(event) == solution.getMinRank(event)
		solution.getRank(event) == solution.getMaxRank(event)
		solution.getRank(event) == 124
	}

	def "can parse a solution"() {
		when: 'parse a solution'
		def solution = Solution.parse(run, new File('src/test/resources/conop4j/solution-riley.csv'))

		then: 'the solution was parsed'
		solution != null

		and: 'expected number of events'
		solution.events.size() == 124

		when: 'find specific event'
		def event = solution.events.find { it.name == 'Dysoristus lochmanae LAD' }

		then: 'event was found'
		event != null

		and: 'position and rank is correct'
		solution.getPosition(event) == 0
		solution.getRank(event) == 124
	}
}
