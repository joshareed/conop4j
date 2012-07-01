package org.andrill.conop.search.schedules;

import java.util.Properties;

import org.andrill.conop.search.AbstractConfigurable;
import org.andrill.conop.search.Solution;

/**
 * An exponential cooling schedule.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class ExponentialSchedule extends AbstractConfigurable implements CoolingSchedule {
	protected long count;
	protected double current;
	protected double factor;
	protected double initial;
	protected long minStepsPer;
	protected double score = -1;

	/**
	 * Create a new ExponentialSchedule.
	 */
	public ExponentialSchedule() {
		initial = 1000;
		factor = 0.01;
		minStepsPer = 100;
		this.current = initial;
	}

	@Override
	public void configure(final Properties properties) {
		this.initial = Double.parseDouble(properties.getProperty("schedule.initial", "1000"));
		this.factor = Double.parseDouble(properties.getProperty("schedule.delta", "0.01"));
		this.minStepsPer = Long.parseLong(properties.getProperty("schedule.stepsPer", "100"));
		this.current = initial;
	}

	public double getInitial() {
		return initial;
	}

	public double next(final Solution solution) {
		count++;
		if (current < 0.01) {
			return 0;
		} else if (score == -1) {
			score = solution.getScore();
			return current;
		} else if (solution.getScore() <= score) {
			score = solution.getScore();
			count = 0;
			return current;
		} else if (count > minStepsPer) {
			current = current / (1 + (current * factor));
			count = 0;
			return current;
		} else {
			return current;
		}
	}
}
