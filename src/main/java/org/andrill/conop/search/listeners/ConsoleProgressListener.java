package org.andrill.conop.search.listeners;

import java.text.DecimalFormat;

import org.andrill.conop.search.AbstractConfigurable;
import org.andrill.conop.search.Solution;

/**
 * A {@link Listener} that writes progress to the console.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class ConsoleProgressListener extends AbstractConfigurable implements Listener {
	private static final DecimalFormat DEC = new DecimalFormat("0.00");
	private long iter = 0;
	private double score = Double.MAX_VALUE;
	private long start = -1;

	@Override
	public void tried(final double temp, final Solution current, final Solution best) {
		if (start == -1) {
			start = System.currentTimeMillis();
		}
		iter++;
		if ((current.getScore() < score) || (iter % 10000 == 0)) {
			score = current.getScore();
			long elapsed = (System.currentTimeMillis() - start);
			System.out.print("                                                                            \r");
			System.out.print("CONOP4J: " + DEC.format(score) + " [ " + (elapsed / 60000) + "min | " + DEC.format(temp)
					+ "C | " + iter + " ]\r");
		}
	}
}
