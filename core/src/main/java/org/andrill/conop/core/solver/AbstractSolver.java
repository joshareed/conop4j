package org.andrill.conop.core.solver;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.andrill.conop.core.HaltedException;
import org.andrill.conop.core.Run;
import org.andrill.conop.core.Solution;
import org.andrill.conop.core.listeners.Listener;

public abstract class AbstractSolver implements Solver {
	protected Set<Listener> listeners = new CopyOnWriteArraySet<Listener>();
	protected Solution best = null;
	protected boolean stopped = false;

	/**
	 * Add a new listener.
	 *
	 * @param l
	 *            the listener.
	 */
	public void addListener(final Listener l) {
		listeners.add(l);
	}

	private void addShutdownHook() {
		// add our shutdown hook so we can make an effort to run stopped
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				stopped(best);
			}
		});
	}

	protected void initialize(final SolverConfiguration config) {
	}

	/**
	 * Remove a listener.
	 *
	 * @param l
	 *            the listener.
	 */
	public void removeListener(final Listener l) {
		listeners.remove(l);
	}

	protected abstract Solution solve(Solution initial);

	@Override
	public Solution solve(final SolverConfiguration config, final Run run) throws HaltedException {
		addShutdownHook();
		initialize(config);

		Solution initial = config.getInitialSolution();
		if (initial == null) {
			initial = Solution.initial(run);
		}
		return solve(initial);
	}

	protected void started(final Solution initial) {
		for (Listener l : listeners) {
			l.started(initial);
		}
	}

	protected void stopped(final Solution solution) {
		if (!stopped) {
			stopped = true;
			for (Listener l : listeners) {
				l.stopped(solution);
			}
		}
	}

}
