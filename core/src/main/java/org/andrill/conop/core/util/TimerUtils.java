package org.andrill.conop.core.util;

public class TimerUtils {

	private static class CounterThread extends Thread {
		long counter = 0;

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				counter++;
			}
		}
	}

	private static CounterThread thread = new CounterThread();

	/**
	 * Gets a counter to avoid having to call System.currentTimeMillis().
	 *
	 * @return the counter.
	 */
	public static long getCounter() {
		if (!thread.isAlive()) {
			thread.start();
		}
		return thread.counter;
	}

	private TimerUtils() {
		// not to be instantiated
	}
}
