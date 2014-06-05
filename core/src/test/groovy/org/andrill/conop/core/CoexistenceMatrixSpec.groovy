package org.andrill.conop.core

import static org.andrill.conop.core.CoexistenceMatrix.*

import org.andrill.conop.core.CoexistenceMatrix;
import org.andrill.conop.core.DefaultEvent;
import org.andrill.conop.core.Run;
import org.andrill.conop.core.Solution;

import spock.lang.Specification

class CoexistenceMatrixSpec extends Specification {

	def "coexistence is computed correctly"() {
		when: 'compute coexistence'
		def computed = CoexistenceMatrix.computeCoexistence(start1, end1, start2, end2)

		then: 'computed is expected'
		computed == expected

		where:
		start1 | end1 | start2 | end2 | expected
		null   | null | null   | null | MASK
		0      | 5    | 10     | 15   | (DISJUNCT + DISJUNCT_BEFORE)
		10     | 15   | 0      | 5    | (DISJUNCT + DISJUNCT_AFTER)
		0      | 10   | 5      | 15   | (CONJUNCT + CONJUNCT_BEFORE)
		10     | 25   | 5      | 15   | (CONJUNCT + CONJUNCT_AFTER)
		0      | 20   | 5      | 15   | (CONJUNCT + CONJUNCT_CONTAINS)
		5      | 15   | 0      | 20   | (CONJUNCT + CONJUNCT_CONTAINED)
	}


	def "Coexistence & Coexistence is computed correctly"() {
		expect:
		(existing & computed) == expected

		where:
		existing                       | computed                    | expected
		MASK                           | MASK                        | MASK
		CONJUNCT                       | MASK                        | CONJUNCT
		MASK                           | DISJUNCT                    | DISJUNCT
		CONJUNCT                       | CONJUNCT                    | CONJUNCT
		DISJUNCT                       | DISJUNCT                    | DISJUNCT
		CONJUNCT                       | DISJUNCT                    | NONE
		(DISJUNCT + DISJUNCT_BEFORE)   | (DISJUNCT + DISJUNCT_AFTER) | DISJUNCT
		(CONJUNCT + CONJUNCT_BEFORE)   | (CONJUNCT + CONJUNCT_AFTER) | CONJUNCT
		(CONJUNCT + CONJUNCT_CONTAINS) | (CONJUNCT + CONJUNCT_AFTER) | CONJUNCT
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
		matrix.getCoexistence(e1, e2) == CONJUNCT + CONJUNCT_CONTAINS

		and:
		matrix.getCoexistence(e1, e3) == DISJUNCT + DISJUNCT_AFTER

		and:
		matrix.getCoexistence(e1, e4) == NONE
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
		matrix.getCoexistence(e1, e2) == CONJUNCT + CONJUNCT_BEFORE

		and:
		matrix.getCoexistence(e1, e3) == DISJUNCT + DISJUNCT_BEFORE
	}
}
