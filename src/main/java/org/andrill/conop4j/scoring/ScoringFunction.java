package org.andrill.conop4j.scoring;

import org.andrill.conop4j.Solution;

/**
 * Calculates the score for a {@link Solution}.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface ScoringFunction {
	/**
	 * The scoring function type.
	 */
	enum Type {
		/**
		 * A PENALTY type indicates the function returns values which should be
		 * minimized for the optimal solution.
		 */
		PENALTY,

		/**
		 * A SCORE type indicates the function returns values which should be
		 * maximized for the optimal solution.
		 */
		SCORE
	}

	/**
	 * Gets the type of scoring function.
	 * 
	 * @return the type.
	 */
	Type getType();

	/**
	 * Calculate the score.
	 * 
	 * @param solution
	 *            the solution.
	 * @return the score.
	 */
	double score(Solution solution);
}
