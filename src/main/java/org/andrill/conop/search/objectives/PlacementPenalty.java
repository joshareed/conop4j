package org.andrill.conop.search.objectives;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.andrill.conop.search.AbstractConfigurable;
import org.andrill.conop.search.Section;
import org.andrill.conop.search.Solution;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * A parallel version of {@link PlacementPenalty}.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class PlacementPenalty extends AbstractConfigurable implements ObjectiveFunction {
	protected final Map<Section, SectionPlacement> placements = Maps.newHashMap();
	protected ExecutorService pool;
	protected int procs = 1;

	@Override
	public void configure(final Properties properties) {
		this.procs = Integer.parseInt(properties.getProperty("processors", "1"));
		pool = MoreExecutors.getExitingExecutorService((ThreadPoolExecutor) Executors.newFixedThreadPool(procs));
	}

	protected Future<Double> execute(final SectionPlacement placement, final Solution solution) {
		if (pool == null) {
			return Futures.immediateFuture(placement.score(solution));
		} else {
			return pool.submit(new Callable<Double>() {
				@Override
				public Double call() throws Exception {
					return placement.score(solution);
				}
			});
		}
	}

	@Override
	public double score(final Solution solution) {
		List<Future<Double>> results = Lists.newArrayList();
		for (final Section section : solution.getRun().getSections()) {
			SectionPlacement placement = placements.get(section);
			if (placement == null) {
				placement = new SectionPlacement(section);
				placements.put(section, placement);
			}
			results.add(execute(placement, solution));
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
	public String toString() {
		return "Placement [" + procs + "]";
	}
}
