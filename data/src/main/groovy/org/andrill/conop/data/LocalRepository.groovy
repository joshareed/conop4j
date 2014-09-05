package org.andrill.conop.data

import groovy.util.logging.Slf4j

import org.andrill.conop.core.Location
import org.andrill.conop.data.internal.AbstractJsonRepository

@Slf4j
class LocalRepository extends AbstractJsonRepository {
	protected File root

	LocalRepository(File root) {
		this.root = root
	}

	@Override
	protected String fetch(String id) {
		def (program, site) = id.split(':')

		// build our full path
		def file = new File(new File(root, program), "${site}.json")
		if (!file.exists() || !file.file) {
			return null
		} else {
			log.info "Using location '{}' from '{}'", id, file.absolutePath
			return file.text
		}
	}
}
