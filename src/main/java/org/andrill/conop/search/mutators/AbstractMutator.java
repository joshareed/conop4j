package org.andrill.conop.search.mutators;

import java.util.Properties;

import org.andrill.conop.search.Solution;
import org.andrill.conop.search.listeners.AbstractListener;

/**
 * An abstract mutator that adds reset support.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public abstract class AbstractMutator extends AbstractListener implements MutationStrategy {
	protected long counter;
	protected Solution local;
	protected long reset = -1;
	protected double temp = -1;

	@Override
	public void configure(final Properties properties) {
		reset = Long.parseLong(properties.getProperty("mutator.reset", "-1"));
	}

	protected abstract Solution internalMutate(final Solution solution);

	@Override
	public final Solution mutate(final Solution solution) {
		if ((reset > 0) && (counter > reset)) {
			counter = 0;
			return internalMutate(local);
		} else {
			return internalMutate(solution);
		}
	}

	@Override
	public void tried(final double temp, final Solution current, final Solution best) {
		this.temp = temp;
		if ((local == null) || (best.getScore() < local.getScore())) {
			local = best;
			counter = 0;
		} else {
			counter++;
		}
	}
}
