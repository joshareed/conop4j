package org.andrill.conop.search.mutators;

import org.andrill.conop.search.AbstractConfigurable;
import org.andrill.conop.search.Solution;
import org.andrill.conop.search.listeners.Listener;

/**
 * A shared mutator for multiple CONOP processes on the same machine.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class SharedMutator extends AbstractConfigurable implements
		MutationStrategy, Listener {
	protected final double factor;
	protected final MutationStrategy mutator;
	protected Solution shared;

	/**
	 * Create a new SharedMutator that wraps the specified mutator.
	 * 
	 * @param mutator
	 *            the base mutator.
	 */
	public SharedMutator(final MutationStrategy mutator) {
		this(mutator, 0.95);
	}

	/**
	 * Create a new SharedMutator that wraps the specified mutator and
	 * acceptance factor.
	 * 
	 * @param mutator
	 *            the base mutator.
	 * @param factor
	 *            the acceptance factor.
	 */
	public SharedMutator(final MutationStrategy mutator, final double factor) {
		this.mutator = mutator;
		this.factor = factor;
	}

	@Override
	public Solution mutate(final Solution solution) {
		if ((shared != null)
				&& (shared.getScore() < factor * solution.getScore())) {
			return new Solution(shared.getRun(), shared.getEvents());
		} else {
			return mutator.mutate(solution);
		}
	}

	@Override
	public String toString() {
		return "Shared [" + mutator + "]";
	}

	@Override
	public void tried(final double temp, final Solution current,
			final Solution best) {
		if ((shared == null) || (best.getScore() < shared.getScore())) {
			shared = best;
		}
	}
}
