package org.andrill.conop.search.listeners;

import org.andrill.conop.search.Configurable;
import org.andrill.conop.search.Simulation;
import org.andrill.conop.search.Solution;

/**
 * An abstract implementation of the {@link Listener} interface.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public abstract class AbstractListener implements Listener, Configurable {

	@Override
	public void configure(final Simulation simulation) {
		// do nothing
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
