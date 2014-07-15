package org.andrill.conop.core.schedules;

import org.andrill.conop.core.AbstractConfigurable;
import org.andrill.conop.core.Configuration;
import org.andrill.conop.core.Solution;

public class TemperingSchedule extends AbstractConfigurable implements Schedule {
	protected long count;
	protected double current;
	protected double factor;
	protected double initial;
	protected long minStepsPer;
	protected double score = -1;
	protected double temperTo;
	protected double temperWhen;

	/**
	 * Create a new TemperingSchedule.
	 */
	public TemperingSchedule() {
		initial = 1000;
		factor = 0.01;
		minStepsPer = 100;
		this.current = initial;
	}

	@Override
	public void configure(final Configuration config) {
		this.initial = config.get("initial", 1000.0);
		this.factor = config.get("delta", 0.01);
		this.minStepsPer = config.get("steps", 100l);
		this.current = initial;
		this.temperTo = initial / 2;
		this.temperWhen = Math.log10(temperTo);
	}

	@Override
	public double getInitial() {
		return initial;
	}

	@Override
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
			if (current <= temperWhen) {
				current = temperTo;
				temperTo /= 2;
				temperWhen = Math.log10(temperTo);
			} else {
				current = current / (1 + (current * factor));
			}
			count = 0;
			return current;
		} else {
			return current;
		}
	}
}
