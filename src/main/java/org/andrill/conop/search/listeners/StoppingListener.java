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
	protected long stopNoProgressIteration = -1;
	protected long stopNoProgressTime = -1;
	protected long stopTime = -1;

	@Override
	public void configure(final Properties properties) {
		super.configure(properties);

		// parse stopping conditions
		if (properties.containsKey("stop.time")) {
			stopTime = Long.parseLong(properties.getProperty("stop.time", "-1")) * 1000 * 60;
		}
		if (properties.containsKey("stop.steps")) {
			stopIteration = Long.parseLong(properties.getProperty("stop.steps", "-1"));
		}
		if (properties.containsKey("stop.noProgress.time")) {
			stopNoProgressTime = Long.parseLong(properties.getProperty("stop.noProgress.time", "-1")) * 1000 * 60;
		}
		if (properties.containsKey("stop.noProgress.steps")) {
			stopNoProgressIteration = Long.parseLong(properties.getProperty("stop.noProgress.steps", "-1"));
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
		if ((stopIteration > 0) && (stopIteration <= currentIteration)) {
			throw new RuntimeException("Stopped because iteration " + stopIteration + " was reached");
		}
		if ((stopTime > 0) && (stopTime <= (time - startTime))) {
			throw new RuntimeException("Stopped because run time of " + (stopTime / 60000) + " minute(s) was reached");
		}
		if ((stopNoProgressIteration > 0) && (stopNoProgressIteration <= (currentIteration - lastProgressIteration))) {
			throw new RuntimeException("Stopped because no progress was made in " + stopNoProgressIteration
					+ " iterations");
		}
		if ((stopNoProgressTime > 0) && (stopNoProgressTime <= (time - lastProgressTime))) {
			throw new RuntimeException("Stopped because no progress was made in " + (stopNoProgressTime / 60000)
					+ " minute(s)");
		}
	}
}
