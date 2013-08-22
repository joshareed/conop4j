package org.andrill.conop.search.constraints;

import org.andrill.conop.search.Solution;

/**
 * Determines whether a {@link Solution} satisfies all event and run
 * constraints.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface ConstraintChecker {

	/**
	 * Check whether the specified solution satisfies all constraints.
	 * 
	 * @param solution
	 *            the solution.
	 * @return true if all constraints are satisfied, false otherwise.
	 */
	boolean isValid(Solution solution);
}
