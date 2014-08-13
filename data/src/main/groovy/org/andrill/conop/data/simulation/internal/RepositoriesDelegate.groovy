package org.andrill.conop.data.simulation.internal

import groovy.util.logging.Slf4j

import org.andrill.conop.data.CONOP9Repository
import org.andrill.conop.data.LocalRepository
import org.andrill.conop.data.RemoteRepository

@Slf4j
class RepositoriesDelegate {
	def repositories = []

	def local(Map params) {
		repositories << new LocalRepository(new File(params.dir))
		log.info "Registered local repository '{}'", params.dir
	}

	def local(String dir) {
		repositories << new LocalRepository(new File(dir))
		log.info "Registered local repository '{}'", dir
	}

	def remote(Map params) {
		repositories << new RemoteRepository(new URL(params.url))
		log.info "Registered remote repository '{}'", params.url
	}

	def remote(String url) {
		repositories << new RemoteRepository(new URL(url))
		log.info "Registered remote repository '{}'", url
	}

	def conop9(Map params) {
		if (params.dir) {
			repositories << new CONOP9Repository(new File(params.dir))
			log.info "Registered CONOP9 repository '{}'", params.dir
		} else {
			repositories << new CONOP9Repository(new File(params.sections),
					new File(params.events), new File(params.observations))
			log.info "Registered CONOP9 repository using '{}', '{}', and '{}'", params.sections, params.events, params.observations
		}
	}
}
