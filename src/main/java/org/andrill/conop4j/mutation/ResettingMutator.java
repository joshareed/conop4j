package org.andrill.conop4j.mutation;

import org.andrill.conop4j.Solution;
import org.andrill.conop4j.listeners.Listener;

/**
 * A mutation strategy that restarts from the best seen solution if no progress
 * is made in 10 steps.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class ResettingMutator implements MutationStrategy, Listener {
	protected MutationStrategy base;
	protected long counter = 0;
	protected Solution local = null;

	/**
	 * Create a new ResettingMutator that wraps a {@link ConstrainedMutator}.
	 */
	public ResettingMutator() {
		this(new ConstrainedMutator());
	}

	/**
	 * Create a new ResettingMutator that wraps the specified mutator.
	 * 
	 * @param base
	 *            the base mutator.
	 */
	public ResettingMutator(final MutationStrategy base) {
		this.base = base;
	}

	@Override
	public Solution mutate(final Solution solution) {
		if (counter > 10) {
			counter = 0;
			return base.mutate(local);
		} else {
			return base.mutate(solution);
		}
	}

	@Override
	public void tried(final double temp, final Solution current, final Solution best) {
		if ((local == null) || (best.getScore() <= local.getScore())) {
			local = best;
			counter = 0;
		} else {
			counter++;
		}
	}
}
