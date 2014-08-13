package org.andrill.conop.core.listeners;

import java.text.DecimalFormat;

import org.andrill.conop.core.Configuration;
import org.andrill.conop.core.Solution;
import org.andrill.conop.core.util.TimerUtils;

/**
 * A {@link Listener} that writes progress to the console.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class ConsoleProgressListener extends PeriodicListener {
	private static final String CLEAR = "                                                                                    \r";
	private static final String FORMAT = "CONOP4J: %s [ %d min | %s C | %d ] (%d/s)\r";
	private static final DecimalFormat DEC = new DecimalFormat("0.00");
	private double score = Double.MAX_VALUE;

	@Override
	public void configure(Configuration config) {
		frequency = config.get("frequency", 15);
		log.debug("Configuring frequency as '{} seconds'", frequency);
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
		boolean time = super.test(temp, iteration, current, best);
		return time || (best.getScore() < score);
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
