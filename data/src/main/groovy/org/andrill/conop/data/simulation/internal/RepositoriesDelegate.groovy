package org.andrill.conop.data.simulation.internal

import org.andrill.conop.data.LocalRepository
import org.andrill.conop.data.RemoteRepository

class RepositoriesDelegate {
	def repositories = []

	def local(Map params) {
		repositories << new LocalRepository(new File(params.dir))
	}

	def local(String dir) {
		repositories << new LocalRepository(new File(dir))
	}

	def remote(Map params) {
		repositories << new RemoteRepository(new URL(params.url))
	}

	def remote(String url) {
		repositories << new RemoteRepository(new URL(url))
	}
}
