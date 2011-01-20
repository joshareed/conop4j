package org.andrill.conop4j.objective;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.andrill.conop4j.Section;
import org.andrill.conop4j.Solution;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * An {@link ObjectiveFunction} implementation that places events using
 * cumulative penalties in a matrix.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class MatrixPlacement implements ObjectiveFunction, Parallel {
	protected Map<Section, SectionMatrix> matrices = Maps.newHashMap();
	protected ExecutorService pool;
	protected int procs = 1;

	protected Future<Double> execute(final SectionMatrix matrix, final Solution solution) {
		if (pool == null) {
			return Futures.immediateFuture(matrix.score(solution));
		} else {
			return pool.submit(new Callable<Double>() {
				@Override
				public Double call() throws Exception {
					return matrix.score(solution);
				}
			});
		}
	}

	@Override
	public double score(final Solution solution) {
		List<Future<Double>> results = Lists.newArrayList();
		for (Section section : solution.getRun().getSections()) {
			SectionMatrix matrix = matrices.get(section);
			if (matrix == null) {
				matrix = new SectionMatrix(section);
				matrices.put(section, matrix);
			}
			results.add(execute(matrix, solution));
		}

		double penalty = 0;
		for (Future<Double> r : results) {
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
	public void setProcessors(final int procs) {
		this.procs = procs;
		pool = MoreExecutors.getExitingExecutorService((ThreadPoolExecutor) Executors.newFixedThreadPool(procs));
	}

	@Override
	public String toString() {
		return "Matrix [" + procs + "]";
	}
}
