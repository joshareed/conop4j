package org.andrill.conop.core.listeners;

import java.text.DecimalFormat;

import org.andrill.conop.core.Solution;

/**
 * A {@link Listener} that writes progress to the console.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class ConsoleProgressListener extends AsyncListener {
	private static final String CLEAR = "                                                                                    \r";
	private static final String FORMAT = "CONOP4J: %s / %s [ %d min | %s C | %d ] (%d/s)\r";
	private static final DecimalFormat DEC = new DecimalFormat("0.00");
	private double score = Double.MAX_VALUE;

	@Override
	protected void run(final double temp, final long iteration,
			final Solution current, final Solution best) {
		score = best.getScore();
		long elapsed = (System.currentTimeMillis() - start);
		System.out.print(CLEAR);
		System.out.print(String.format(FORMAT, DEC.format(score),
				DEC.format(best.getScore()), (elapsed / 60000),
				DEC.format(temp), iteration, (iteration / (elapsed / 1000))));
	}

	@Override
	protected boolean test(final double temp, final long iteration,
			final Solution current, final Solution best) {
		return (best.getScore() < score) || ((iteration % 10000) == 0);
	}
}
