package org.andrill.conop.core.mutators;

import org.andrill.conop.core.Solution;

/**
 * Mutates a solution into another potential {@link Solution}.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface Mutator {

	/**
	 * Mutates the specified solution into a new solution.
	 * 
	 * @param solution
	 *            the existing solution.
	 * @return the new solution.
	 */
	Solution mutate(Solution solution);
}
