package org.andrill.conop4j.schedule;

import org.andrill.conop4j.Solution;

/**
 * An exponential cooling schedule.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class ExponentialSchedule implements CoolingSchedule {
	protected long count;
	protected double current;
	protected final double factor;
	protected final double initial;
	protected final long minStepsPer;
	protected final long noProgress;
	protected double score = -1;
	protected long stop = 0;

	/**
	 * Create a new AdaptiveCooling.
	 * 
	 * @param initial
	 *            the initial temp.
	 * @param factor
	 *            the factor.
	 * @param minStepsPer
	 *            the minimum number of steps before changing.
	 */
	public ExponentialSchedule(final double initial, final double factor, final long minStepsPer) {
		this(initial, factor, minStepsPer, Long.MAX_VALUE);
	}

	/**
	 * Create a new AdaptiveCooling.
	 * 
	 * @param initial
	 *            the initial temp.
	 * @param factor
	 *            the factor.
	 * @param minStepsPer
	 *            the minimum number of steps before changing.
	 * @param noProgress
	 *            the maximum number of steps at the same score before stopping
	 *            due to no progress.
	 */
	public ExponentialSchedule(final double initial, final double factor, final long minStepsPer, final long noProgress) {
		this.initial = initial;
		this.factor = factor;
		this.minStepsPer = minStepsPer;
		this.noProgress = noProgress;
		current = initial;
		count = 0;
	}

	@Override
	public double getInitial() {
		return initial;
	}

	@Override
	public double next(final Solution solution) {
		count++;
		stop++;
		if (current < 0.01) {
			return 0;
		} else if (stop > noProgress) {
			throw new RuntimeException("Stopped due to no progress in " + noProgress + " iterations");
		} else if (score == -1) {
			score = solution.getScore();
			return current;
		} else if (solution.getScore() < score) {
			score = solution.getScore();
			count = 0;
			stop = 0;
			return current;
		} else if (count > minStepsPer) {
			current = current / (1 + current * factor);
			count = 0;
			return current;
		} else {
			return current;
		}
	}
}
