package org.andrill.conop.core.objectives;

import org.andrill.conop.core.Solution;

/**
 * Calculates the penalty for a {@link Solution}.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface ObjectiveFunction {

	/**
	 * Calculate the score.
	 * 
	 * @param solution
	 *            the solution.
	 * @return the score.
	 */
	double score(Solution solution);
}
