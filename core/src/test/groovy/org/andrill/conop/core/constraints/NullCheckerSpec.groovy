package org.andrill.conop.core.constraints

import org.andrill.conop.core.Solution
import org.andrill.conop.core.RunFixtures

import spock.lang.Specification

class NullCheckerSpec extends Specification {

	def "always returns true"() {
		given: 'a NullChecker'
		def checker = new NullConstraints()

		expect: 'null is valid'
		checker.isValid(null)

		when: 'a run'
		def run = RunFixtures.simpleRun()

		then: 'any solution is valid'
		checker.isValid(Solution.initial(run))
	}

	def "toString returns as expected"() {
		expect:
		new NullConstraints().toString() == "Null"
	}
}
