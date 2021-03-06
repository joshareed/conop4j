package org.andrill.conop.core.listeners;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.andrill.conop.core.AbstractConfigurable;
import org.andrill.conop.core.Solution;

import com.google.common.util.concurrent.MoreExecutors;

/**
 * An abstract base class for listeners that can dataset asynchronously.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public abstract class AsyncListener extends AbstractConfigurable implements Listener {
	protected static ExecutorService pool = MoreExecutors.getExitingExecutorService((ThreadPoolExecutor) Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));

	protected long iteration = 0;

	/**
	 * Dataset the listener method.
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
	 * Tests whether this listener should be dataset.
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
