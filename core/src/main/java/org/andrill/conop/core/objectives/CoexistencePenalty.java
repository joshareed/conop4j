package org.andrill.conop.core.objectives;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.andrill.conop.core.CoexistenceMatrix;
import org.andrill.conop.core.Event;
import org.andrill.conop.core.Run;
import org.andrill.conop.core.Simulation;
import org.andrill.conop.core.Solution;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Create a penalty based on event coexistence violations.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class CoexistencePenalty extends AbstractParallelObjective {
	static class CoexistenceCheck implements ObjectiveFunction {
		private final Event target;
		private final CoexistenceMatrix runMatrix;
		public CoexistenceMatrix solutionMatrix;

		public CoexistenceCheck(final Event target, final CoexistenceMatrix runMatrix) {
			this.target = target;
			this.runMatrix = runMatrix;
		}

		@Override
		public double score(final Solution solution) {
			double penalty = 0.0;
			for (Event event : solution.getEvents()) {
				int observed = runMatrix.getCoexistence(target, event);
				int proposed = solutionMatrix.getCoexistence(target, event);
				int combined = observed & proposed;
				if (combined == 0) {
					penalty += MAJOR_PENALTY;
				} else if (combined < observed) {
					penalty += MINOR_PENALTY;
				}
			}
			return penalty;
		}
	}

	public static final int MAJOR_PENALTY = 10;
	public static final int MINOR_PENALTY = 4;

	protected Map<Event, CoexistenceCheck> checks = Maps.newHashMap();

	public CoexistencePenalty() {
		super("Coexistence");
	}

	@Override
	public void configure(final Simulation simulation) {
		super.configure(simulation);

		// initialize our coexistence matrix
		Run run = simulation.getRun();
		CoexistenceMatrix matrix = run.getCoexistenceMatrix();
		for (Event e : run.getEvents()) {
			checks.put(e, new CoexistenceCheck(e, matrix));
		}
	}

	@Override
	protected List<Future<Double>> internalScore(final Solution solution) {
		CoexistenceMatrix solutionMatrix = new CoexistenceMatrix(solution);
		List<Future<Double>> results = Lists.newArrayList();
		for (Event e : solution.getEvents()) {
			CoexistenceCheck check = checks.get(e);
			check.solutionMatrix = solutionMatrix;
			results.add(execute(check, solution));
		}
		return results;
	}
}
