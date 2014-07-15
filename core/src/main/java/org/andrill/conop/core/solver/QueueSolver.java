package org.andrill.conop.core.solver;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;

import org.andrill.conop.core.HaltedException;
import org.andrill.conop.core.Solution;
import org.andrill.conop.core.constraints.Constraints;
import org.andrill.conop.core.listeners.Listener;
import org.andrill.conop.core.mutators.Mutator;
import org.andrill.conop.core.penalties.Penalty;
import org.andrill.conop.core.schedules.Schedule;

public class QueueSolver extends AbstractSolver {

	private class ScorerThread extends Thread {
		private final Penalty objective;

		private ScorerThread(final Penalty objective) {
			this.objective = objective;
		}

		@Override
		public void run() {
			while (!stopped) {
				try {
					Solution next = work.take();
					next.setScore(objective.score(next));
					complete.put(next);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected LinkedBlockingQueue<Solution> work;
	protected LinkedBlockingQueue<Solution> complete;
	protected Set<ScorerThread> scorers;
	protected Constraints constraints;
	protected Mutator mutator;
	protected Schedule schedule;
	protected Random random = new Random();

	@Override
	protected void initialize(final SolverConfiguration config) {
		int procs = Runtime.getRuntime().availableProcessors();
		work = new LinkedBlockingQueue<>(procs + 1);
		complete = new LinkedBlockingQueue<>(procs + 1);

		// save our important components
		constraints = config.getConstraints();
		mutator = config.getMutator();
		schedule = config.getSchedule();

		// check for listeners
		if (constraints instanceof Listener) {
			addListener((Listener) constraints);
		}
		if (mutator instanceof Listener) {
			addListener((Listener) mutator);
		}
		if (schedule instanceof Listener) {
			addListener((Listener) schedule);
		}

		// create our scorer threads
		scorers = new HashSet<>();
		for (int i = 0; i < procs; i++) {
			Penalty penalty = config.getPenalty();
			scorers.add(new ScorerThread(penalty));
			if (penalty instanceof Listener) {
				addListener((Listener) penalty);
			}
		}

		// other listeners
		for (Listener l : config.getListeners()) {
			addListener(l);
		}
	}

	@Override
	protected Solution solve(final Solution initial) {
		Solution current = initial;

		// get our initial temperature and score
		double temp = schedule.getInitial();
		initial.setScore(Double.MAX_VALUE);
		started(initial);

		// fill the work queue
		for (int i = 0; i < scorers.size(); i++) {
			Solution next = mutator.mutate(current);
			while (!constraints.isValid(next)) {
				next = mutator.mutate(current);
			}
			try {
				work.put(next);
			} catch (InterruptedException e) {
				// ignore
			}
		}

		// start all scorer threads
		for (ScorerThread thread : scorers) {
			thread.start();
		}

		try {
			// anneal
			while (temp > 0) {
				Solution next = complete.take();

				// save as best if the penalty is less
				if (next.getScore() < best.getScore()) {
					best = next;

					if (best.getScore() == 0) {
						throw new RuntimeException("Score reached 0");
					}
				}

				// notify listeners
				for (Listener l : listeners) {
					l.tried(temp, current, best);
				}

				// accept the new solution if it is better than the current
				// or randomly based on score and temperature
				if ((next.getScore() < current.getScore()) || (Math.exp(-(next.getScore() - current.getScore()) / temp) > random.nextDouble())) {
					current = next;
				}

				// get our next temperature
				temp = schedule.next(current);

				// get a new solution that satisfies the constraints
				next = mutator.mutate(current);
				while (!constraints.isValid(next)) {
					next = mutator.mutate(current);
				}
				work.put(next);
			}
		} catch (Exception e) {
			HaltedException halt;
			if (e instanceof HaltedException) {
				halt = (HaltedException) e;
			} else if ((e instanceof InterruptedException) || (e instanceof RejectedExecutionException)) {
				halt = new HaltedException("User Interrupt");
			} else {
				halt = new HaltedException("Unexpected Error: " + e.getMessage());
			}
			stopped(best);
			throw halt;
		}

		// clean up
		stopped(best);
		return best;
	}

	@Override
	protected void stopped(final Solution solution) {
		for (ScorerThread thread : scorers) {
			thread.interrupt();
		}

		super.stopped(solution);
	}
}
