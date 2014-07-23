package org.andrill.conop.core.penalties;

import org.andrill.conop.core.Solution;

/**
 * Calculates the penalty for a {@link Solution}.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface Penalty {

	/**
	 * Calculate the score.
	 * 
	 * @param solution
	 *            the solution.
	 * @return the score.
	 */
	double score(Solution solution);
}
