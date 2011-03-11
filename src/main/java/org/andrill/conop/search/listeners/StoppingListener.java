package org.andrill.conop.search.listeners;

import java.util.Properties;

import org.andrill.conop.search.AbstractConfigurable;
import org.andrill.conop.search.Solution;

/**
 * A listener responsible for stopping a run under various conditions.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class StoppingListener extends AbstractConfigurable implements Listener {
	protected double bestScore = Double.MAX_VALUE;
	protected long currentIteration = 0;
	protected long lastProgressIteration = 0;
	protected long lastProgressTime = 0;
	protected long startTime = 0;

	protected long stopIteration = -1;
	protected long stopProgressIteration = -1;
	protected double stopProgressTime = -1;
	protected double stopThreshold = -1;
	protected long stopThresholdIteration = -1;
	protected double stopThresholdTime = -1;
	protected double stopTime = -1;

	@Override
	public void configure(final Properties properties) {
		super.configure(properties);

		// parse stopping conditions
		if (properties.containsKey("stop.time")) {
			stopTime = Double.parseDouble(properties.getProperty("stop.time", "-1")) * 1000 * 60;
		}
		if (properties.containsKey("stop.steps")) {
			stopIteration = Long.parseLong(properties.getProperty("stop.steps", "-1"));
		}
		if (properties.containsKey("stop.progress.time")) {
			stopProgressTime = Double.parseDouble(properties.getProperty("stop.progress.time", "-1")) * 1000 * 60;
		}
		if (properties.containsKey("stop.progress.steps")) {
			stopProgressIteration = Long.parseLong(properties.getProperty("stop.progress.steps", "-1"));
		}
		if (properties.containsKey("stop.threshold")) {
			stopThreshold = Double.parseDouble(properties.getProperty("stop.threshold", "-1"));
		}
		if (properties.containsKey("stop.threshold.time")) {
			stopThresholdTime = Double.parseDouble(properties.getProperty("stop.threshold.time", "-1")) * 1000 * 60;
		}
		if (properties.containsKey("stop.threshold.steps")) {
			stopThresholdIteration = Long.parseLong(properties.getProperty("stop.threshold.steps", "-1"));
		}
	}

	@Override
	public void tried(final double temp, final Solution current, final Solution best) {
		currentIteration++;
		long time = System.currentTimeMillis();

		// save our start time
		if (startTime == 0) {
			startTime = time;
		}

		// check our score and update our last progress variables
		if (best.getScore() < bestScore) {
			bestScore = best.getScore();
			lastProgressIteration = currentIteration;
			lastProgressTime = time;
		}

		// check our stopping conditions
		if ((stopIteration > 0) && (currentIteration >= stopIteration)) {
			throw new RuntimeException("Stopped because iteration " + stopIteration + " was reached");
		}
		if ((stopTime > 0) && ((time - startTime) >= stopTime)) {
			throw new RuntimeException("Stopped because run time of " + (stopTime / 60000) + " minute(s) was reached");
		}
		if ((stopProgressIteration > 0) && ((currentIteration - lastProgressIteration) >= stopProgressIteration)) {
			throw new RuntimeException("Stopped because no progress was made in " + stopProgressIteration
					+ " iterations");
		}
		if ((stopProgressTime > 0) && ((time - lastProgressTime) >= stopProgressTime)) {
			throw new RuntimeException("Stopped because no progress was made in " + (stopProgressTime / 60000)
					+ " minute(s)");
		}
		if ((stopThreshold > 0) && (best.getScore() > stopThreshold)) {
			if ((stopThresholdIteration > 0) && (currentIteration >= stopThresholdIteration)) {
				throw new RuntimeException("Stopped because simulation did not reach score threshold of "
						+ stopThreshold + " in " + stopThresholdIteration + " iterations");
			}
			if ((stopThresholdTime > 0) && ((time - startTime) >= stopThresholdTime)) {
				throw new RuntimeException("Stopped because simulation did not reach score threshold of "
						+ stopThreshold + " in " + (stopThresholdTime / 60000) + " minute(s)");
			}
		}
	}
}
