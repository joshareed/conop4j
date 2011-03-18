package org.andrill.conop.search.listeners;

import java.text.DecimalFormat;

import org.andrill.conop.search.Solution;

/**
 * A {@link Listener} that writes progress to the console.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class ConsoleProgressListener extends AsyncListener {
	private static final DecimalFormat DEC = new DecimalFormat("0.00");
	private double score = Double.MAX_VALUE;

	@Override
	public Mode getMode() {
		return Mode.TUI;
	}

	@Override
	protected void run(final double temp, final long iteration, final Solution current, final Solution best) {
		score = current.getScore();
		long elapsed = (System.currentTimeMillis() - start);
		System.out.print("                                                                            \r");
		System.out.print("CONOP4J: " + DEC.format(score) + " [ " + (elapsed / 60000) + "min | " + DEC.format(temp)
				+ "C | " + iteration + " ]\r");
	}

	@Override
	protected boolean test(final double temp, final long iteration, final Solution current, final Solution best) {
		return (current.getScore() < score) || (iteration % 10000 == 0);
	}
}
