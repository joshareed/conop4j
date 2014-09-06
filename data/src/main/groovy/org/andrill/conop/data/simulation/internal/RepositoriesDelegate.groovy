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

	def conop9(String path) {
		if (path.contains('http')) {
			repositories << new CONOP9Repository(new URL(path))
		} else {
			repositories << new CONOP9Repository(new File(path))
		}
		log.info "Registered CONOP9 repository '{}'", path
	}

	def conop9(Map params) {
		def sections = (params.sections ?: params.location) ?: CONOP9Repository.LOCATIONS_FILE
		def events = params.events ?: CONOP9Repository.EVENTS_FILE
		def observations = params.observations ?: CONOP9Repository.OBSERVATIONS_FILE
		def overrideWeights = params.containsKey('overrideWeights') ? params.overrideWeights : false

		if (params.dir) {
			def dir = new File(params.dir)
			repositories << new CONOP9Repository(new File(dir, sections), new File(dir, events), new File(dir, observations), overrideWeights)
			log.info "Registered CONOP9 repository '{}'", params.dir
			log.info "Reading sections from '{}'", sections
			log.info "Reading events from '{}'", events
			log.info "Reading observations from '{}'", observations
		} else if (params.url) {
			def url = new URL(params.url)
			repositories << new CONOP9Repository(new URL(url, sections), new URL(url, events), new URL(url, observations), overrideWeights)
			log.info "Registered CONOP9 repository '{}'", params.url
			log.info "Reading sections from '{}'", sections
			log.info "Reading events from '{}'", events
			log.info "Reading observations from '{}'", observations
		} else {
			log.warn "Did not specify a dir or a url so skipped"
		}
	}
}
