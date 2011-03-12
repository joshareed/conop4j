package org.andrill.conop.search.mutators;

import java.util.Properties;

import org.andrill.conop.search.Solution;
import org.andrill.conop.search.listeners.Listener;

/**
 * An abstract mutator that adds reset support.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public abstract class AbstractMutator implements MutationStrategy, Listener {
	protected long counter;
	protected Solution local;
	protected long reset = -1;
	protected double shared = -1;

	public void configure(final Properties properties) {
		reset = Long.parseLong(properties.getProperty("mutator.reset", "-1"));
	}

	protected abstract Solution internalMutate(final Solution solution);

	public final Solution mutate(final Solution solution) {
		if ((reset > 0) && (counter > reset)) {
			counter = 0;
			return internalMutate(local);
		} else {
			return internalMutate(solution);
		}
	}

	public void tried(final double temp, final Solution current, final Solution best) {
		if ((local == null) || (best.getScore() < local.getScore())) {
			local = best;
			counter = 0;
		} else {
			counter++;
		}
	}
}
