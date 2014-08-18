package org.andrill.conop.core.mutators;

import org.andrill.conop.core.Configuration;
import org.andrill.conop.core.Solution;
import org.andrill.conop.core.listeners.AbstractListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract mutator that adds reset support.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public abstract class AbstractMutator extends AbstractListener implements Mutator {
	private static final long DEFAULT_RESET = -1l;

	protected final Logger log = LoggerFactory.getLogger(getClass());
	protected long counter;
	protected long reset = -1;
	protected final String name;

	protected AbstractMutator(final String name) {
		this.name = name;
	}

	@Override
	public void configure(final Configuration config) {
		reset = config.get("reset", DEFAULT_RESET);
		if (reset > 0) {
			log.debug("Configuring reset to best solution as '{} iterations'", reset);
		}
	}

	protected abstract Solution internalMutate(final Solution solution);

	@Override
	public final Solution mutate(final Solution solution) {
		if ((reset > 0) && (counter > reset)) {
			counter = 0;
			return internalMutate(context.getBest());
		} else {
			return internalMutate(solution);
		}
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public void tried(final double temp, final Solution current, final Solution best) {
		counter++;
	}
}
