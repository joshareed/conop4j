package org.andrill.conop4j.scoring;

import org.andrill.conop4j.Solution;

/**
 * Calculates the score for a {@link Solution}.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface ScoringFunction {

	/**
	 * Calculate the score.
	 * 
	 * @param solution
	 *            the solution.
	 * @return the score.
	 */
	double score(Solution solution);
}
