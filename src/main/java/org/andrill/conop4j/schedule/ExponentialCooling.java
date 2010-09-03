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
	}

	@Override
	public double getInitial() {
		return initial;
	}

	@Override
	public double next(final Solution solution) {
		current = current / (1 + current * factor);
		if (current < 0.01) {
			current = 0;
		}
		return current;
	}
}
