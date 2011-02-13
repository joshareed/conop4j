package org.andrill.conop.search.constraints;

import org.andrill.conop.search.AbstractConfigurable;
import org.andrill.conop.search.Solution;

/**
 * Performs no constraint checking.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class NullChecker extends AbstractConfigurable implements
		ConstraintChecker {

	@Override
	public boolean isValid(final Solution solution) {
		return true;
	}

	@Override
	public String toString() {
		return "Null";
	}
}
