package org.andrill.conop.core.schedules;

import org.andrill.conop.core.AbstractConfigurable;
import org.andrill.conop.core.Configuration;
import org.andrill.conop.core.Solution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A linear cooling schedule.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class LinearSchedule extends AbstractConfigurable implements Schedule {
	private static final long DEFAULT_STEPS = 100l;
	private static final double DEFAULT_DELTA = 0.01;
	private static final double DEFAULT_INITIAL = 1000.0;
	private static final Logger log = LoggerFactory.getLogger(LinearSchedule.class);

	protected long count = 0;
	protected double current = 1000;
	protected double delta = 0.01;
	protected double initial = 1000;
	protected long steps = 100;
	protected double score = -1;

	@Override
	public void configure(final Configuration config) {
		this.initial = config.get("initial", DEFAULT_INITIAL);
		log.debug("Configuring initial temperature as '{}C'", initial);

		this.delta = config.get("delta", DEFAULT_DELTA);
		log.debug("Configuring temperature delta as '{}C'", delta);

		this.steps = config.get("steps", DEFAULT_STEPS);
		log.debug("Configuring minimum steps per temperature as '{}'", steps);

		current = initial;
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
		} else if (solution.getScore() < score) {
			score = solution.getScore();
			count = 0;
			return current;
		} else if (count > steps) {
			current = Math.max(current - delta, 0);
			count = 0;
			return current;
		} else {
			return current;
		}
	}

	@Override
	public String toString() {
		return "Linear Schedule";
	}
}
