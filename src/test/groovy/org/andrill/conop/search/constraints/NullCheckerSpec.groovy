package org.andrill.conop.search.constraints

import org.andrill.conop.search.Run
import org.andrill.conop.search.Solution

import spock.lang.Specification

class NullCheckerSpec extends Specification {

	def "always returns true"() {
		given: 'a NullChecker'
		def checker = new NullChecker()

		expect: 'null is valid'
		checker.isValid(null)

		when: 'a run'
		def run = Run.loadCONOP9Run(new File('src/test/resources/riley'))

		then: 'any solution is valid'
		checker.isValid(Solution.initial(run))
	}

	def "toString returns as expected"() {
		expect:
		new NullChecker().toString() == "Null"
	}
}
