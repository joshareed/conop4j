package org.andrill.conop.search.listeners;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.andrill.conop.search.Solution;

import com.google.common.util.concurrent.MoreExecutors;

/**
 * An abstract base class for listeners that can run asynchronously.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public abstract class AsyncListener implements Listener {
	protected static ExecutorService pool = MoreExecutors.getExitingExecutorService((ThreadPoolExecutor) Executors
			.newFixedThreadPool(4));
	protected long iteration = 0;
	protected long start = 0;

	@Override
	public void configure(final Properties properties) {
		// do nothing
	}

	@Override
	public Mode getMode() {
		return Mode.ANY;
	}

	/**
	 * Run the listener method.
	 * 
	 * @param temp
	 *            the current temperature.
	 * @param iteration
	 *            the current iteration number.
	 * @param current
	 *            the current score.
	 * @param best
	 *            the best score.
	 */
	protected abstract void run(final double temp, long iteration, final Solution current, final Solution best);

	@Override
	public void started(final Solution initial) {
		// do nothing
	}

	@Override
	public void stopped(final Solution solution) {
		// do nothing
	}

	/**
	 * Tests whether this listener should be run.
	 * 
	 * @param temp
	 *            the temperature.
	 * @param iteration
	 *            the current iteration number.
	 * @param current
	 *            the current score.
	 * @param best
	 *            the best score.
	 * @return
	 */
	protected abstract boolean test(final double temp, long iteration, final Solution current, final Solution best);

	@Override
	public final void tried(final double temp, final Solution current, final Solution best) {
		iteration++;
		if (iteration == 1) {
			start = System.currentTimeMillis();
		}
		if (test(temp, iteration, current, best)) {
			pool.submit(new Runnable() {
				@Override
				public void run() {
					AsyncListener.this.run(temp, iteration, current, best);
				}
			});
		}
	}
}
