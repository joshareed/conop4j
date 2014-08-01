package org.andrill.conop.core.constraints;

import org.andrill.conop.core.Solution;

/**
 * Determines whether a {@link Solution} satisfies all event and dataset
 * constraints.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface Constraints {

	/**
	 * Check whether the specified solution satisfies all constraints.
	 * 
	 * @param solution
	 *            the solution.
	 * @return true if all constraints are satisfied, false otherwise.
	 */
	boolean isValid(Solution solution);
}
