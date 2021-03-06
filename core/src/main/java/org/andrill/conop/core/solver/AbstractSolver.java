package org.andrill.conop.core.solver;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.RejectedExecutionException;

import org.andrill.conop.core.Configurable;
import org.andrill.conop.core.Dataset;
import org.andrill.conop.core.HaltedException;
import org.andrill.conop.core.Solution;
import org.andrill.conop.core.internal.DefaultSolverContext;
import org.andrill.conop.core.listeners.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSolver implements Solver {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	protected Set<Listener> listeners = new CopyOnWriteArraySet<Listener>();
	protected boolean started = false;
	protected boolean stopped = false;
	protected SolverContext context = new DefaultSolverContext();

	/**
	 * Add a new listener.
	 *
	 * @param l
	 *            the listener.
	 */
	public void addListener(final Listener l) {
		listeners.add(l);
		setContext(l);
	}

	protected Solution getBest() {
		return context.getBest();
	}

	private void addShutdownHook() {
		// add our shutdown hook so we can make an effort to call stopped()
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				stopped(getBest());
			}
		});
	}

	protected void handleError(final Exception e) throws HaltedException {
		Solution best = getBest();

		HaltedException halt;
		if (e instanceof HaltedException) {
			halt = new HaltedException(e.getMessage(), best);
		} else if ((e instanceof InterruptedException) || (e instanceof RejectedExecutionException)) {
			halt = new HaltedException("User Interrupt", best);
		} else {
			log.error("Unexpected error", e);

			halt = new HaltedException("Unexpected Error: " + e.getMessage(), best);
		}
		stopped(best);
		throw halt;
	}

	protected void initialize(final SolverConfiguration config) {
		// override
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

	protected abstract void solve(Solution initial);

	@Override
	public SolverContext solve(final SolverConfiguration config, final Dataset dataset) throws HaltedException {
		addShutdownHook();
		initialize(config);

		context.setDataset(dataset);

		Solution initial = config.getInitialSolution();
		if (initial == null) {
			log.info("Using random initial solution");
			initial = Solution.initial(dataset);
		}

		solve(initial);

		return context;
	}

	protected void started(final Solution initial) {
		if (!started) {
			started = true;
			for (Listener l : listeners) {
				l.started(initial);
			}
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

	protected boolean updateBest(Solution next) {
		Solution best = context.getBest();

		// save as best if the penalty is less
		if (best == null || next.getScore() < best.getScore()) {

			// publish the best solution in the context
			context.setBest(next);

			if (next.getScore() == 0) {
				throw new HaltedException("Score reached 0", best);
			}
			return true;
		}
		return false;
	}

	public SolverContext getContext() {
		return context;
	}

	protected void setContext(Object... objs) {
		for (Object o : objs) {
			if (o instanceof Configurable) {
				((Configurable) o).setContext(context);
			}
		}
	}
}
