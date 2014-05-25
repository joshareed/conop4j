package org.andrill.conop.core.constraints

import org.andrill.conop.core.constraints.NullChecker;
import org.andrill.conop.core.Run
import org.andrill.conop.core.Solution

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
