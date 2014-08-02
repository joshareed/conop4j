package org.andrill.conop.core.solver;

import org.andrill.conop.core.Dataset;
import org.andrill.conop.core.HaltedException;

public interface Solver {

	/**
	 * Find a solution using the specified configuration and dataset.
	 *
	 * @param config
	 *            the solver configuration.
	 * @param dataset
	 *            the dataset.
	 * @return the solver context.
	 * @throws HaltedException
	 */
	public SolverContext solve(SolverConfiguration config, Dataset dataset)
			throws HaltedException;
}
