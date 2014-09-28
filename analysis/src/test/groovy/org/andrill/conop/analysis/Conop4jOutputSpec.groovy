package org.andrill.conop.analysis;

import static org.junit.Assert.*

import org.junit.Test

import spock.lang.Specification

class Conop4jOutputSpec extends Specification {

	@Test
	void "test conop4j output parsing"() {
		when: "parse output"
		def output = new Conop4jOutput(new File("src/test/resources/test.json").text)

		then: "get a dataset"
		output.dataset != null

		and: "get a solution"
		output.annotatedSolution != null
		output.annotatedSolution.score == 1765
	}
}
