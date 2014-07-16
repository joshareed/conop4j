package org.andrill.conop.data.simulation.internal

class DataDelegate {
	def locations = []

	def include(String id) {
		locations << id
	}
}
