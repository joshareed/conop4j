package org.andrill.conop4j.constraints;

import org.andrill.conop4j.Solution;

/**
 * Performs no constraint checking.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class NullChecker implements ConstraintChecker {

	@Override
	public boolean isValid(final Solution solution) {
		return true;
	}

	@Override
	public String toString() {
		return "Null";
	}
}
