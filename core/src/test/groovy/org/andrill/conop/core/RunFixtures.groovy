package org.andrill.conop.core

import org.andrill.conop.core.internal.DefaultEvent;
import org.andrill.conop.core.internal.DefaultObservation;
import org.andrill.conop.core.internal.DefaultRun;
import org.andrill.conop.core.internal.DefaultSection;

class RunFixtures {

	static Run simpleRun() {

		// events
		DefaultEvent e1 = DefaultEvent.createPaired("Fossil 1 LAD", "Fossil 1 FAD")
		DefaultEvent e2 = e1.beforeConstraint
		DefaultEvent e3 = DefaultEvent.createPaired("Fossil 2 LAD", "Fossil 2 FAD")
		DefaultEvent e4 = e3.beforeConstraint
		DefaultEvent e5 = new DefaultEvent("Ash")

		// section
		DefaultSection s1 = new DefaultSection("Section 1", [
			new DefaultObservation(e1, -1, 1.0, 10.0),
			new DefaultObservation(e3, -2, 1.0, 10.0),
			new DefaultObservation(e5, -3, 10.0, 10.0),
			new DefaultObservation(e4, -4, 10.0, 1.0),
			new DefaultObservation(e2, -5, 10.0, 1.0)
		])

		return new DefaultRun([s1])
	}

	static Solution simpleRunBest(Run run = RunFixtures.simpleRun()) {
		DefaultEvent e1 = run.events.find { it.name == "Fossil 1 LAD" }
		DefaultEvent e2 = run.events.find { it.name == "Fossil 1 FAD" }
		DefaultEvent e3 = run.events.find { it.name == "Fossil 2 LAD" }
		DefaultEvent e4 = run.events.find { it.name == "Fossil 2 FAD" }
		DefaultEvent e5 = run.events.find { it.name == "Ash" }

		return new Solution(run, [e1, e3, e5, e4, e2])
	}

	static Solution simpleRunWorst(Run run = RunFixtures.simpleRun()) {
		DefaultEvent e1 = run.events.find { it.name == "Fossil 1 LAD" }
		DefaultEvent e2 = run.events.find { it.name == "Fossil 1 FAD" }
		DefaultEvent e3 = run.events.find { it.name == "Fossil 2 LAD" }
		DefaultEvent e4 = run.events.find { it.name == "Fossil 2 FAD" }
		DefaultEvent e5 = run.events.find { it.name == "Ash" }

		return new Solution(run, [e5, e2, e4, e3, e1])
	}
}
