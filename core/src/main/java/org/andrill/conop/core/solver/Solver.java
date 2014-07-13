package org.andrill.conop.core.solver;

import org.andrill.conop.core.HaltedException;
import org.andrill.conop.core.Run;
import org.andrill.conop.core.Solution;

public interface Solver {

	public Solution solve(SolverConfiguration config, Run run) throws HaltedException;
}
