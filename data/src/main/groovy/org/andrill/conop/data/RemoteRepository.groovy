package org.andrill.conop.data

import groovy.util.logging.Slf4j

import org.andrill.conop.data.internal.AbstractJsonRepository

@Slf4j
class RemoteRepository extends AbstractJsonRepository {
	protected URL root

	RemoteRepository(URL root) {
		this.root = root
	}

	@Override
	protected String fetch(String id) {
		def (program, site) = id.split(':')

		def url = new URL(root, "${program}/${site}.json")
		try {
			log.info "Using location '{}' from '{}'", id, url
			return url.text
		} catch (e) {
			return null
		}
	}
}
