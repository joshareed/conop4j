package org.andrill.conop.core

class RunFixtures {

	static Run simpleRun() {
		Event.ID = 0

		// events
		Event e1 = Event.createPaired("Fossil 1 LAD", "Fossil 1 FAD")
		Event e2 = e1.beforeConstraint
		Event e3 = Event.createPaired("Fossil 2 LAD", "Fossil 2 FAD")
		Event e4 = e3.beforeConstraint
		Event e5 = new Event("Ash")

		// section
		Section s1 = new Section("Section 1", [
			new Observation(e1, -1, 1.0, 10.0),
			new Observation(e3, -2, 1.0, 10.0),
			new Observation(e5, -3, 10.0, 10.0),
			new Observation(e4, -4, 10.0, 1.0),
			new Observation(e2, -5, 10.0, 1.0)
		])

		return new Run([s1])
	}

	static Solution simpleRunBest(Run run = RunFixtures.simpleRun()) {
		Event e1 = run.events.find { it.name == "Fossil 1 LAD" }
		Event e2 = run.events.find { it.name == "Fossil 1 FAD" }
		Event e3 = run.events.find { it.name == "Fossil 2 LAD" }
		Event e4 = run.events.find { it.name == "Fossil 2 FAD" }
		Event e5 = run.events.find { it.name == "Ash" }

		return new Solution(run, [e1, e3, e5, e4, e2])
	}

	static Solution simpleRunWorst(Run run = RunFixtures.simpleRun()) {
		Event e1 = run.events.find { it.name == "Fossil 1 LAD" }
		Event e2 = run.events.find { it.name == "Fossil 1 FAD" }
		Event e3 = run.events.find { it.name == "Fossil 2 LAD" }
		Event e4 = run.events.find { it.name == "Fossil 2 FAD" }
		Event e5 = run.events.find { it.name == "Ash" }

		return new Solution(run, [e5, e2, e4, e3, e1])
	}
}
