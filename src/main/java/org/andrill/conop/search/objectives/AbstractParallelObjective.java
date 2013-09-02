package org.andrill.conop.search.objectives;

import java.util.List;
import java.util.concurrent.*;

import org.andrill.conop.search.AbstractConfigurable;
import org.andrill.conop.search.Simulation;
import org.andrill.conop.search.Solution;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;

public abstract class AbstractParallelObjective extends AbstractConfigurable implements ObjectiveFunction {
	protected ExecutorService pool;
	protected int procs = 1;
	protected final String name;

	protected AbstractParallelObjective(final String name) {
		this.name = name;
	}

	@Override
	public void configure(final Simulation simulation) {
		this.procs = Integer.parseInt(simulation.getProperty("processors", ""
				+ Runtime.getRuntime().availableProcessors()));
		if (procs > 1) {
			pool = MoreExecutors.getExitingExecutorService((ThreadPoolExecutor) Executors.newFixedThreadPool(procs));
		}
	}

	protected Future<Double> execute(final ObjectiveFunction task, final Solution solution) {
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

	protected abstract List<Future<Double>> internalScore(Solution solution);

	@Override
	public double score(final Solution solution) {
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
