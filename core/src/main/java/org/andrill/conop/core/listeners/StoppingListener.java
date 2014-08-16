package org.andrill.conop.core.listeners;

import org.andrill.conop.core.Configuration;
import org.andrill.conop.core.HaltedException;
import org.andrill.conop.core.Solution;
import org.andrill.conop.core.util.TimerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A listener responsible for stopping a dataset under various conditions.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class StoppingListener extends AbstractListener {
	protected static final Logger log = LoggerFactory.getLogger(StoppingListener.class);

	protected double bestScore = Double.MAX_VALUE;
	protected long currentIteration = -1;
	protected long lastProgressIteration = -1;
	protected long lastProgressTime = -1;

	protected long stopIteration = -1;
	protected long stopProgressIteration = -1;
	protected long stopProgressTime = -1;
	protected double stopThreshold = -1;
	protected long stopThresholdIteration = -1;
	protected long stopThresholdTime = -1;
	protected long stopTime = -1;

	protected void abort(final String message) {
		throw new HaltedException(message);
	}

	@Override
	public void configure(final Configuration config) {
		super.configure(config);

		// parse stopping conditions
		stopTime = config.get("time", -1l) * 60l;
		if (stopTime > 0) {
			log.debug("Configuring stop time as '{} minutes'", stopTime / 60);
		}

		stopIteration = config.get("steps", -1l);
		if (stopIteration > 0) {
			log.debug("Configuring stop iteration as '{}'", stopIteration);
		}

		stopProgressTime = config.get("progressTime", -1l) * 60l;
		if (stopProgressTime > 0) {
			log.debug("Configuring stop if no progress time as '{} minutes'", stopProgressTime / 60);
		}

		stopProgressIteration = config.get("progressSteps", -1l);
		if (stopProgressIteration > 0) {
			log.debug("Configuring stop if no progress iterations as '{}'", stopProgressIteration);
		}

		stopThreshold = config.get("threshold", -1.0);
		if (stopThreshold > 0) {
			log.debug("Configuring stop threshold as '{}'", stopThreshold);
		}

		stopThresholdTime = config.get("thresholdTime", -1l) * 60;
		if (stopThresholdTime > 0) {
			log.debug("Configuring stop threshold time as '{} minutes'", stopThresholdTime / 60);
		}

		stopThresholdIteration = config.get("thresholdSteps", -1l);
		if (stopThresholdIteration > 0) {
			log.debug("Configuring stop threshold iteration as '{}'", stopThresholdIteration);
		}
	}

	@Override
	public String toString() {
		return "Stopping Listener";
	}

	protected int minutes(final long time) {
		return (int) (time / 60);
	}

	@Override
	public void started(final Solution solution) {
		currentIteration = 0;
		lastProgressIteration = 0;
		lastProgressTime = 0;
	}

	protected void stop(final String message) {
		throw new HaltedException(message);
	}

	@Override
	public void tried(final double temp, final Solution current, final Solution best) {
		currentIteration++;
		long time = TimerUtils.getCounter();

		// check our score and update our last progress variables
		if (best.getScore() < bestScore) {
			bestScore = best.getScore();
			lastProgressIteration = currentIteration;
			lastProgressTime = time;
		}

		// check our stopping conditions
		if ((stopIteration > 0) && (currentIteration >= stopIteration)) {
			stop("Stopped because iteration " + stopIteration + " was reached");
		}
		if ((stopTime > 0) && (time >= stopTime)) {
			stop("Stopped because run time of " + minutes(stopTime) + " minutes was reached");
		}
		if ((stopProgressIteration > 0) && ((currentIteration - lastProgressIteration) >= stopProgressIteration)) {
			stop("Stopped because no progress was made in " + stopProgressIteration + " iterations");
		}
		if ((stopProgressTime > 0) && ((time - lastProgressTime) >= stopProgressTime)) {
			stop("Stopped because no progress was made in " + minutes(stopProgressTime) + " minutes");
		}
		if ((stopThreshold > 0) && (bestScore > stopThreshold)) {
			if ((stopThresholdIteration > 0) && (currentIteration >= stopThresholdIteration)) {
				stop("Stopped because simulation did not reach score threshold of " + stopThreshold + " in "
						+ stopThresholdIteration + " iterations");
			}
			if ((stopThresholdTime > 0) && (time >= stopThresholdTime)) {
				stop("Stopped because simulation did not reach score threshold of " + stopThreshold + " in "
						+ minutes(stopThresholdTime) + " minutes");
			}
		}
	}
}
