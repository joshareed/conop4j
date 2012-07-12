package org.andrill.conop.search.util;

public class TimerUtils {

	private static class CounterThread extends Thread {
		int counter = 0;

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

	private static CounterThread thread = null;

	/**
	 * Gets a counter to avoid having to call System.currentTimeMillis().
	 * 
	 * @return the counter.
	 */
	public static int getCounter() {
		if (thread == null) {
			thread = new CounterThread();
			thread.start();
		}
		return thread.counter;
	}

	private TimerUtils() {
		// not to be instantiated
	}
}
