package org.andrill.conop.core.solver;

import org.andrill.conop.core.HaltedException;
import org.andrill.conop.core.Dataset;
import org.andrill.conop.core.Solution;

public interface Solver {

	/**
	 * Find a solution using the specified configuration and dataset.
	 *
	 * @param config
	 *            the solver configuration.
	 * @param dataset
	 *            the dataset.
	 * @return the best solution found.
	 * @throws HaltedException
	 */
	public Solution solve(SolverConfiguration config, Dataset dataset) throws HaltedException;
}
