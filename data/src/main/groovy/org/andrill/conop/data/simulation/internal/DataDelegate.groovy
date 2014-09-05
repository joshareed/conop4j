package org.andrill.conop.data.simulation.internal

class DataDelegate {
	def locations = []
	def all = false

	def location(Map params) {
		locations << "${params.program}:${params.id}"
	}

	def location(String id) {
		locations << id
	}

	def all() {
		all = true
	}
}
