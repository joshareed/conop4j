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

	def conop9(String dir) {
		repositories << new CONOP9Repository(new File(dir))
		log.info "Registered CONOP9 repository '{}'", dir
	}

	def conop9(Map params) {
		def dir = new File(params.dir ?: '.')
		def sections = new File(dir, params.sections ?: 'sections.sct')
		def events = new File(dir, params.events ?: 'events.evt')
		def observations = new File(dir, params.observations ?: 'loadfile.dat')
		def overrideWeights = params.containsKey('overrideWeights') ? params.overrideWeights : false

		repositories << new CONOP9Repository(sections, events, observations, overrideWeights)

		log.info "Registered CONOP9 repository '{}'", dir
		log.info "Reading sections from '{}'", sections
		log.info "Reading events from '{}'", events
		log.info "Reading observations from '{}'", observations
	}
}
