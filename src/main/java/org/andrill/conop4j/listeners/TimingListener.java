package org.andrill.conop4j.listeners;

import org.andrill.conop4j.Solution;

/**
 * A {@link Listener} the prints timing statistics.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class TimingListener implements Listener {
	private long count = 0;
	private final long sampleSize;
	private long start = 0;

	/**
	 * Create a new TimingListener.
	 * 
	 * @param sampleSize
	 *            the sample size.
	 */
	public TimingListener(final long sampleSize) {
		this.sampleSize = sampleSize;
	}

	@Override
	public void tried(final double temp, final Solution current, final Solution best) {
		if (start == 0) {
			start = System.currentTimeMillis();
		}
		count = (count + 1) % sampleSize;
		if (count == 0) {
			System.out.println(sampleSize + " iterations in " + (System.currentTimeMillis() - start) + "ms");
			start = System.currentTimeMillis();
		}
	}
}
