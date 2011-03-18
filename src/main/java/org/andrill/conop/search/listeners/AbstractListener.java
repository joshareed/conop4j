package org.andrill.conop.search.listeners;

import java.util.Properties;

import org.andrill.conop.search.Solution;

/**
 * An abstract implementation of the {@link Listener} interface.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public abstract class AbstractListener implements Listener {

	public void configure(final Properties properties) {
		// do nothing
	}

	public Mode getMode() {
		return Mode.ANY;
	}

	public void started(final Solution initial) {
		// do nothing
	}

	public void stopped(final Solution solution) {
		// do nothing
	}

	public abstract void tried(double temp, Solution current, Solution best);
}
