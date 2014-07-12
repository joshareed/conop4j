package org.andrill.conop.core.objectives;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.andrill.conop.core.AbstractConfigurable;
import org.andrill.conop.core.Configuration;
import org.andrill.conop.core.Run;
import org.andrill.conop.core.Solution;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;

public abstract class AbstractParallelPenalty extends AbstractConfigurable implements Penalty {
	protected ExecutorService pool;
	protected int procs = 1;
	protected final String name;
	protected boolean first = true;

	protected AbstractParallelPenalty(final String name) {
		this.name = name;
	}

	@Override
	public void configure(final Configuration config) {
		this.procs = config.get("processors", Runtime.getRuntime().availableProcessors());
		if (procs > 1) {
			pool = MoreExecutors.getExitingExecutorService((ThreadPoolExecutor) Executors.newFixedThreadPool(procs));
		}
	}

	protected Future<Double> execute(final Penalty task, final Solution solution) {
		if (pool == null) {
			return Futures.immediateFuture(task.score(solution));
		} else {
			return pool.submit(new Callable<Double>() {
				@Override
				public Double call() throws Exception {
					return task.score(solution);
				}
			});
		}
	}

	protected void initialize(final Run run) {
		// do nothing
	}

	protected abstract List<Future<Double>> internalScore(Solution solution);

	@Override
	public double score(final Solution solution) {
		if (first) {
			first = false;
			initialize(solution.getRun());
		}

		double penalty = 0;
		for (Future<Double> r : internalScore(solution)) {
			try {
				penalty += r.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		return penalty;
	}

	@Override
	public String toString() {
		return name;
	}
}
