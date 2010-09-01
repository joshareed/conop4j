package org.andrill.conop4j.schedule;

import org.andrill.conop4j.Solution;

/**
 * A linear cooling schedule.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class LinearCooling implements CoolingSchedule {
	protected double current = 0.0;
	protected double delta = 1;
	protected double initial = 0;
	protected long stepsLeft = 1;
	protected long stepsPer = 1;

	/**
	 * Create a new linear cooling schedule.
	 * 
	 * @param initial
	 *            the initial temperature.
	 * @param steps
	 *            the number of temperature steps to 0.
	 */
	public LinearCooling(final double initial, final long steps) {
		this.initial = initial;
		stepsPer = 1;
		delta = initial / steps;
		stepsLeft = stepsPer;
		current = initial;
	}

	/**
	 * Create a new linear cooling schedule.
	 * 
	 * @param initial
	 *            the initial temperature.
	 * @param stepsPer
	 *            the steps per temperature.
	 * @param delta
	 *            the temperature delta.
	 */
	public LinearCooling(final double initial, final long stepsPer, final double delta) {
		this.initial = initial;
		this.stepsPer = stepsPer;
		this.delta = delta;
		stepsLeft = stepsPer;
		current = initial;
	}

	@Override
	public double getInitial() {
		return initial;
	}

	@Override
	public double next(final Solution solution) {
		stepsLeft--;
		if (stepsLeft == 0) {
			stepsLeft = stepsPer;
			current = Math.max(current - delta, 0);
		}
		return current;
	}
}
