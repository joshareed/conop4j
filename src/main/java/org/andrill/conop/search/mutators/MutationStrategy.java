package org.andrill.conop.search.mutators;

import org.andrill.conop.search.Solution;

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
