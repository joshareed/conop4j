package org.andrill.conop.core.listeners;

import java.util.concurrent.locks.ReentrantLock;

import org.andrill.conop.core.Configuration;
import org.andrill.conop.core.Solution;
import org.andrill.conop.core.util.TimerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PeriodicListener extends AsyncListener {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	protected int frequency = getDefaultFrequency();
	protected ReentrantLock lock = new ReentrantLock();
	protected volatile long next = 0;

	protected int getDefaultFrequency() {
		return 60;
	}

	@Override
	public void configure(Configuration config) {
		super.configure(config);

		frequency = config.get("frequency", getDefaultFrequency());
		log.debug("Configuring frequency as '{} seconds'", frequency);
	}

	@Override
	protected void run(double temp, long iteration, Solution current, Solution best) {
		if (lock.tryLock()) {
			try {
				fired(temp, iteration, current, best);
				next = TimerUtils.getCounter() + frequency;
			} finally {
				lock.unlock();
			}
		}
	}

	protected abstract void fired(final double temp, final long iteration, final Solution current, final Solution best);

	@Override
	protected boolean test(double temp, long iteration, Solution current, Solution best) {
		return (TimerUtils.getCounter() > next);
	}
}
