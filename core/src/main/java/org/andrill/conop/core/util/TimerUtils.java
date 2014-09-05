package org.andrill.conop.core.util;

import java.util.concurrent.locks.ReentrantLock;

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
	private static ReentrantLock lock = new ReentrantLock();

	/**
	 * Gets a counter to avoid having to call System.currentTimeMillis().
	 *
	 * @return the counter.
	 */
	public static long getCounter() {
		if (!thread.isAlive()) {
			try {
				lock.lock();
				if (!thread.isAlive()) {
					thread.start();
				}
			} finally {
				lock.unlock();
			}
		}
		return thread.counter;
	}

	public static void reset() {
		thread.counter = 0;
	}

	private TimerUtils() {
		// not to be instantiated
	}
}
