package org.andrill.conop.data.simulation.internal

import org.andrill.conop.data.LocalRepository

class RepositoriesDelegate {
	def repositories = []

	def local(Map params) {
		repositories << new LocalRepository(new File(params.dir))
	}
}
