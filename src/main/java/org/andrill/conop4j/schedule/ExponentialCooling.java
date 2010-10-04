package org.andrill.conop4j.schedule;

import org.andrill.conop4j.Solution;

/**
 * An exponential cooling schedule.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class ExponentialCooling implements CoolingSchedule {
	protected double current = 0;
	protected double factor = 0.001;
	protected double initial = 0;
	protected long stepsLeft = 1;
	protected long stepsPer = 1;

	/**
	 * Create a new exponential cooling schedule.
	 * 
	 * @param initial
	 *            the initial temperature.
	 * @param factor
	 *            the cooling factor (< 0.01)
	 */
	public ExponentialCooling(final double initial, final double factor) {
		this.initial = initial;
		this.factor = factor;
		current = initial;
		stepsPer = 1;
		stepsLeft = stepsPer;
	}

	/**
	 * Create a new exponential cooling schedule.
	 * 
	 * @param initial
	 *            the initial temperature.
	 * @param factor
	 *            the cooling factor (< 0.01)
	 * @param stepsPer
	 *            the steps per temperature.
	 */
	public ExponentialCooling(final double initial, final double factor, final long stepsPer) {
		this.initial = initial;
		this.factor = factor;
		this.stepsPer = stepsPer;
		current = initial;
		stepsLeft = stepsPer;
	}

	@Override
	public double getInitial() {
		return initial;
	}

	@Override
	public double next(final Solution solution) {
		stepsLeft--;
		if (stepsLeft == 0) {
			current = current / (1 + current * factor);
			if (current < 0.01) {
				current = 0;
			}
			stepsLeft = stepsPer;
		}
		return current;
	}
}
