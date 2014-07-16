package org.andrill.conop.data

import org.andrill.conop.data.internal.AbstractJsonRepository

class LocalRepository extends AbstractJsonRepository {
	protected File root

	LocalRepository(File root) {
		this.root = root
	}

	@Override
	protected String fetch(String id) {
		def (program, site) = id.split(':')

		// get our program directory
		def dir = new File(root, program)
		if (!dir.exists() || !dir.directory) {
			return null
		}

		def file = new File(dir, "${site}.json")
		if (!file.exists() || !file.file) {
			return null
		}

		return file.text;
	}
}
