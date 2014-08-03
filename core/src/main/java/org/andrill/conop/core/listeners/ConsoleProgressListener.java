package org.andrill.conop.core.listeners;

import java.text.DecimalFormat;
import java.util.concurrent.locks.ReentrantLock;

import org.andrill.conop.core.Solution;
import org.andrill.conop.core.util.TimerUtils;

/**
 * A {@link Listener} that writes progress to the console.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class ConsoleProgressListener extends AsyncListener {
	private static final String CLEAR = "                                                                                    \r";
	private static final String FORMAT = "CONOP4J: %s [ %d min | %s C | %d ] (%d/s)\r";
	private static final DecimalFormat DEC = new DecimalFormat("0.00");
	private double score = Double.MAX_VALUE;
	private ReentrantLock lock = new ReentrantLock();

	@Override
	protected void run(final double temp, final long iteration,
			final Solution current, final Solution best) {
		if (lock.tryLock()) {
			try {
				score = best.getScore();
				long elapsed = TimerUtils.getCounter();
				System.out.print(CLEAR);
				System.out.print(String.format(FORMAT,
						DEC.format(best.getScore()), (elapsed / 60),
						DEC.format(temp), iteration, (iteration / elapsed)));
			} finally {
				lock.unlock();
			}
		}
	}

	@Override
	protected boolean test(final double temp, final long iteration,
			final Solution current, final Solution best) {
		return (best.getScore() < score) || ((iteration % 10000) == 0);
	}

	@Override
	public void stopped(Solution solution) {
		System.out.print("\r\n");
	}
}
