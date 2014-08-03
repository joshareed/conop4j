package org.andrill.conop.core;

import org.andrill.conop.core.solver.SolverContext;

/**
 * An abstract implementation of the {@link Configurable} interface.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public abstract class AbstractConfigurable implements Configurable {
	protected SolverContext context = null;

	@Override
	public void configure(final Configuration config) {
		// override
	}

	@Override
	public void setContext(SolverContext context) {
		this.context = context;
	}
}
