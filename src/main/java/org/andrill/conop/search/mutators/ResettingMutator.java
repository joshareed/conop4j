package org.andrill.conop.search.mutators;

import org.andrill.conop.search.Solution;
import org.andrill.conop.search.listeners.Listener;

/**
 * A mutation strategy that restarts from the best seen solution if no progress
 * is made in 10 steps.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class ResettingMutator implements MutationStrategy, Listener {
	/**
	 * The default reset counter.
	 */
	public static final long DEFAULT_RESET = 10;
	protected MutationStrategy base;
	protected long counter = 0;
	protected Solution local = null;
	protected long reset = 0;

	/**
	 * Create a new ResettingMutator that wraps a {@link ConstrainedMutator}.
	 */
	public ResettingMutator() {
		this(new ConstrainedMutator(), DEFAULT_RESET);
	}

	/**
	 * Create a new ResettingMutator that wraps the specified mutator.
	 * 
	 * @param base
	 *            the base mutator.
	 * @param reset
	 *            the reset counter.
	 */
	public ResettingMutator(final MutationStrategy base, final long reset) {
		this.base = base;
		this.reset = reset;
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
