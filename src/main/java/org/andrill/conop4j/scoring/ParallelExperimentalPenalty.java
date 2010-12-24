package org.andrill.conop4j.scoring;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.andrill.conop4j.Event;
import org.andrill.conop4j.Section;
import org.andrill.conop4j.Solution;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * A parallel version of {@link ExperimentalPenalty}.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class ParallelExperimentalPenalty implements ScoringFunction {
	private final ExecutorService pool;

	public ParallelExperimentalPenalty(final int size) {
		pool = MoreExecutors.getExitingExecutorService((ThreadPoolExecutor) Executors.newFixedThreadPool(size));
	}

	@Override
	public Type getType() {
		return Type.PENALTY;
	}

	@Override
	public double score(final Solution solution) {
		List<Future<Double>> jobs = Lists.newArrayList();
		for (final Section s : solution.getRun().getSections()) {
			jobs.add(pool.submit(new Callable<Double>() {
				@Override
				public Double call() throws Exception {
					ExperimentalPlacement placement = new ExperimentalPlacement(s);
					for (Event e : solution.getEvents()) {
						placement.place(e);
					}
					return placement.getPenalty();
				}
			}));
		}

		double score = 0;
		for (Future<Double> f : jobs) {
			try {
				score += f.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		return score;
	}
}
