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
	protected long startTime = 0;
	protected long currentIteration = 0;
	protected long lastProgressIteration = 0;
	protected long lastProgressTime = 0;

	@Override
	public void configure(final Properties properties) {
		super.configure(properties);
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
	}
}
