package org.andrill.conop.core.schedules;

import org.andrill.conop.core.AbstractConfigurable;
import org.andrill.conop.core.Configuration;
import org.andrill.conop.core.Solution;

/**
 * A linear cooling schedule.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class LinearSchedule extends AbstractConfigurable implements Schedule {
	protected long count = 0;
	protected double current = 0.0;
	protected double delta = 1;
	protected double initial = 0;
	protected long minStepsPer = 1;
	protected double score = -1;

	/**
	 * Create a new LinearSchedule.
	 */
	public LinearSchedule() {
		this.initial = 1000;
		this.delta = 0.01;
		minStepsPer = 100;
		this.current = initial;
	}

	@Override
	public void configure(final Configuration config) {
		this.initial = config.get("initial", 1000.0);
		this.delta = config.get("delta", 0.01);
		this.minStepsPer = config.get("steps", 100l);
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
		} else if (count > minStepsPer) {
			current = Math.max(current - delta, 0);
			count = 0;
			return current;
		} else {
			return current;
		}
	}
}
