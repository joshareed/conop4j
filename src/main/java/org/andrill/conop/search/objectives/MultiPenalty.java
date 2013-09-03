package org.andrill.conop.search.objectives;

import java.util.List;
import java.util.concurrent.Future;

import org.andrill.conop.search.Simulation;
import org.andrill.conop.search.Solution;

import com.google.common.collect.Lists;

public class MultiPenalty extends AbstractParallelObjective {
	protected List<ObjectiveFunction> objectives;

	public MultiPenalty() {
		super("Multi");
	}

	@Override
	public void configure(final Simulation simulation) {
		super.configure(simulation);

		objectives = Lists.newArrayList();
		for (String name : simulation.getProperty("multi.objectives", "").split(",")) {
			ObjectiveFunction objective = simulation.lookup(name, ObjectiveFunction.class, Simulation.OBJECTIVES);
			if (objective != null) {
				objectives.add(objective);
			}
		}
	}

	@Override
	protected List<Future<Double>> internalScore(final Solution solution) {
		List<Future<Double>> results = Lists.newArrayList();
		for (ObjectiveFunction penalty : objectives) {
			results.add(execute(penalty, solution));
		}
		return results;
	}
}
