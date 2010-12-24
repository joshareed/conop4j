package org.andrill.conop4j;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.andrill.conop4j.constraints.ConstraintChecker;
import org.andrill.conop4j.mutation.MutationStrategy;
import org.andrill.conop4j.schedule.CoolingSchedule;
import org.andrill.conop4j.scoring.ScoringFunction;
import org.andrill.conop4j.scoring.ScoringFunction.Type;

import com.google.common.util.concurrent.MoreExecutors;

/**
 * A Java implementation of simulated annealing.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class CONOP {

	/**
	 * Notified when new solutions are tried.
	 */
	public interface Listener {

		/**
		 * Called when a solution is tried.
		 * 
		 * @param temp
		 *            the temperature.
		 * @param current
		 *            the current solution.
		 * @param best
		 *            the current best solution.
		 */
		void tried(double temp, Solution current, Solution best);
	}

	private static class NotifyListenersTask implements Runnable {
		private final Solution best;
		private final Solution current;
		private final Set<Listener> listeners;
		private final double temp;

		private NotifyListenersTask(final Set<Listener> listeners, final double temp, final Solution current,
				final Solution best) {
			this.listeners = listeners;
			this.current = current;
			this.best = best;
			this.temp = temp;
		}

		@Override
		public void run() {
			for (Listener l : listeners) {
				l.tried(temp, current, best);
			}
		}
	}

	protected final ConstraintChecker constraints;
	protected final ExecutorService executor;
	protected final Set<Listener> listeners;
	protected final MutationStrategy mutator;
	protected final Random random;
	protected final CoolingSchedule schedule;
	protected final ScoringFunction scorer;

	/**
	 * Create a new simulated annealing solver.
	 * 
	 * @param constraints
	 *            the constraints.
	 * @param mutator
	 *            the mutation strategy.
	 * @param scorer
	 *            the scoring function.
	 * @param schedule
	 *            the cooling schedule.
	 */
	public CONOP(final ConstraintChecker constraints, final MutationStrategy mutator, final ScoringFunction scorer,
			final CoolingSchedule schedule) {
		this.constraints = constraints;
		this.mutator = mutator;
		this.scorer = scorer;
		this.schedule = schedule;
		random = new Random();
		listeners = new CopyOnWriteArraySet<Listener>();
		int size = Runtime.getRuntime().availableProcessors() + 1;
		executor = MoreExecutors.getExitingExecutorService((ThreadPoolExecutor) Executors.newFixedThreadPool(size));
	}

	/**
	 * Add a new listener.
	 * 
	 * @param l
	 *            the listener.
	 */
	public void addListener(final Listener l) {
		listeners.add(l);
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

	/**
	 * Solve the constrained optimization problem.
	 * 
	 * @param run
	 *            the run data.
	 * @param initial
	 *            the initial solution.
	 * @return the best solution.
	 */
	public Solution solve(final Run run, final Solution initial) {
		Solution best = initial;
		Solution current = initial;

		// get our initial temperature and score
		double temp = schedule.getInitial();
		initial.setScore(scorer.score(initial));

		try {
			// anneal
			while (temp > 0) {
				// get a new solution that satisfies the constraints
				Solution next = mutator.mutate(current);
				while (!constraints.isValid(next)) {
					next = mutator.mutate(current);
				}

				// score this solution
				next.setScore(scorer.score(next));
				if (scorer.getType() == Type.PENALTY) {
					// save as best if the penalty is less
					if (next.getScore() < best.getScore()) {
						best = next;
					}

					// accept the new solution if it is better than the current
					// or randomly based on score and temperature
					if ((next.getScore() < current.getScore())
							|| (Math.exp(-Math.abs(next.getScore() - current.getScore()) / temp) > random.nextDouble())) {
						current = next;
					}
				} else {
					// save as best if the score is more
					if (next.getScore() > best.getScore()) {
						best = next;
					}

					// accept the new solution if it is better than the current
					// or randomly based on score and temperature
					if ((next.getScore() > current.getScore())
							|| (Math.exp(-Math.abs(next.getScore() - current.getScore()) / temp) > random.nextDouble())) {
						current = next;
					}
				}

				// notify listeners asynchronously
				if (listeners.size() > 0) {
					executor.submit(new NotifyListenersTask(listeners, temp, current, best));
				}

				// get our next temperature
				if (best.getScore() == 0) {
					throw new RuntimeException("Score reached 0");
				}
				temp = schedule.next(current);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// shut down executor and wait up to 30 seconds for listeners to finish
		try {
			executor.shutdown();
			executor.awaitTermination(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// do nothing
		}

		return best;
	}
}
