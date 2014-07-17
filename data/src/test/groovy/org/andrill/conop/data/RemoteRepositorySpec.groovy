package org.andrill.conop.data;

import static org.junit.Assert.*
import spock.lang.Specification

class RemoteRepositorySpec extends Specification {

	def "can fetch a file"() {
		given: "a remote repo"
		def repo = new RemoteRepository(new URL('https://raw.githubusercontent.com/joshareed/conop4j/develop/data/src/test/resources/repos/local/'))

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
