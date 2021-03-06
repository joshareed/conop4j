package org.andrill.conop.core.test

import org.andrill.conop.core.Dataset
import org.andrill.conop.core.Solution
import org.andrill.conop.core.internal.DefaultDataset
import org.andrill.conop.core.internal.DefaultEvent
import org.andrill.conop.core.internal.DefaultLocation
import org.andrill.conop.core.internal.DefaultObservation

class DatasetFixtures {

	static Dataset simpleDataset() {

		// events
		DefaultEvent e1 = new DefaultEvent("Fossil 1 LAD")
		DefaultEvent e2 = new DefaultEvent("Fossil 1 FAD")
		DefaultEvent e3 = new DefaultEvent("Fossil 2 LAD")
		DefaultEvent e4 = new DefaultEvent("Fossil 2 FAD")
		DefaultEvent e5 = new DefaultEvent("Ash")

		// section
		DefaultLocation s1 = new DefaultLocation("Section 1", [
			new DefaultObservation(e1, -1, 1.0, 10.0),
			new DefaultObservation(e3, -2, 1.0, 10.0),
			new DefaultObservation(e5, -3, 10.0, 10.0),
			new DefaultObservation(e4, -4, 10.0, 1.0),
			new DefaultObservation(e2, -5, 10.0, 1.0)
		])

		return new DefaultDataset([s1])
	}

	static Solution simpleDatasetBest(Dataset dataset = DatasetFixtures.simpleDataset()) {
		DefaultEvent e1 = dataset.events.find { it.name == "Fossil 1 LAD" }
		DefaultEvent e2 = dataset.events.find { it.name == "Fossil 1 FAD" }
		DefaultEvent e3 = dataset.events.find { it.name == "Fossil 2 LAD" }
		DefaultEvent e4 = dataset.events.find { it.name == "Fossil 2 FAD" }
		DefaultEvent e5 = dataset.events.find { it.name == "Ash" }

		return new Solution([e1, e3, e5, e4, e2])
	}

	static Solution simpleDatasetWorst(Dataset dataset = DatasetFixtures.simpleDataset()) {
		DefaultEvent e1 = dataset.events.find { it.name == "Fossil 1 LAD" }
		DefaultEvent e2 = dataset.events.find { it.name == "Fossil 1 FAD" }
		DefaultEvent e3 = dataset.events.find { it.name == "Fossil 2 LAD" }
		DefaultEvent e4 = dataset.events.find { it.name == "Fossil 2 FAD" }
		DefaultEvent e5 = dataset.events.find { it.name == "Ash" }

		return new Solution([e5, e2, e4, e3, e1])
	}
}
