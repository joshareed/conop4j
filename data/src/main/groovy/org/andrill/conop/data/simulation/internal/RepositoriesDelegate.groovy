package org.andrill.conop.data.simulation.internal

class RepositoriesDelegate {
	def repositories = []

	def local(Map params) {
		repositories << "local: ${params.dir}".toString()
	}
}
