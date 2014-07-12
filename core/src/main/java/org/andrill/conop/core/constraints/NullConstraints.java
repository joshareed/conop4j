package org.andrill.conop.core.constraints;

import org.andrill.conop.core.AbstractConfigurable;
import org.andrill.conop.core.Solution;

/**
 * Performs no constraint checking.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class NullConstraints extends AbstractConfigurable implements Constraints {

	public boolean isValid(final Solution solution) {
		return true;
	}

	@Override
	public String toString() {
		return "Null";
	}
}
