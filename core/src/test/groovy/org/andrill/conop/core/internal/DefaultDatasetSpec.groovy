package org.andrill.conop.core.internal;

import static org.junit.Assert.*
import spock.lang.Specification

class DefaultDatasetSpec extends Specification {

	def "building a dataset works as expected"() {
		given: "a dataset"
		def dataset = new DefaultDataset([
			new DefaultLocation("Location 1", [
				new DefaultObservation(new DefaultEvent("Event 1"), 1, 1, 1)
			]),
			new DefaultLocation("Location 2", [
				new DefaultObservation(new DefaultEvent("Event 2"), 1, 1, 1)
			])
		])

		expect: "2 locations"
		assert 2 == dataset.locations.size()

		and: "2 events"
		assert 2 == dataset.events.size()

		and: "events have ids"
		assert dataset.getId(new DefaultEvent("Event 1")) in 0..1
		assert dataset.getId(new DefaultEvent("Event 2")) in 0..1
	}

	def "can build a canonical dataset"() {
		given: "a dataset"
		def dataset = new DefaultDataset([
			new DefaultLocation("Location 1", [
				new DefaultObservation(new DefaultEvent("Event 1"), 1, 1, 1),
				new DefaultObservation(new DefaultEvent("Event 2"), 1, 1, 1)
			]),
			new DefaultLocation("Location 2", [
				new DefaultObservation(new DefaultEvent("Event 1"), 1, 1, 1),
				new DefaultObservation(new DefaultEvent("Event 2"), 1, 1, 1)
			])
		])

		expect: "canonical"
		assert dataset.canonical
	}
}
