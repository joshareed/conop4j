package org.andrill.conop.data;

import static org.junit.Assert.*
import spock.lang.Specification

class LocalRepositorySpec extends Specification {

	def "can fetch a file"() {
		given: "a local repo"
		def repo = new LocalRepository(new File('src/test/resources/repos/local/'))

		when: "invalid location"
		def invalid = repo.getLocation("invalid:id")

		then:
		assert null == invalid

		when: "valid lcoation"
		def and1b = repo.getLocation("andrill:1b")

		then:
		assert and1b
	}
}
