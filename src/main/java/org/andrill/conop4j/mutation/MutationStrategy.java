package org.andrill.conop4j.mutation;

import org.andrill.conop4j.Solution;

/**
 * Mutates a solution into another potential {@link Solution}.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface MutationStrategy {

	/**
	 * Mutates the specified solution into a new solution.
	 * 
	 * @param solution
	 *            the existing solution.
	 * @return the new solution.
	 */
	Solution mutate(Solution solution);
}
