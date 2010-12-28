package org.andrill.conop4j;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.andrill.conop4j.listeners.ProgressListener;
import org.andrill.conop4j.listeners.RanksListener;
import org.andrill.conop4j.listeners.SnapshotListener;
import org.andrill.conop4j.mutation.SharedMutator;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;

public class ParallelSimulation {
	private static final DecimalFormat D = new DecimalFormat("0.00");

	public static void main(final String[] args) throws Exception {
		// load the simulation configuration
		final Simulation config = new Simulation(new File(args[0]));
		int count = Integer.parseInt(args[1]);

		System.out.println("Starting a swarm of " + count + " CONOP instances using settings from " + args[0]);

		// create our shared mutator and ranks listener
		final SharedMutator shared = new SharedMutator();
		final RanksListener ranks = new RanksListener();

		ExecutorService pool = MoreExecutors.getExitingExecutorService((ThreadPoolExecutor) Executors
				.newFixedThreadPool(count));
		List<Future<Solution>> runs = Lists.newArrayList();
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			// setup CONOP4J
			final CONOP conop = new CONOP(config.getConstraints(), shared, config.getObjectiveFunction(),
					config.getSchedule());

			// add listeners
			conop.addListener(shared);
			conop.addListener(ranks);

			// add a progress indicator
			if (i == 0) {
				conop.addListener(new ProgressListener());
				conop.addListener(new SnapshotListener());
			}

			// find the optimal placement
			runs.add(pool.submit(new Callable<Solution>() {
				@Override
				public Solution call() throws Exception {
					Run run = config.getRun();
					return conop.solve(run, Solution.initial(run));
				}
			}));

		}

		Solution best = null;
		for (Future<Solution> f : runs) {
			Solution sol = f.get();
			if ((best == null) || (sol.getScore() < best.getScore())) {
				best = sol;
			}
		}
		long elapsed = (System.currentTimeMillis() - start) / 60000;

		System.out.println("Elapsed time: " + elapsed + " minutes.  Final score: " + D.format(best.getScore()));

		// write out the results
		Simulation.writeResults(best, ranks);
	}
}
