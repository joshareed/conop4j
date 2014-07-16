package org.andrill.conop.core.schedules;

import org.andrill.conop.core.AbstractConfigurable;
import org.andrill.conop.core.Configuration;
import org.andrill.conop.core.Solution;

/**
 * An exponential cooling schedule.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class ExponentialSchedule extends AbstractConfigurable implements Schedule {
	protected long count = 0;
	protected double current = 1000;
	protected double delta = 0.01;
	protected double initial = 1000;
	protected long steps = 100;
	protected double score = -1;

	@Override
	public void configure(final Configuration config) {
		this.initial = config.get("initial", 1000.0);
		this.delta = config.get("delta", 0.01);
		this.steps = config.get("steps", 100l);
		this.current = initial;
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
		} else if (count > steps) {
			current = current / (1 + (current * delta));
			count = 0;
			return current;
		} else {
			return current;
		}
	}
}
