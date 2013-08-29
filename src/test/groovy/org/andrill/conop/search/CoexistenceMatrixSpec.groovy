package org.andrill.conop.search

import static org.andrill.conop.search.CoexistenceMatrix.Coexistence.*
import spock.lang.Specification

class CoexistenceMatrixSpec extends Specification {

	def setup() {
		Event.ID = 0;
	}

	def "coexistence is computed correctly"() {
		when: 'compute coexistence'
		def computed = CoexistenceMatrix.computeCoexistence(start1, end1, start2, end2)

		then: 'computed is expected'
		computed == expected

		where:
		start1 | end1 | start2 | end2 | expected
		null   | null | null   | null | ABSENT
		0      | 10   | 5      | 10   | CONJUNCT
		0      | 5    | 10     | 15   | DISJUNCT
		0      | 10   | 0      | 0    | CONJUNCT
		10     | 10   | 10     | 10   | CONJUNCT
	}

	def "Coexistence & Coexistence is computed correctly"() {
		expect:
		existing.and(computed) == expected

		where:
		existing | computed | expected
		ABSENT   | ABSENT   | ABSENT
		ABSENT   | CONJUNCT | MIXED
		ABSENT   | DISJUNCT | MIXED
		CONJUNCT | ABSENT   | CONJUNCT
		CONJUNCT | CONJUNCT | CONJUNCT
		CONJUNCT | DISJUNCT | MIXED
		DISJUNCT | ABSENT   | DISJUNCT
		DISJUNCT | DISJUNCT | DISJUNCT
		DISJUNCT | CONJUNCT | MIXED
	}

	def "can create a coexistence matrix from a run"() {
		given: 'a matrix'
		def run = Run.loadCONOP9Run(new File('src/test/resources/riley'))
		def matrix = run.coexistenceMatrix

		when: 'events'
		def e1 = run.events.find { it.name == 'Dysoristus lochmanae LAD' }
		def e2 = run.events.find { it.name == 'Apsotreta expansus LAD' }
		def e3 = run.events.find { it.name == 'Llanoaspis peculiaris FAD' }
		def e4 = run.events.find { it.name == 'Sponge spicule C LAD' }

		then:
		matrix.getCoexistence(e1, e2) == CONJUNCT

		and:
		matrix.getCoexistence(e1, e3) == DISJUNCT

		and:
		matrix.getCoexistence(e1, e4) == ABSENT
	}

	def "can create a coexistence matrix from a solution"() {
		given: 'a matrix'
		def run = Run.loadCONOP9Run(new File('src/test/resources/riley'))
		def solution = Solution.parse(run, new File('src/test/resources/conop4j/solution-riley.csv'))
		def matrix = new CoexistenceMatrix(solution)

		when: 'events'
		def e1 = run.events.find { it.name == 'Dysoristus lochmanae LAD' }
		def e2 = run.events.find { it.name == 'Apsotreta expansus LAD' }
		def e3 = run.events.find { it.name == 'Llanoaspis peculiaris FAD' }

		then:
		matrix.getCoexistence(e1, e2) == CONJUNCT

		and:
		matrix.getCoexistence(e1, e3) == DISJUNCT
	}
}
