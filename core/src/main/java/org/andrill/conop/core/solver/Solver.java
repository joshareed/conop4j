package org.andrill.conop.core.solver;

import org.andrill.conop.core.HaltedException;
import org.andrill.conop.core.Run;
import org.andrill.conop.core.Solution;

public interface Solver {

	/**
	 * Find a solution using the specified configuration and run.
	 *
	 * @param config
	 *            the solver configuration.
	 * @param run
	 *            the run.
	 * @return the best solution found.
	 * @throws HaltedException
	 */
	public Solution solve(SolverConfiguration config, Run run) throws HaltedException;
}
