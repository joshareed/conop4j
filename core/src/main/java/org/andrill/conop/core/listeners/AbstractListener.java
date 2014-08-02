package org.andrill.conop.core.listeners;

import org.andrill.conop.core.Configurable;
import org.andrill.conop.core.Configuration;
import org.andrill.conop.core.Solution;
import org.andrill.conop.core.solver.SolverContext;

/**
 * An abstract implementation of the {@link Listener} interface.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public abstract class AbstractListener implements Listener, Configurable {
	protected SolverContext context = null;

	@Override
	public void configure(final Configuration config) {
		// do nothing
	}

	@Override
	public void setContext(SolverContext context) {
		this.context = context;
	}

	@Override
	public Mode getMode() {
		return Mode.ANY;
	}

	@Override
	public void started(final Solution initial) {
		// do nothing
	}

	@Override
	public void stopped(final Solution solution) {
		// do nothing
	}

	@Override
	public abstract void tried(double temp, Solution current, Solution best);
}
