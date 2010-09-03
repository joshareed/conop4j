package org.andrill.conop4j;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
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
	protected final MutationStrategy mutation;
	protected final Random random;
	protected final CoolingSchedule schedule;
	protected final ScoringFunction scoring;

	/**
	 * Create a new simulated annealing solver.
	 * 
	 * @param constraints
	 *            the constraints.
	 * @param mutation
	 *            the mutation strategy.
	 * @param scoring
	 *            the scoring function.
	 * @param schedule
	 *            the cooling schedule.
	 */
	public CONOP(final ConstraintChecker constraints, final MutationStrategy mutation, final ScoringFunction scoring,
			final CoolingSchedule schedule) {
		this.constraints = constraints;
		this.mutation = mutation;
		this.scoring = scoring;
		this.schedule = schedule;
		random = new Random();
		listeners = new CopyOnWriteArraySet<Listener>();
		executor = MoreExecutors.getExitingExecutorService(new ThreadPoolExecutor(0, 1, 10, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>()), 1, TimeUnit.SECONDS);
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
		initial.setScore(scoring.score(initial));

		// anneal
		while (temp > 0) {
			// get a new solution that satisfies the constraints
			Solution next = mutation.mutate(current);
			while (!constraints.isValid(next)) {
				next = mutation.mutate(current);
			}

			// score this solution
			next.setScore(scoring.score(next));
			if (scoring.getType() == Type.PENALTY) {
				// save as best if the penalty is less
				if (next.getScore() < best.getScore()) {
					best = next;
				}

				// accept the new solution if it is better than the current or
				// randomly based on score and temperature
				if ((next.getScore() < current.getScore())
						|| (Math.exp(-Math.abs(next.getScore() - current.getScore()) / temp) > random.nextDouble())) {
					current = next;
				}
			} else {
				// save as best if the score is less
				if (next.getScore() > best.getScore()) {
					best = next;
				}

				// accept the new solution if it is better than the current or
				// randomly based on score and temperature
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
			temp = schedule.next(current);
		}
		return best;
	}
}
