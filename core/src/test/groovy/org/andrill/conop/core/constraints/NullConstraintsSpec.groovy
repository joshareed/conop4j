package org.andrill.conop.core.constraints

import org.andrill.conop.core.Solution
import org.andrill.conop.core.DatasetFixtures

import spock.lang.Specification

class NullConstraintsSpec extends Specification {

	def "always returns true"() {
		given: 'a NullChecker'
		def checker = new NullConstraints()

		expect: 'null is valid'
		checker.isValid(null)

		when: 'a dataset'
		def dataset = DatasetFixtures.simpleDataset()

		then: 'any solution is valid'
		checker.isValid(Solution.initial(dataset))
	}

	def "toString returns as expected"() {
		expect:
		new NullConstraints().toString() == "Null"
	}
}
