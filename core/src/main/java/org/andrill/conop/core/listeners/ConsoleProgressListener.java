package org.andrill.conop.core.listeners;

import java.text.DecimalFormat;

import org.andrill.conop.core.Solution;
import org.andrill.conop.core.util.TimerUtils;

/**
 * A {@link Listener} that writes progress to the console.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class ConsoleProgressListener extends PeriodicListener {
	private static final String CLEAR = "                                                                                          \r";
	private static final String FORMAT = "Best: %s | Elapsed: %d min | Temperature: %s C | Tried: %d | Scored: %d/s                    \r";
	private static final DecimalFormat DEC = new DecimalFormat("0.00");
	private double score = Double.MAX_VALUE;

	@Override
	protected int getDefaultFrequency() {
		return 15;
	}

	@Override
	protected void fired(final double temp, final long iteration, final Solution current, final Solution best) {
		score = best.getScore();
		long elapsed = TimerUtils.getCounter();
		System.out.print(CLEAR);
		System.out.print(String.format(FORMAT, DEC.format(best.getScore()), (elapsed / 60), DEC.format(temp),
				iteration, (iteration / elapsed)));
	}

	@Override
	protected boolean test(final double temp, final long iteration, final Solution current, final Solution best) {
		return super.test(temp, iteration, current, best) || (best.getScore() < score);
	}

	@Override
	public void stopped(Solution solution) {
		System.out.println("\r");
	}

	@Override
	public String toString() {
		return "Console Progress Listener";
	}
}
