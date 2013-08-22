package org.andrill.conop.search.objectives;

import java.util.Properties;
import java.util.concurrent.*;

import org.andrill.conop.search.AbstractConfigurable;
import org.andrill.conop.search.Solution;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;

public abstract class AbstractParallelObjective extends AbstractConfigurable implements ObjectiveFunction {
	protected ExecutorService pool;
	protected int procs = 1;

	@Override
	public void configure(final Properties properties) {
		this.procs = Integer.parseInt(properties.getProperty("processors", ""
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

}
